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

import app.judo.sdk.BuildConfig
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.services.ExperienceService
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.File

internal class ExperienceServiceImpl(
    private val cachePathSupplier: () -> String,
    private val baseURLSupplier: () -> String?,
    private val cacheSizeSupplier: () -> Long,
    clientSupplier: () -> OkHttpClient,
) : ExperienceService {

    companion object {
        const val cacheName: String = "judo_experience_cache"

        /**
         * Dummy URL for Retrofit purposes, it never gets used.
         */
        const val url: String = "https://127.0.0.1/"
    }

    interface ExperienceAPI {
        @GET
        suspend fun getExperience(
            @Url aURL: String,
            @Header("Cache-Control") cacheControlHeader: String?,
            @Query("apiVersion") apiVersion: Int = BuildConfig.API_VERSION,
        ): Response<Experience>
    }

    private val cache by lazy {

        Cache(
            File(File(cachePathSupplier()), cacheName),
            cacheSizeSupplier()
        )

    }

    private val client: OkHttpClient by lazy {
        clientSupplier().newBuilder().apply {
            cache(
                cache
            )
        }.build()
    }

    private val api: ExperienceAPI by lazy {
        val base: String =
            this@ExperienceServiceImpl.baseURLSupplier()?.takeIf { it.isNotBlank() } ?: url

        Retrofit.Builder().apply {
            baseUrl(base)
            addConverterFactory(MoshiConverterFactory.create(JsonParser.moshi))
            client(client)
        }.build().create(ExperienceAPI::class.java)
    }

    override suspend fun getExperience(
        aURL: String,
        skipCache: Boolean,
    ): Response<Experience> {
        val sanitizedURL = baseURLSupplier()?.let { baseURL ->
            aURL.replaceFirst(Regex("""^(https://|http://)""", RegexOption.IGNORE_CASE), baseURL)
        } ?: aURL

        val cacheControlHeader = if (skipCache) "no-cache" else null

        return api.getExperience(sanitizedURL, cacheControlHeader)
    }

    override suspend fun delete(aURL: String) {

        @Suppress(
            "BlockingMethodInNonBlockingContext"
        )
        val iterator =
            cache.urls()

        var notRemoved = true

        while (notRemoved && iterator.hasNext()) {

            val nextURL =
                iterator.next()

            if (nextURL == aURL) {

                iterator.remove()

                notRemoved = false

            }

        }

    }

}
