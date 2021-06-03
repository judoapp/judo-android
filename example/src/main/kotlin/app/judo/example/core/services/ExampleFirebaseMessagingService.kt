package app.judo.example.core.services

import android.util.Log
import app.judo.example.BuildConfig
import app.judo.sdk.api.Judo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ExampleFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "ExampleFMS"
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Notification received:\n\t${p0.data}")
        }

        Judo.onFirebaseRemoteMessageReceived(data = p0.data)

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "New token received:\n\t$p0")
        }

        Judo.setPushToken(
            fcmToken = p0
        )

    }

}