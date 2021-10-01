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

package app.judo.sdk.api

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.MainThread
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.events.ScreenViewedCallback
import app.judo.sdk.api.logs.LogLevel
import app.judo.sdk.api.models.Authorizer
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.HttpMethod
import app.judo.sdk.api.models.URLRequest
import app.judo.sdk.core.controllers.NoOpSDKController
import app.judo.sdk.core.controllers.SDKController
import app.judo.sdk.core.controllers.SDKControllerImpl
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.ui.ExperienceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


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
     * @param domain A URL of the domain where Experiences will be retrieved from.
     *
     * @throws IllegalArgumentException If the [accessToken] or [domains] is empty, or one of the domains is blank.
     */
    @JvmStatic
    @MainThread
    fun initialize(
        application: Application,
        accessToken: String,
        domain: String
    ) {
        initialize(
            application,
            Configuration(
                accessToken = accessToken,
                domain = domain
            )
        )
    }

    /**
     * Initializes the Judo SDK.
     *
     * @param application A [Application] class needed for services.
     * @param configuration A [Judo.Configuration] class that includes all the configuration
     * parameters for the Judo SDK, including the mandatory ones access token and domain.
     *
     * @throws IllegalArgumentException If the [configuration.accessToken] or
     * [configuration.domain] is empty, or one of the domains is blank.
     */
    @JvmStatic
    @MainThread
    fun initialize(
        application: Application,
        configuration: Judo.Configuration
    ) {
        controller.apply {
            this.initialize(
                application,
                configuration
            )
        }
    }

    /**
     * Call this method to identify the logged in user and their details to Judo.
     */
    @MainThread
    fun identify(userId: String?, traits: Map<String, Any> = emptyMap()) {
        controller.identify(
            userId,
            traits
        )
    }

    @MainThread
    @Deprecated("Misnamed method has been deprecated", replaceWith = ReplaceWith("anonymousID"))
    fun getAnonymousID(@Suppress("unused") anonymousId: String): String {
        return controller.anonymousId
    }

    /**
     * Get the Anonymous ID for the current user in Judo.
     */
    val anonymousId: String
        get() = controller.anonymousId

    /**
     * Resets the user profile information previously identified to Judo, and also recycles the
     * Anonymous ID.
     */
    @MainThread
    fun reset() {
        controller.reset()
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

    fun addScreenViewedCallback(callback: ScreenViewedCallback) {
        controller.addScreenViewedCallback(callback)
    }

    @JvmStatic
    @Deprecated(
        message = "Manually pre-fetching assets is no longer supported",
        replaceWith = ReplaceWith("Judo.performSync {}"),
    )
    fun performSync(
        @Suppress("unused")
        prefetchAssets: Boolean = false,
        onComplete: () -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            controller.performSync(onComplete)
        }
    }

    @JvmStatic
    fun performSync(
        onComplete: () -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            controller.performSync(onComplete = onComplete)
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
        userInfo: Map<String, Any>? = null
    ): Intent {
        return try {
            activityClass.asSubclass(activityClass)

            Intent(context, activityClass).apply {
                putExtra(Environment.Keys.EXPERIENCE_URL, url)
                putExtra(Environment.Keys.IGNORE_CACHE, ignoreCache)
                screenId?.let { id ->
                    putExtra(Environment.Keys.SCREEN_ID, id)
                }
                userInfo?.let {
                    putExtra(Environment.Keys.USER_INFO_OVERRIDE, HashMap(userInfo))
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
        userInfo: Map<String, Any>? = null,
        authorizers: List<Authorizer> = listOf(),
        urlQueryParameters: Map<String, String> = mapOf()
    ): Intent {
        return try {
            activityClass.asSubclass(activityClass)

            controller.loadExperienceIntoMemory(experience, authorizers, urlQueryParameters)

            Intent(context, activityClass).apply {
                putExtra(Environment.Keys.LOAD_FROM_MEMORY, true)
                putExtra(Environment.Keys.EXPERIENCE_KEY, experience.id)
                screenId?.let { id ->
                    putExtra(Environment.Keys.SCREEN_ID, id)
                }
                userInfo?.let {
                    putExtra(Environment.Keys.USER_INFO_OVERRIDE, HashMap(userInfo))
                }
            }
        } catch (t: Throwable) {
            throw IllegalArgumentException(ErrorMessages.EXTEND_EXPERIENCE_ACTIVITY(activityClass))
        }
    }

    /**
     * This object describes the configuration of the Judo SDK.
     *
     * If you are using Java, there is a [Builder] available to help you tersely set up a
     * Configuration with only the fields you care to change from the defaults.
     */
    data class Configuration(
        /**
         * The API key for this app obtained from your Judo account settings.
         */
        val accessToken: String,

        /**
         * The Judo domain. Commonly in the form `myapp.judo.app`.
         */
        val domain: String,

        /**
         * Configures which events are tracked by Judo and what data is captured.
         */
        val analyticsMode: AnalyticsMode = AnalyticsMode.DEFAULT,

        val experienceCacheSize: Long = Environment.Sizes.EXPERIENCE_CACHE_SIZE,
        val imageCacheSize: Long = Environment.Sizes.IMAGE_CACHE_SIZE,
        var authorizers: List<Authorizer> = emptyList()
    ) {
        init {
            authorizers = (authorizers + listOf(
                // add an implicit default authorizer for first-party Judo data sources.
                Authorizer("data.judo.app") { it.headers["Judo-Access-Token"] = accessToken }
            )).distinct()
        }

        enum class AnalyticsMode {
            /**
             * All events are tracked along with any user data passed to the
             * `Judo.identify` method.
             */
            DEFAULT,

            /**
             * All events are tracked but only anonymous device data is captured such as locale and
             * device token.
             */
            ANONYMOUS,

            /**
             * Only the bare minimum events required for all features to function correctly are
             * tracked and only anonymous device data is captured.
             */
            MINIMAL,

            /**
             * No events are tracked and no device or user data is sent to Judo's servers. Some
             * features may not work correctly with this setting.
             */
            DISABLED;
        }

        class Builder(
            val accessToken: String,
            val domain: String
        ) {
            var results: Configuration = Configuration(accessToken, domain)

            fun setAllowedEvents(analyticsMode: AnalyticsMode): Builder {
                results = results.copy(analyticsMode = analyticsMode)
                return this
            }

            fun setExperienceCacheSize(experienceCacheSize: Long): Builder {
                results = results.copy(experienceCacheSize = experienceCacheSize)
                return this
            }

            fun setImageCacheSize(imageCacheSize: Long): Builder {
                this.results = results.copy(imageCacheSize = imageCacheSize)
                return this
            }

            fun authorize(pattern: String, with: (URLRequest) -> Unit): Builder {
                val authorizer = Authorizer(pattern, with)
                this.results = results.copy(authorizers = this.results.authorizers + listOf(authorizer))
                return this
            }

            fun build(): Configuration {
                return results.copy()
            }
        }
    }
}
