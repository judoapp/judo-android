package app.judo.sdk.core.implementations

import android.graphics.Typeface
import android.os.Build
import app.judo.sdk.api.models.FontResource
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.services.FontResourceService
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

                        val response = api.getFile(resource.url, cacheControlHeader)

                        val body = response.body()?.bytes()

                        val pathname = cachePathSupplier()

                        val file = File("$pathname/fontFamily.${resource.url.takeLast(3)}")

                        body?.let { bytes ->

                            file.writeBytes(bytes)

                            val builder = Typeface.Builder(file)

                            resource.names.forEachIndexed { index, name ->
                                builder.setTtcIndex(index)
                                builder.build()?.let { face ->
                                    result[name] = face
                                }
                            }
                        }

                        file.delete()

                    }
                }

                is FontResource.Single -> {

                    val response = api.getFile(resource.url, cacheControlHeader)

                    val body = response.body()?.bytes()

                    val pathname = cachePathSupplier()

                    val file = File("$pathname/${resource.name}.${resource.url.takeLast(3)}")

                    body?.let { bytes ->
                        file.writeBytes(bytes)

                        val face = Typeface.createFromFile(file)

                        result.put(resource.name, face)
                    }

                    file.delete()

                }

            }
        }

        return result
    }

}
