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
import app.judo.sdk.api.Judo
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.events.ScreenViewedCallback
import app.judo.sdk.api.models.Authorizer
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.URLRequest
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.MutableEnvironment
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.implementations.*
import app.judo.sdk.core.implementations.EnvironmentImpl
import app.judo.sdk.core.implementations.NotificationHandlerImpl
import app.judo.sdk.core.implementations.SynchronizerImpl
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.sync.Synchronizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

/**
 * Coordinates the underlying pieces of the SDK's components.
 */
internal class SDKControllerImpl : SDKController {

    companion object {
        private const val TAG = "SDKController"
    }

    override var logger: Logger = ProductionLoggerImpl()

    /**
     * This should not be referenced from the UI layer directly.
     *
     * Instead use the [Environment.Companion.current] extension variable.
     *
     * It will throw the correct [ExperienceError.NotInitialized] error.
     */
    lateinit var environment: Environment

    private lateinit var synchronizer: Synchronizer

    override fun initialize(application: Application, configuration: Judo.Configuration) {
        require(configuration.accessToken.isNotBlank()) {
            ErrorMessages.ACCESS_TOKEN_NOT_BLANK
        }
        require(configuration.domain.isNotBlank()) {
            ErrorMessages.DOMAIN_NAME_NOT_BLANK
        }

        if (!this::environment.isInitialized) {
            environment = EnvironmentImpl(
                context = application
            )

            logger = environment.logger.apply {
                logLevel = logger.logLevel
            }
        }

        (environment as? MutableEnvironment)?.apply {

            this.experienceCacheSize = experienceCacheSize

            this.imageCacheSize = imageCacheSize

            if (keyValueCache.retrieveString(Environment.Keys.DEVICE_ID) == null) {
                keyValueCache.putString(
                    Environment.Keys.DEVICE_ID to UUID.randomUUID().toString()
                )
            }

            this.configuration = configuration

            environment.eventQueue.start()
        }
    }

    override suspend fun performSync(prefetchAssets: Boolean, onComplete: () -> Unit) {
        if (!this@SDKControllerImpl::synchronizer.isInitialized)
            synchronizer = SynchronizerImpl(
                environment
            )
        synchronizer.performSync(prefetchAssets, onComplete)
    }

    override suspend fun onFirebaseRemoteMessageReceived(data: Map<String, String>) {
        if (this::environment.isInitialized) {
            NotificationHandlerImpl(
                environment = environment
            ).handleRemoteMessagingData(data)
        } else {
            logger.e(TAG, null, IllegalStateException(ErrorMessages.SDK_NOT_INITIALIZED))
        }
    }

    override fun setPushToken(fcmToken: String) {
        if (this::environment.isInitialized) {
            environment.pushTokenService.register(fcmToken)
        } else {
            logger.e(TAG, null, IllegalStateException(ErrorMessages.SDK_NOT_INITIALIZED))
        }
    }

    override fun loadExperienceIntoMemory(experience: Experience, authorizers: List<Authorizer>) {
        if (this::environment.isInitialized) {
            environment.experienceRepository.put(
                experience,
                authorizers = authorizers
            )
        }
    }

    override fun identify(userId: String?, traits: Map<String, Any>) {
        if (this::environment.isInitialized) {
            (environment as? MutableEnvironment)?.profileService?.identify(
                userId,
                traits
            )
        }
    }

    override fun reset() {
        if (this::environment.isInitialized) {
            (environment as? MutableEnvironment)?.profileService?.reset()
        }
    }

    override val anonymousId: String
        get() = (environment as? MutableEnvironment)?.profileService?.anonymousId ?: ""

    override fun setExperienceFragmentFactory(factory: ExperienceFragmentFactory) {
        if (this::environment.isInitialized) {
            (environment as? MutableEnvironment)?.experienceFragmentFactory = factory
        }
    }

    override fun addScreenViewedCallback(callback: ScreenViewedCallback) {
        if(this::environment.isInitialized) {
            CoroutineScope(environment.mainDispatcher).launch {
                environment.eventBus.eventFlow.collect { event ->
                    if (event is Event.ScreenViewed) {
                        callback.screenViewed(event)
                    }
                }
            }
        }
    }
}

/**
 * The current [Environment] instance.
 *
 * @throws [ExperienceError.NotInitialized] If the [Judo] has not been initialized prior to access or running under Android API 23.
 */
internal val Environment.Companion.current: Environment
    get() = try {
        (Judo.controller as SDKControllerImpl).environment
    } catch (e: Throwable) {
        throw ExperienceError.NotInitialized(
            message = ErrorMessages.SDK_NOT_INITIALIZED
        )
    }