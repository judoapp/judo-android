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

package app.judo.sdk.api.analytics

import android.os.Build
import com.squareup.moshi.JsonClass
import java.util.*

interface AnalyticsEvent {
    val id: String
    val anonymousID: String
    val userID: String?
    val timestamp: String
    val type: String
    val context: Context

    @JsonClass(generateAdapter = true)
    data class Context(
        val device: Device,
        val locale: String,
        val os: OS = OS()
    ) {
        @JsonClass(generateAdapter = true)
        data class Device(
            /**
             * The device identifier generated at SDK initialization time.
             */
            val id: String,

            /**
             * The Firebase FCM push token.
             */
            val token: String?
        )

        @JsonClass(generateAdapter = true)
        data class OS(
            val name: String = "Android",

            /**
             * The Android version number.
             */
            val version: String = Build.VERSION.RELEASE
        )
    }

    @JsonClass(generateAdapter = true)
    data class Screen(
        override val id: String,
        override val anonymousID: String,
        override val userID: String?,
        override val timestamp: String,
        override val context: Context,
        val properties: Properties
    ): AnalyticsEvent {
        override val type: String
            get() = "screen"

        @JsonClass(generateAdapter = true)
        data class Properties(
            /**
             * The Node ID of the Screen.
             */
            val id: String,
            /**
             * The name of the Screen.
              */
            val name: String,
            val experienceID: String,
            val experienceName: String,
            val experienceRevisionID: String
        )
    }

    @JsonClass(generateAdapter = true)
    data class Identify(
        override val id: String,
        override val anonymousID: String,
        override val userID: String?,
        override val timestamp: String,
        override val context: Context,
        val traits: Map<String, Any>
    ): AnalyticsEvent {
        override val type: String
            get() = "identify"
    }

    /**
     * This event is tracked when the Push Token is updated, to ensure the most recent Context is
     * delivered to the Ingest API.
     */
    @JsonClass(generateAdapter = true)
    class Register(
        override val id: String,
        override val anonymousID: String,
        override val userID: String?,
        override val timestamp: String,
        override val context: Context
    ): AnalyticsEvent {
        override val type: String
            get() = "register"
    }
}
