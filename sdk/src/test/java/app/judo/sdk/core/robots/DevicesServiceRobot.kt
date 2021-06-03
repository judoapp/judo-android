package app.judo.sdk.core.robots

import app.judo.sdk.core.data.RegistrationRequestBody
import app.judo.sdk.core.data.RegistrationResponse
import app.judo.sdk.core.environment.Environment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Response

@ExperimentalCoroutinesApi
internal class DevicesServiceRobot : AbstractTestRobot() {

    suspend fun register(registrationRequestBody: RegistrationRequestBody): Response<RegistrationResponse> {
        return environment.devicesService.register(registrationRequestBody)
    }

    fun getRequestHeader(name: String): String? {
        return serverDispatcher.actualRequest?.getHeader(name)
    }

    fun setDeviceIdTo(deviceId: String) {
        environment.keyValueCache.putString(Environment.Keys.DEVICE_ID to deviceId)
    }


}
