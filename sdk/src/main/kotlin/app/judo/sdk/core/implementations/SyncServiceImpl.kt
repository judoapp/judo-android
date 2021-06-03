package app.judo.sdk.core.implementations

import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.services.SyncService
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

internal class SyncServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val baseURLSupplier: () -> String?
) : SyncService {

    companion object {
        const val url: String = "https://127.0.0.1"
    }

    interface SyncAPI {
        @GET
        suspend fun getSync(@Url aURL: String): Response<SyncResponse>
    }

    private val api by lazy {

        Retrofit.Builder()
            .baseUrl(baseURLSupplier() ?: url)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(baseClientSupplier())
            .build().create(SyncAPI::class.java)
    }

    override suspend fun getSync(aURL: String): Response<SyncResponse> {
        val theURL = "${
            baseURLSupplier() ?: "https://"
        }${
            aURL.removePrefix("https://")
        }${
            "/sync".takeUnless { aURL.contains("/sync?") } ?: ""
        }"
        return api.getSync(aURL = theURL)
    }

}

