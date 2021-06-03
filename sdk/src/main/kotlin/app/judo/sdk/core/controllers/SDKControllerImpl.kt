package app.judo.sdk.core.controllers

import android.app.Application
import app.judo.sdk.api.Judo
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.MutableEnvironment
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.implementations.EnvironmentImpl
import app.judo.sdk.core.implementations.NotificationHandlerImpl
import app.judo.sdk.core.implementations.ProductionLoggerImpl
import app.judo.sdk.core.implementations.SynchronizerImpl
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.sync.Synchronizer
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

    override fun initialize(
        application: Application,
        accessToken: String,
        experienceCacheSize: Long,
        imageCacheSize: Long,
        vararg domains: String,
    ) {
        require(accessToken.isNotBlank()) {
            ErrorMessages.ACCESS_TOKEN_NOT_BLANK
        }
        require(domains.isNotEmpty()) {
            ErrorMessages.DOMAINS_NOT_EMPTY
        }
        domains.forEach {
            require(it.isNotBlank()) {
                ErrorMessages.DOMAIN_NAME_NOT_BLANK
            }
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

            this.accessToken = accessToken

            domainNames = domains.toSet()
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

    override suspend fun setPushToken(fcmToken: String) {
        if (this::environment.isInitialized) {
            NotificationHandlerImpl(
                environment
            ).setPushToken(fcmToken)
        } else {
            logger.e(TAG, null, IllegalStateException(ErrorMessages.SDK_NOT_INITIALIZED))
        }
    }

    override fun loadExperienceIntoMemory(experience: Experience) {
        if (this::environment.isInitialized) {
            environment.experienceRepository.put(
                experience
            )
        }
    }

    override fun setUserDataSupplier(supplier: UserDataSupplier) {
        (environment as? MutableEnvironment)?.userDataSupplier = supplier
    }

    override fun setExperienceFragmentFactory(factory: ExperienceFragmentFactory) {
        if (this::environment.isInitialized) {
            (environment as MutableEnvironment).experienceFragmentFactory = factory
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