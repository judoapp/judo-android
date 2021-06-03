package app.judo.sdk.core.controllers

import android.app.Application
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.environment.Environment
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

    override fun setUserDataSupplier(supplier: UserDataSupplier) {
        /* no-op */
    }

    override fun setExperienceFragmentFactory(factory: ExperienceFragmentFactory) {
        /* no-op */
    }

}