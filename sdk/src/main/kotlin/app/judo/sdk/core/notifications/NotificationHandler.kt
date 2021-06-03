package app.judo.sdk.core.notifications

interface NotificationHandler {
    suspend fun handleRemoteMessagingData(data: Map<String, String>)
    suspend fun setPushToken(fcmToken: String)
}
