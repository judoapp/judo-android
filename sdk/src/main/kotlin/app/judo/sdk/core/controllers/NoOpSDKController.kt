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
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.events.ActionReceivedCallback
import app.judo.sdk.api.events.ScreenViewedCallback
import app.judo.sdk.api.data.UserInfoSupplier
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.implementations.ProductionLoggerImpl
import app.judo.sdk.core.log.Logger

internal class NoOpSDKController : SDKController {

    override var logger: Logger = ProductionLoggerImpl()

    override fun initialize(
        application: Application,
        accessToken: String,
        experienceCacheSize: Long,
        imageCacheSize: Long,
        vararg domains: String
    ) {
        /* no-op */
    }

    override suspend fun performSync(prefetchAssets: Boolean, onComplete: () -> Unit) {
        onComplete()
    }

    override suspend fun onFirebaseRemoteMessageReceived(data: Map<String, String>) {
        /* no-op */
    }

    override suspend fun setPushToken(fcmToken: String) {
        /* no-op */
    }

    override fun loadExperienceIntoMemory(experience: Experience) {
        /* no-op */
    }

    override fun setUserInfoSupplier(supplier: UserInfoSupplier) {
        /* no-op */
    }

    override fun setExperienceFragmentFactory(factory: ExperienceFragmentFactory) {
        /* no-op */
    }

    override fun addActionReceivedCallback(callback: ActionReceivedCallback) {
        /* no-op */
    }

    override fun addScreenViewedCallback(callback: ScreenViewedCallback) {
        /* no-op */
    }
}