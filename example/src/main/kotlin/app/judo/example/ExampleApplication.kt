package app.judo.example

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.work.*
import app.judo.sdk.BuildConfig
import app.judo.sdk.api.Judo
import app.judo.sdk.api.logs.LogLevel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ExampleApplication : MultiDexApplication() {

    companion object {
        private const val TAG = "ExampleApplication"
        private const val RESULT_CODE_SUCCESS: Long = 1L
        private const val RESULT_CODE_FAILURE: Long = 0L
        private const val EXPERIENCE_CACHE_SIZE: Long = 50L * 1024 * 1024 // 10MB
        private const val IMAGE_CACHE_SIZE: Long = 50L * 1024 * 1024 // 10MB
    }

    private val accessToken: String = "yourToken"
    private val domains = arrayOf("brand1.judo.app", "brand2.judo.app")

    private lateinit var analytics: FirebaseAnalytics
    private lateinit var messaging: FirebaseMessaging

    override fun onCreate() {
        super.onCreate()

        analytics = FirebaseAnalytics.getInstance(this)
        messaging = FirebaseMessaging.getInstance()

        val success: Long = try {

            if (app.judo.example.BuildConfig.DEBUG) {
                Judo.logLevel = LogLevel.Verbose
            }

            Judo.initialize(
                application = this,
                accessToken = accessToken,
                experienceCacheSize = EXPERIENCE_CACHE_SIZE,
                imageCacheSize = IMAGE_CACHE_SIZE,
                domains = domains,
            )

            Judo.performSync(prefetchAssets = false) {
                Log.d(TAG, "Experience sync completed")
            }

            messaging.token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                task.result?.let { fcmToken ->
                    // Log
                    Log.d(TAG, "FCM registration token retrieved: $fcmToken")

                    Judo.setPushToken(fcmToken = fcmToken)
                }
            }

            val judoWorkRequest =
                PeriodicWorkRequest.Builder(JudoSyncWorker::class.java, 1, TimeUnit.HOURS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.UNMETERED)
                            .setRequiresCharging(true)
                            .build(),
                    )
                    .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "judo-sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                judoWorkRequest
            )

            RESULT_CODE_SUCCESS
        } catch (error: Throwable) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, error.message, error)
            }
            RESULT_CODE_FAILURE
        }

        analytics.logEvent("JUDO_INITIALIZED") {
            param(FirebaseAnalytics.Param.SUCCESS, success)
        }
    }
}

class JudoSyncWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    companion object {
        private const val TAG = "JudoSyncWorker"
    }

    override fun doWork(): Result {
        val latch = CountDownLatch(1)

        Judo.performSync {
            latch.countDown()
        }

        try {
            latch.await()
        } catch (error: Exception) {
            Log.e(TAG, "Unable to complete background Judo sync")
            return Result.failure()
        }
        return Result.success()
    }
}
