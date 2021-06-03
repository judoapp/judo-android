package app.judo.sdk.core.implementations

import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.services.ExperienceService
import okhttp3.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
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
            @Header("Cache-Control") cacheControlHeader: String?
        ): Response<Experience>
    }

    private val client: OkHttpClient by lazy {
        clientSupplier().newBuilder().apply {
            cache(
                Cache(
                    File(File(cachePathSupplier()), cacheName),
                    cacheSizeSupplier()
                )
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

}

