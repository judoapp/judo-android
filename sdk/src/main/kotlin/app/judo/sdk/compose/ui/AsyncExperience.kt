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

package app.judo.sdk.compose.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.judo.sdk.BuildConfig
import app.judo.sdk.compose.data.JudoWebService
import app.judo.sdk.compose.model.values.CDNConfiguration
import app.judo.sdk.compose.model.values.ExperienceModel
import app.judo.sdk.compose.ui.fonts.FontLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

/**
 * Load an Experience from the given remote URL.
 *
 *  If an error occurs, this composable will yield empty.
 *
 * @param url The URL of the experience: may be a app link (aka universal link) or deep link.
 * @param userInfo A closure that provides a hashmap of data to be available under `user.` in the
 *                 experience. If null, defaults to the values provided within the Experience.
 */
@Composable
fun AsyncExperience(
    url: Uri,
    modifier: Modifier = Modifier,
    userInfo: (() -> Map<String, Any>)? = null
) {
    AsyncExperience(url = url, modifier = modifier) { phase ->
        when (phase) {
            AsyncExperiencePhase.Empty -> {
                CircularProgressIndicator(modifier = Modifier.requiredSize(25.dp))
            }
            is AsyncExperiencePhase.Failure -> { /* empty */ }
            is AsyncExperiencePhase.Success -> {
                CompositionLocalProvider(Environment.LocalUserInfo provides userInfo) {
                    Experience(experience = phase.experience)
                }
            }
        }
    }
}

/**
 * Load an Experience from the given remote URL.
 *
 * If an error occurs, this composable will yield empty.
 *
 * @param url The URL of the experience: may be a app link (aka universal link) or deep link.
 * @param placeholder A Composable to show while the experience is loading.
 * @param userInfo A closure that provides a hashmap of data to be available under `user.` in the
 *                 experience. If null, defaults to the values provided within the Experience.
 */
@Composable
fun AsyncExperience(
    url: Uri,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit,
    userInfo: (() -> Map<String, Any>)? = null
) {
    AsyncExperience(url = url, modifier = modifier) { phase ->
        when (phase) {
            AsyncExperiencePhase.Empty -> {
                placeholder()
            }
            is AsyncExperiencePhase.Failure -> { /* empty */ }
            is AsyncExperiencePhase.Success -> {
                CompositionLocalProvider(Environment.LocalUserInfo provides userInfo) {
                    Experience(experience = phase.experience)
                }
            }
        }
    }
}

/**
 * Load an Experience from the given remote URL.
 *
 * In the `content` composable closure given, in the [AsyncExperiencePhase.Success] use
 * [Experience] to display the [ExperienceModel].
 *
 * @param url The URL of the experience: may be a app link (aka universal link) or deep link.
 * @param userInfo A closure that provides a hashmap of data to be available under `user.` in the
 *                 experience. If null, defaults to the values provided within the Experience.
 * @param content A composable closure that is given the current result of the experience request,
 *                such as error, success, etc.
 */
@Composable
fun AsyncExperience(
    url: Uri,
    modifier: Modifier = Modifier,
    userInfo: (() -> Map<String, Any>)? = null,
    content: @Composable (AsyncExperiencePhase) -> Unit
) {
    Services.Inject { services ->
        // This version allows full control of the view.
        // The content closure passes the state of the fetch and
        // and the developer can customize the view accordingly.
        val viewModel = viewModel<AsyncExperienceViewModel>()
        val context = LocalContext.current
        SideEffect {
            viewModel.start(
                context,
                webService = services.webService,
                fontLoader = services.fontLoader
            )
        }

        LaunchedEffect(url) {
            viewModel.request(url)
        }

        val urlParameters = url.queryParameterNames.mapNotNull { key ->
            url.getQueryParameter(key)?.let { Pair(key, it) }
        }.associate { it }

        Surface(
            // TODO: fillMaxSize will not be desirable in 2.0 when we enable the embedding case and
            //  Screen is removed.
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            when (val state = viewModel.state.collectAsState().value) {
                AsyncExperienceViewModel.State.Init -> {
                    content(AsyncExperiencePhase.Empty)
                }
                AsyncExperienceViewModel.State.Loading -> {
                    content(AsyncExperiencePhase.Empty)
                }
                is AsyncExperienceViewModel.State.Success -> {
                    CompositionLocalProvider(
                        Environment.LocalAssetContext provides state.assertContext,
                        Environment.LocalExperienceUrl provides url,
                        Environment.LocalUrlParameters provides urlParameters,
                        Environment.LocalUserInfo provides userInfo,
                        Environment.LocalTypefaceMapping provides state.typeFaceMapping,
                        Environment.LocalExperienceId provides state.cloudExperienceId,
                        Environment.LocalExperienceName provides state.cloudExperienceName
                    ) {
                        content(AsyncExperiencePhase.Success(state.experience))
                    }
                }
                is AsyncExperienceViewModel.State.Failed -> {
                    content(AsyncExperiencePhase.Failure(state.reason))
                }
            }
        }
    }
}

sealed class AsyncExperiencePhase {
    /**
     * No experience is yet loaded.
     */
    object Empty : AsyncExperiencePhase()

    /**
     * An experience successfully loaded.
     */
    class Success(val experience: ExperienceModel) : AsyncExperiencePhase()

    /**
     * An experience failed to load with an error.
     */
    class Failure(val exception: Exception) : AsyncExperiencePhase()
}

internal class AsyncExperienceViewModel() : ViewModel() {
    private val tag = AsyncExperienceViewModel::class.java.simpleName

    sealed class State {
        object Init : State()
        object Loading : State()
        data class Success(
            val experience: ExperienceModel,
            val assertContext: RemoteAssetContext,
            val typeFaceMapping: FontLoader.TypeFaceMapping,
            val cloudExperienceId: String?,
            val cloudExperienceName: String?
        ) : State()
        data class Failed(val reason: Exception) : State()
    }

    private val _state = MutableStateFlow<AsyncExperienceViewModel.State>(AsyncExperienceViewModel.State.Init)
    val state: StateFlow<AsyncExperienceViewModel.State> = _state

    sealed class Command {
        object NoneYet : Command()
        class LoadExperience(val uri: Uri) : Command()
    }

    private val commands = MutableStateFlow<Command>(Command.NoneYet)

    fun start(
        context: Context,
        webService: JudoWebService,
        fontLoader: FontLoader
    ) {
        viewModelScope.launch {
            commands
                .filterIsInstance<AsyncExperienceViewModel.Command.LoadExperience>()
                // mapLatest allows us to cancel any in-flight request attempt if a new Load command is issued.
                .mapLatest { loadExperience ->
                    _state.emit(AsyncExperienceViewModel.State.Loading)
                    val url = loadExperience.uri.buildUpon().scheme(
                        // replace any non-HTTP scheme with HTTPS, to more easily support deep linking setups.
                        if (!listOf("https", "http").contains(loadExperience.uri.scheme?.lowercase())) "https" else loadExperience.uri.scheme
                    ).build()

                    var configuration: CDNConfiguration? = null
                    try {
                        val configJsonUrl = url.buildUpon().apply {
                            clearQuery()
                            path("configuration.json")
                        }
                        configuration = webService.getConfiguration(
                            configJsonUrl.toString()
                        )
                        Log.i("JudoSDK", "Configuration JSON has been retrieved.")
                    } catch (e: java.lang.Exception) {
                        // Configuration is optional - NO-OP
                    }

                    try {
                        val documentJsonUrl = url.buildUpon().apply {
                            appendPath("document.json")
                        }

                        val experienceResponse = webService.getExperience(documentJsonUrl.toString())
                        val experience = experienceResponse.body() ?: throw Exception("Failed to get the Experience's document.json.")

                        val cloudExperienceId = experienceResponse.headers()["judo-cloud-experience-id"]
                        val cloudExperienceName = experienceResponse.headers()["judo-cloud-experience-name"]

                        val assetContext = RemoteAssetContext(url, configuration)
                        val fontSources = experience.fonts.flatMap { font ->
                            font.sources.apply {
                                if (this == null) Log.w(tag, "This file saved before Judo 1.11, custom fonts will not work.")
                            } ?: emptyList()
                        }

                        val typefaceMapping = fontLoader.getTypefaceMappings(
                            context,
                            assetContext,
                            fontSources
                        )

                        return@mapLatest AsyncExperienceViewModel.ApiResponse.Success(
                            experience,
                            assetContext,
                            typefaceMapping,
                            cloudExperienceId,
                            cloudExperienceName
                        )
                    } catch (e: Exception) {
                        Log.e("JudoSDK", "Unable to retrieve Experience: ${e.message ?: "unknown reason"}")
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace()
                        }
                        return@mapLatest AsyncExperienceViewModel.ApiResponse.Error(e)
                    }
                }.collect { response ->
                    when (response) {
                        is AsyncExperienceViewModel.ApiResponse.Success -> {
                            response.experience.buildTreeAndRelationships()
                            _state.emit(
                                AsyncExperienceViewModel.State.Success(
                                    response.experience,
                                    response.assetContext,
                                    response.typeFaceMapping,
                                    response.cloudExperienceId,
                                    response.cloudExperienceName
                                )
                            )
                        }
                        is ApiResponse.Error -> {
                            _state.emit(AsyncExperienceViewModel.State.Failed(response.reason))
                        }
                    }
                }
        }
    }

    private sealed class ApiResponse {
        class Error(val reason: java.lang.Exception) : ApiResponse()
        class Success(
            val experience: ExperienceModel,
            val assetContext: RemoteAssetContext,
            val typeFaceMapping: FontLoader.TypeFaceMapping,
            val cloudExperienceId: String? = null,
            val cloudExperienceName: String? = null
        ) : ApiResponse()
    }

    fun request(url: Uri) {
        viewModelScope.launch {
            commands.emit(AsyncExperienceViewModel.Command.LoadExperience(url))
        }
    }
}
