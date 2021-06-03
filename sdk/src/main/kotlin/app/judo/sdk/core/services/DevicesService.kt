package app.judo.sdk.core.services

import app.judo.sdk.core.data.RegistrationRequestBody
import app.judo.sdk.core.data.RegistrationResponse
import retrofit2.Response

internal interface DevicesService {

    suspend fun register(body: RegistrationRequestBody): Response<RegistrationResponse>

}