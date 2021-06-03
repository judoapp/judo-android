package app.judo.sdk.core.implementations

import app.judo.sdk.core.data.RegistrationRequestBody
import app.judo.sdk.core.data.RegistrationResponse
import app.judo.sdk.core.services.DevicesService
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT

class DevicesServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val baseURLSupplier: () -> String?
) : DevicesService {

    companion object {
        const val url: String = "https://devices.judo.app"
    }

    interface DevicesAPI {
        @PUT("/register")
        suspend fun register(@Body body: RegistrationRequestBody): Response<RegistrationResponse>
    }

    private val api by lazy {

        Retrofit.Builder()
            .baseUrl(baseURLSupplier() ?: url)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(baseClientSupplier())
            .build().create(DevicesAPI::class.java)
    }

    override suspend fun register(body: RegistrationRequestBody): Response<RegistrationResponse> {
        return api.register(body)
    }
}
