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

package app.judo.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.work.*
import app.judo.sdk.BuildConfig
import app.judo.sdk.api.Judo
import app.judo.sdk.api.logs.LogLevel
import app.judo.sdk.api.models.Action
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

            Judo.setUserInfoSupplier {
                hashMapOf(
                    "firstName" to "Jane",
                    "lastName" to "Doe",
                )
            }

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

        // you may also subscribe to events from the Judo SDK, namely, be notified whenever a screen is Viewed, or when an action is received (aka, a button tapped).

        Judo.addScreenViewedCallback { event ->
            // a common use case is to notify your own Analytics tooling that a Judo screen has been
            // displayed.
            Log.i(TAG, "Judo Screen Viewed: ${event.screen.name}")
        }

        Judo.addActionReceivedCallback { event ->
            // a possible use case for this is handling the "Custom" action type with your own behaviour.
            if(event.action is Action.Custom) {
                // interrogate event.node, event.screen, etc. to determine which behaviour you wish to invoke.
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.judo.app"))
                startActivity(intent)
            }

            // equally, you may do other tasks here, such as log an event in your own Analytics tooling.
        }

        // if you like, notify your analytics that you've completed starting the Judo SDK.
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
