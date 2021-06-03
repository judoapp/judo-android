package app.judo.sdk.api

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.MainThread
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.api.logs.LogLevel
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.controllers.NoOpSDKController
import app.judo.sdk.core.controllers.SDKController
import app.judo.sdk.core.controllers.SDKControllerImpl
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.ui.ExperienceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * TODO DOCUMENT
 */
object Judo {

    internal val controller: SDKController =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SDKControllerImpl()
        } else {
            NoOpSDKController()
        }

    /**
     * The type of logs the SDK will put out in the console
     *
     * Defaults to [LogLevel.Error]
     */
    var logLevel: LogLevel
        get() {
            return controller.logger.logLevel
        }
        set(value) {
            controller.logger.logLevel = value
        }

    /**
     * Initializes the Judo SDK.
     *
     * @param application A [Application] class needed for services.
     * @param accessToken The account token used to authorize API requests.
     * @param domains A URL of the domain where Experiences will be retrieved from.
     *
     * @throws IllegalArgumentException If the [accessToken] or [domains] is empty, or one of the domains is blank.
     */
    @JvmStatic
    @MainThread
    fun initialize(
        application: Application,
        accessToken: String,
        vararg domains: String,
        experienceCacheSize: Long = Environment.Sizes.EXPERIENCE_CACHE_SIZE,
        imageCacheSize: Long = Environment.Sizes.IMAGE_CACHE_SIZE,
    ) {
        controller.apply {
            initialize(
                application = application,
                accessToken = accessToken,
                experienceCacheSize,
                imageCacheSize,
                domains = domains,
            )
        }
    }

    fun setUserDataSupplier(supplier: UserDataSupplier) {
        controller.setUserDataSupplier(supplier)
    }

    /**
     * Ensure this function is only called **_after_** [Judo.initialize]
     *
     * Otherwise it will have no effect.
     */
    @MainThread
    fun setExperienceFragmentFactory(factory: ExperienceFragmentFactory) {
        controller.setExperienceFragmentFactory(factory)
    }

    @JvmStatic
    fun performSync(prefetchAssets: Boolean = false, onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            controller.performSync(prefetchAssets, onComplete)
        }
    }

    fun onFirebaseRemoteMessageReceived(data: Map<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            controller.onFirebaseRemoteMessageReceived(data)
        }
    }

    fun setPushToken(fcmToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controller.setPushToken(fcmToken)
        }
    }

    /**
     * @throws IllegalArgumentException When [activityClass] is not, or does not extend, [ExperienceActivity].
     * */
    @JvmStatic
    @JvmOverloads
    fun makeIntent(
        context: Context,
        url: String,
        ignoreCache: Boolean = false,
        activityClass: Class<*> = ExperienceActivity::class.java,
        screenId: String? = null,
    ): Intent {
        return try {
            activityClass.asSubclass(activityClass)

            Intent(context, activityClass).apply {
                putExtra(Environment.Keys.EXPERIENCE_URL, url)
                putExtra(Environment.Keys.IGNORE_CACHE, ignoreCache)
                screenId?.let { id ->
                    putExtra(Environment.Keys.SCREEN_ID, id)
                }
            }
        } catch (t: Throwable) {
            throw IllegalArgumentException(ErrorMessages.EXTEND_EXPERIENCE_ACTIVITY(activityClass))
        }
    }

    /**
     * @throws IllegalArgumentException When [activityClass] is not, or does not extend, [ExperienceActivity].
     * @param screenId Overrides the initial screen that is displayed.
     * */
    @JvmStatic
    @JvmOverloads
    fun makeIntent(
        context: Context,
        experience: Experience,
        activityClass: Class<*> = ExperienceActivity::class.java,
        screenId: String? = null,
    ): Intent {
        return try {
            activityClass.asSubclass(activityClass)

            controller.loadExperienceIntoMemory(
                experience
            )

            Intent(context, activityClass).apply {
                putExtra(Environment.Keys.LOAD_FROM_MEMORY, true)
                putExtra(Environment.Keys.EXPERIENCE_KEY, experience.id.toString())
                screenId?.let { id ->
                    putExtra(Environment.Keys.SCREEN_ID, id)
                }
            }
        } catch (t: Throwable) {
            throw IllegalArgumentException(ErrorMessages.EXTEND_EXPERIENCE_ACTIVITY(activityClass))
        }
    }


}
