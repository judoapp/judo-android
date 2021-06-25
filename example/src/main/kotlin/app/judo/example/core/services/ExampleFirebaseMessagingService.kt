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