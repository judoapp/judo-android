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

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import app.judo.compose.model.values.HttpMethod
import app.judo.compose.ui.*
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.URLRequest
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.ui.events.ExperienceRequested
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

private fun handleScreenViewed(
    scope: CoroutineScope,
    trackScreenEvent: TrackScreenEvent
) {
    val experience = Experience(
        id = trackScreenEvent.experienceID ?: "",
        version = 0,
        revisionID = "",
        name = trackScreenEvent.experienceName ?: "",
        nodes = emptyList(),
        screenIDs = emptyList(),
        initialScreenID = "",
        appearance = Appearance.AUTO,
        fonts = emptyList(),
        localization = emptyMap(),
    )
    val screen = Screen(
        id = trackScreenEvent.screen.id,
        metadata = Metadata(
            trackScreenEvent.screen.metadata?.tags ?: emptySet(),
            trackScreenEvent.screen.metadata?.properties ?: emptyMap()
        ),
        childIDs = emptyList(),
        name = trackScreenEvent.screen.name,
        backgroundColor = ColorVariants(default = Color(0f, 0f, 0f, 0f)),
        androidStatusBarBackgroundColor = ColorVariants(default = Color(0f, 0f, 0f, 0f)),
        androidStatusBarStyle = StatusBarStyle.DEFAULT,
        frame = null
    )

    val dataContext: Map<String, Any?> = hashMapOf(
        "url" to trackScreenEvent.urlParameters,
        "data" to trackScreenEvent.data,
        "userInfo" to Environment.current.profileService.userInfo
    )

    scope.launch {
        Environment.current.eventBus.publish(
            Event.ScreenViewed(
                experience = experience,
                screen = screen,
                dataContext = dataContext,
                trackScreenEvent.data,
                trackScreenEvent.urlParameters,
                Environment.current.profileService.userInfo
            )
        )
    }
}

/**
 * Handle a custom action callback from the Judo Compose SDK by dispatching
 * a custom action event on the Judo Android SDK's event bus.
 */
private fun handleCustomAction(
    activity: Activity,
    action: CustomActionActivation,
    scope: CoroutineScope
) {
    // synthesize model objects from the Judo Android SDK using data from
    // the Judo Compose SDK:
    val experience = Experience(
        id = action.experienceID ?: "",
        version = 0,
        revisionID = "",
        name = action.experienceName ?: "",
        nodes = emptyList(),
        screenIDs = emptyList(),
        initialScreenID = "",
        appearance = Appearance.AUTO,
        fonts = emptyList(),
        localization = emptyMap(),
    )
    val screen = Screen(
        id = action.screen.id,
        metadata = Metadata(
            action.screen.metadata?.tags ?: emptySet(),
            action.screen.metadata?.properties ?: emptyMap()
        ),
        childIDs = emptyList(),
        name = action.screen.name,
        backgroundColor = ColorVariants(default = Color(0f, 0f, 0f, 0f)),
        androidStatusBarBackgroundColor = ColorVariants(default = Color(0f, 0f, 0f, 0f)),
        androidStatusBarStyle = StatusBarStyle.DEFAULT,
        frame = null
    )
    val node = ErasedNode(
        id = action.node.id,
        name = action.node.name,
        metadata = Metadata(
            action.node.metadata?.tags ?: emptySet(),
            action.node.metadata?.properties ?: emptyMap()
        ),
    )
    scope.launch {
        Environment.current.eventBus.publish(
            Event.CustomActionActivationEvent(
                node = node,
                screen = screen,
                experience = experience,
                metadata = node.metadata,
                data = action.data,
                urlParameters = action.urlParameters,
                userInfo = Environment.current.profileService.userInfo,
                activity = activity
            )
        )
    }
}

/**
 * Convert a Judo-Compose URLRequest to this SDK's URLRequest.
 */
private fun app.judo.compose.ui.URLRequest.toAndroidUrlRequest(): URLRequest = URLRequest(
    url = url,
    method = when (method) {
        HttpMethod.GET -> app.judo.sdk.api.models.HttpMethod.GET
        HttpMethod.PUT -> app.judo.sdk.api.models.HttpMethod.PUT
        HttpMethod.POST -> app.judo.sdk.api.models.HttpMethod.POST
    },
    headers = headers,
    body = body
)

/**
 * Update a Compose SDK's URLRequest with values from this SDK's URLRequest.
 */
private fun app.judo.compose.ui.URLRequest.updateFrom(urlRequest: URLRequest) {
    url = urlRequest.url
    method = when (urlRequest.method) {
        app.judo.sdk.api.models.HttpMethod.GET -> HttpMethod.GET
        app.judo.sdk.api.models.HttpMethod.PUT -> HttpMethod.PUT
        app.judo.sdk.api.models.HttpMethod.POST -> HttpMethod.POST
    }
    headers = urlRequest.headers
    body = urlRequest.body
}
