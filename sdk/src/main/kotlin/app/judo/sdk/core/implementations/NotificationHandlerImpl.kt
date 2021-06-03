package app.judo.sdk.core.implementations

import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.RegistrationRequestBody
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.notifications.NotificationHandler


internal class NotificationHandlerImpl(
    private val environment: Environment
) : NotificationHandler {

    companion object {
        private const val TAG = "NotificationHandler"
    }

    private object ACTIONS {
        const val SYNC: String = "SYNC"
    }

    override suspend fun handleRemoteMessagingData(data: Map<String, String>) {
        try {
            data[Environment.Keys.MESSAGE]?.let { json ->
                JsonParser.parseJudoMessage(json)?.let { judoMessage ->

                    environment.logger.i(TAG, "Sync message received: $judoMessage")

                    when (judoMessage.action) {
                        ACTIONS.SYNC -> {
                            environment.logger.d(TAG, "Triggering action: ${ACTIONS.SYNC}")
                            SynchronizerImpl(environment).performSync(prefetchAssets = true) {
                                environment.logger.d(TAG, "Sync Completed")
                            }
                        }
                        else -> {
                            environment.logger.e(
                                TAG,
                                null,
                                IllegalArgumentException("Invalid Action: ${judoMessage.action}")
                            )
                        }
                    }
                }
            }
        } catch (error: Throwable) {
            environment.logger.e(
                TAG,
                "Failed to handle notification:",
                error
            )
        }
    }

    override suspend fun setPushToken(fcmToken: String) {
        environment.keyValueCache.retrieveString(Environment.Keys.DEVICE_ID)?.let { deviceId ->

            val requestBody = RegistrationRequestBody(
                deviceID = deviceId,
                deviceToken = fcmToken,
                environment = Environment.Type
            )

            val response = environment.devicesService.register(requestBody)

            if (response.isSuccessful && response.body() != null) {
                environment.logger.i(TAG, "Push token Set")
            } else {
                environment.logger.e(
                    TAG,
                    "Failed to set push token: ${response.message()}\nCode: ${response.code()}"
                )
            }

        }
    }

}
