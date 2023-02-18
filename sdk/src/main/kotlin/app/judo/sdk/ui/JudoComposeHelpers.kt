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
import app.judo.sdk.compose.model.values.HttpMethod
import app.judo.sdk.compose.ui.CustomActionActivation
import app.judo.sdk.compose.ui.TrackScreenEvent
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.models.*
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * These routines assist with bridging between the Judo Compose SDK's API and the Judo Android
 * SDK's API.
 */
internal object JudoComposeHelpers {
    internal fun handleScreenViewed(
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
            initialScreenID = ""
        )
        val screen = Screen(
            id = trackScreenEvent.screen.id,
            metadata = Metadata(
                trackScreenEvent.screen.metadata?.tags ?: emptySet(),
                trackScreenEvent.screen.metadata?.properties ?: emptyMap()
            ),
            childIDs = emptyList(),
            name = trackScreenEvent.screen.name
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
    internal fun handleCustomAction(
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
            initialScreenID = ""
        )
        val screen = Screen(
            id = action.screen.id,
            metadata = Metadata(
                action.screen.metadata?.tags ?: emptySet(),
                action.screen.metadata?.properties ?: emptyMap()
            ),
            childIDs = emptyList(),
            name = action.screen.name
        )
        val node = ErasedNode(
            id = action.node.id,
            name = action.node.name,
            metadata = Metadata(
                action.node.metadata?.tags ?: emptySet(),
                action.node.metadata?.properties ?: emptyMap()
            )
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
    internal fun app.judo.sdk.compose.ui.URLRequest.toAndroidUrlRequest(): URLRequest = URLRequest(
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
    internal fun app.judo.sdk.compose.ui.URLRequest.updateFrom(urlRequest: URLRequest) {
        url = urlRequest.url
        method = when (urlRequest.method) {
            app.judo.sdk.api.models.HttpMethod.GET -> HttpMethod.GET
            app.judo.sdk.api.models.HttpMethod.PUT -> HttpMethod.PUT
            app.judo.sdk.api.models.HttpMethod.POST -> HttpMethod.POST
        }
        headers = urlRequest.headers
        body = urlRequest.body
    }

}
