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
    }

    private lateinit var analytics: FirebaseAnalytics
    private lateinit var messaging: FirebaseMessaging

    override fun onCreate() {
        super.onCreate()
        analytics = FirebaseAnalytics.getInstance(this)
        messaging = FirebaseMessaging.getInstance()

        if (app.judo.example.BuildConfig.DEBUG) {
            Judo.logLevel = LogLevel.Verbose
        }

        Judo.initialize(
            application = this,
            accessToken = "<ACCESS-TOKEN>",
            domain = "myapp.judo.app"
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

        // you may also subscribe to events from the Judo SDK, namely, be notified whenever a screen is Viewed, or when an action is received (aka, a button tapped).
        Judo.addScreenViewedCallback { event ->
            // a common use case is to notify your own Analytics tooling that a Judo screen has been
            // displayed.
            Log.i(TAG, "Judo Screen Viewed: ${event.screen.name}")

            // Examples for a few common Analytics products follows:

            // Braze:
            //   val eventProperties = AppboyProperties()
            //   eventProperties.addProperty("name", "Judo / ${event.experience.name} / ${event.screen.name}")
            //   Appboy.getInstance(context).logCustomEvent("Screen Viewed", eventProperties)

            // Segment:
            //   Analytics.with(context).screen("Judo / ${event.experience.name} / ${event.screen.name}")

            // Amplitude:
            //   val eventProperties = JSONObject()
            //   try {
            //       eventProperties.put(
            //           "name",
            //           "Judo / ${event.experience.name} / ${event.screen.name}"
            //       )
            //   } catch (e: JSONException) {
            //       System.err.println("Invalid JSON")
            //       e.printStackTrace()
            //   }
            //   amplitudeClient.logEvent("Screen Viewed", eventProperties)

            // Firebase Analytics:
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(
                    FirebaseAnalytics.Param.SCREEN_NAME,
                    "Judo / ${event.experience.name} / ${event.screen.name}"
                )
            }
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
