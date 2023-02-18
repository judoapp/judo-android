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

package app.judo.sdk.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import app.judo.sdk.compose.ui.*
import app.judo.sdk.api.models.*
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.ui.JudoComposeHelpers.handleCustomAction
import app.judo.sdk.ui.JudoComposeHelpers.handleScreenViewed
import app.judo.sdk.ui.JudoComposeHelpers.toAndroidUrlRequest
import app.judo.sdk.ui.JudoComposeHelpers.updateFrom
import app.judo.sdk.ui.events.ExperienceRequested

/**
 * This Activity displays Judo Experiences.
 *
 * Note: As of 1.9, this uses our new Jetpack Compose-based experience renderer. If you need
 * to continue to use the old renderer, see [LegacyExperienceActivity].
 */
open class ExperienceActivity() : ComponentActivity() {
    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val request = ExperienceRequested(intent)

        if (request.loadFromMemory) {
            Log.e(tag, "loadFromMemory not supported. Use LegacyExperienceActivity if you need it, or adopt judo-compose directly.")
            return
        }

        val uriBuilder = Uri.parse(request.experienceURL).buildUpon()
        request.screenId?.let {
            uriBuilder.appendQueryParameter("screenID", it)
        }
        val uri = uriBuilder.build()

        val uriScheme = uri?.scheme?.lowercase()

        val experienceModifier = Modifier.judoCustomAction { action ->
            handleCustomAction(
                this@ExperienceActivity,
                action,
                lifecycleScope
            )
        }.judoTrackScreen { screenEvent ->
            handleScreenViewed(lifecycleScope, screenEvent)
        }.judoAuthorize { urlRequest ->
            // we have to map to this SDK's equivalent URLRequest type, and then bring
            // the changed values back over.
            val androidSdkUrlRequest = urlRequest.toAndroidUrlRequest()

            Environment.current.configuration.authorizers.forEach { authorizer ->
                authorizer.authorize(androidSdkUrlRequest)
            }
            urlRequest.updateFrom(androidSdkUrlRequest)
        }
        setContent {
            if (uri == null || uri.scheme == null) {
                Text("Something went wrong.")
            } else {
                if (uriScheme == "file" || uriScheme == "content") {
                    Experience(
                        fileUrl = uri,
                        userInfo = request.userInfo?.let { userInfo ->
                            { userInfo }
                        } ?: { Environment.current.profileService.userInfo },
                        modifier = experienceModifier
                    )
                } else {
                    AsyncExperience(
                        url = uri,
                        userInfo = request.userInfo?.let { userInfo ->
                            { userInfo }
                        } ?: { Environment.current.profileService.userInfo },
                        modifier = experienceModifier
                    )
                }
            }
        }
    }

    @Deprecated("Unused")
    open fun navigateToExperienceFragment() {
        /* no-op for API stability */
    }
}
