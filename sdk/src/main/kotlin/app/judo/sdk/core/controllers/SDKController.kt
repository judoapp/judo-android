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

package app.judo.sdk.core.controllers

import android.app.Application
import androidx.annotation.MainThread
import app.judo.sdk.api.Judo
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.events.CustomActionCallback
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.events.ScreenViewedCallback
import app.judo.sdk.api.models.Authorizer
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.URLRequest
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.log.Logger

internal interface SDKController {
    /**
     * This should not be referenced from the UI layer directly.
     *
     * Instead use the [Environment.Companion.current] extension variable.
     *
     * It will throw the correct [ExperienceError.NotInitialized] error.
     */

    var logger: Logger

    fun initialize(
        application: Application,
        configuration: Judo.Configuration
    )

    suspend fun performSync(onComplete: () -> Unit = {})

    suspend fun onFirebaseRemoteMessageReceived(data: Map<String, String>)

    fun setPushToken(fcmToken: String)

    fun loadExperienceIntoMemory(experience: Experience, authorizers: List<Authorizer>, urlQueryParameters: Map<String, String>)

    fun identify(userId: String?, traits: Map<String, Any>)

    fun reset()

    val anonymousId: String

    val userId: String?

    @MainThread
    fun setExperienceFragmentFactory(factory: ExperienceFragmentFactory)

    fun addScreenViewedCallback(callback: ScreenViewedCallback)

    fun addCustomActionCallback(callback: CustomActionCallback)
}