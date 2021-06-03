package app.judo.sdk.core.controllers

import android.app.Application
import androidx.annotation.MainThread
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.api.models.Experience
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
        accessToken: String,
        experienceCacheSize: Long = Environment.Sizes.EXPERIENCE_CACHE_SIZE,
        imageCacheSize: Long = Environment.Sizes.IMAGE_CACHE_SIZE,
        vararg domains: String,
    )

    suspend fun performSync(prefetchAssets: Boolean = false, onComplete: () -> Unit = {})

    suspend fun onFirebaseRemoteMessageReceived(data: Map<String, String>)

    suspend fun setPushToken(fcmToken: String)

    fun loadExperienceIntoMemory(experience: Experience)

    fun setUserDataSupplier(supplier: UserDataSupplier)

    @MainThread
    fun setExperienceFragmentFactory(factory: ExperienceFragmentFactory)
}