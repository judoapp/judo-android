/*
 * Copyright (c) 2020-present, Rover Labs, Inc. All rights reserved.
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Rover.
 *
 * This copyright notice shall be included in all copies or substantial portions of
 * the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package app.judo.sdk.core.implementations

import android.graphics.Typeface
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import app.judo.sdk.api.models.FontResource
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.services.FontResourceService
import app.judo.sdk.ui.extensions.toUri
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url
import java.io.File

internal class FontResourceServiceImpl(
    private val cachePathSupplier: () -> String,
    private val baseURLSupplier: () -> String?,
    clientSupplier: () -> OkHttpClient,
) : FontResourceService {

    companion object {
        const val cacheName: String = "font_resource_http_cache"
        const val maxCacheSize = 50L * 1024L * 1024L // 10 MiB
        const val url: String = "https://127.0.0.1/"
    }

    interface ResourceAPI {
        @GET
        suspend fun getFile(
            @Url aURL: String,
            @Header("Cache-Control") cacheControlHeader: String?,
        ): Response<ResponseBody>
    }

    private val client: OkHttpClient by lazy {
        clientSupplier().newBuilder().apply {
            cache(
                Cache(
                    File(File(cachePathSupplier()), cacheName),
                    maxCacheSize
                )
            )
        }.build()
    }

    private val api: ResourceAPI by lazy {

        val base: String = this@FontResourceServiceImpl.baseURLSupplier()?.takeIf { it.isNotBlank() } ?: url

        Retrofit.Builder().apply {
            baseUrl(base)
            addConverterFactory(MoshiConverterFactory.create(JsonParser.moshi))
            client(client)
        }.build().create(ResourceAPI::class.java)
    }

    override suspend fun getTypefacesFor(fonts: List<FontResource>, ignoreCache: Boolean): Map<String, Typeface> {

        val result = mutableMapOf<String, Typeface>()

        val cacheControlHeader = if (ignoreCache) "no-cache" else null
        fonts.forEach { resource ->
            when (resource) {

                is FontResource.Collection -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        if (resource.url.isLocalFileUri()) {

                            resource.url.toUri().path?.let { path ->
                                val localFile = File(path)

                                result.putAll(typefacesFromTTCFile(localFile, resource))
                            }
                        } else {
                            val response = api.getFile(resource.url, cacheControlHeader)

                            val body = response.body()?.bytes()

                            val pathname = cachePathSupplier()

                            val file = File("$pathname/fontFamily.${resource.url.substringAfterLast(".")}")

                            body?.let { bytes ->

                                file.writeBytes(bytes)

                                result.putAll(typefacesFromTTCFile(file, resource))
                            }

                            file.delete()
                        }
                    }
                }

                is FontResource.Single -> {

                    if (resource.url.isLocalFileUri()) {
                        resource.url.toUri().path?.let { path ->
                            val localFile = File(path)

                            val face = Typeface.createFromFile(localFile)

                            result[resource.name] = face
                        }
                    } else {

                        val response = api.getFile(resource.url, cacheControlHeader)

                        val body = response.body()?.bytes()

                        val pathname = cachePathSupplier()

                        val file = File("$pathname/${resource.name}.${resource.url.substringAfterLast(".")}")

                        body?.let { bytes ->
                            file.writeBytes(bytes)

                            val face = Typeface.createFromFile(file)

                            result.put(resource.name, face)
                        }

                        file.delete()

                    }
                }
            }
        }

        return result
    }

    private fun String.isLocalFileUri() = this.startsWith("file://")

    @RequiresApi(Build.VERSION_CODES.O)
    private fun typefacesFromTTCFile(file: File, resource: FontResource.Collection): Map<String, Typeface> {

        val builder = Typeface.Builder(file)

        val typefacesMap = mutableMapOf<String, Typeface>()

        resource.names.forEachIndexed { index, name ->
            builder.setTtcIndex(index)
            builder.build()?.let { face ->
                typefacesMap[name] = face
            }
        }

        return typefacesMap

    }

}
