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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import app.judo.compose.ui.*
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_INTENT
import app.judo.sdk.ui.JudoComposeHelpers.handleCustomAction
import app.judo.sdk.ui.JudoComposeHelpers.handleScreenViewed
import app.judo.sdk.ui.JudoComposeHelpers.toAndroidUrlRequest
import app.judo.sdk.ui.JudoComposeHelpers.updateFrom
import app.judo.sdk.ui.events.ExperienceRequested

@Deprecated("Embed the AsyncExperience() composable directly in your UI")
open class ExperienceFragment : Fragment() {
    companion object {
        private const val TAG = "ExperienceFragment"

        @MainThread
        fun ExperienceFragment.applyArguments(
            intent: Intent
        ): ExperienceFragment = apply {
            arguments = (arguments ?: Bundle()).apply {
                putParcelable(EXPERIENCE_INTENT, intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val intent = arguments?.getParcelable<Intent>(EXPERIENCE_INTENT)
            ?: throw IllegalArgumentException("ExperienceFragment requires an Intent to be passed in as an argument")

        val request = ExperienceRequested(intent)

        if (request.loadFromMemory) {
            Log.e(tag, "loadFromMemory not supported. Use LegacyExperienceActivity if you need it, or adopt judo-compose directly.")
            return null
        }

        val uriBuilder = Uri.parse(request.experienceURL).buildUpon()
        request.screenId?.let {
            uriBuilder.appendQueryParameter("screenID", it)
        }
        val uri = uriBuilder.build()

        val uriScheme = uri?.scheme?.lowercase()

        val experienceModifier = Modifier.judoCustomAction { action ->
            val activity = this.activity ?: return@judoCustomAction
            handleCustomAction(
                activity,
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

        return ComposeView(requireContext()).apply {
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

//
//    open fun handleAction(action: Action) {
//        when (action) {
//            is Close -> {
//                popStackOrFinish()
//            }
//
//            is PerformSegue -> {
//                navigate(action.screenID, action.segueStyle)
//            }
//
//            is OpenURL -> {
//                if (action.dismissExperience) onDismiss()
//                try {
//
//                    val uri = action.url.let { theURL ->
//                        action.interpolator?.interpolate(theURL) ?: theURL
//                    }.toUri()
//
//                    startActivity(Intent(Intent.ACTION_VIEW, uri))
//
//                } catch (error: Throwable) {
//                    Environment.current
//                        .logger.e(TAG, "Failed to open URL: ${action.url}", error)
//                }
//            }
//
//            is PresentWebsite -> {
//                //TODO: enable changing toolbar color
//                val theURL = action.url
//                try {
//
//                    (action.interpolator?.interpolate(theURL) ?: theURL)
//                        .toUri()
//                        .toCustomTabsIntent()
//                        .also(::startActivity)
//
//                } catch (error: Throwable) {
//                    Environment.current
//                        .logger.e(TAG, "Failed to present website for URL: $theURL", error)
//                }
//            }
//            is Custom -> {
//                if (action.dismissExperience) onDismiss()
//            }
//        }
//    }
//
//    /**
//     * Dismisses the experience by finishing the host activity if the host activity is
//     * an [ExperienceActivity] or no-op if not.
//     */
//    open fun onDismiss() {
//        if (isEmbeddedFragment()) {
//            // no-op
//        } else {
//            requireActivity().finish()
//        }
//    }
//
//    /**
//     * Called during [Fragment.onViewCreated] if the Android SDK version is below 23.
//     */
//    @Deprecated("This method is now a no-op on account")
//    open fun onUnsupportedAndroidPlatformVersion() {
//
//    }
//
//    /**
//     * Called during [Fragment.onViewCreated] and before any data is loaded.
//     */
//    open fun onEmpty() {
//        /* no-op */
//    }
//
//    /**
//     * Called whenever a [Experience] is being retrieved from a data source.
//     *
//     * This function will be skipped if retrieving a [Experience] from memory.
//     */
//    open fun onLoading() {
//        /* no-op */
//    }
//
//    /**
//     * Called when there is some kind of [ExperienceError].
//     *
//     *
//     * A basic [AlertDialog] will be displayed with one of the two configurations
//     * depending on two failure scenarios, "hard" and "soft" failure.
//     *
//     *
//     * For soft failures:
//     * A two button dialog offering an opportunity to retry loading the Experience or exit the activity.
//     *
//     *
//     * For hard failures:
//     * A one button dialog forcing the user to exit the activity.
//     */
//    open fun onError(error: ExperienceError) {
//        Log.e(TAG, "Problem displaying experience: $error")
//        when (error) {
//            is ExperienceError.NetworkError -> {
//                showSoftFailureDialog()
//            }
//
//            is ExperienceError.MalformedExperienceError -> {
//                showHardFailureDialog()
//            }
//
//            is ExperienceError.ExperienceNotFoundError -> {
//                showHardFailureDialog()
//            }
//
//            is ExperienceError.UnexpectedError -> {
//                showHardFailureDialog()
//            }
//
//            is ExperienceError.NotInitialized -> {
//                showHardFailureDialog()
//            }
//        }
//    }
//
//    /**
//     * Called whenever a [Experience] has been successfully retrieved from a data source.
//     */
//    open fun onRetrieved(experience: Experience) {
//        /* no-op */
//    }
}
