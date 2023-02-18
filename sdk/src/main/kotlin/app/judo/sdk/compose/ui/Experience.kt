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

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.ModifierLocalConsumer
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.judo.sdk.compose.model.fromZipStream
import app.judo.sdk.compose.model.nodes.Screen
import app.judo.sdk.compose.model.values.ExperienceModel
import app.judo.sdk.compose.ui.fonts.FontLoader
import app.judo.sdk.compose.ui.graphics.*
import app.judo.sdk.compose.ui.layers.ScreenLayer
import app.judo.sdk.compose.ui.utils.getDarkModeValue
import app.judo.sdk.compose.vendor.accompanist.navigation_animation.AnimatedNavHost
import app.judo.sdk.compose.vendor.accompanist.navigation_animation.composable
import app.judo.sdk.compose.vendor.accompanist.navigation_animation.rememberAnimatedNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/**
 * Retrieve an experience from a ZIP archive given at URL.
 *
 * @param userInfo A closure that provides a hashmap of data to be available under `user.` in the
 *                 experience. If null, defaults to the values provided within the Experience.
 */
@Composable
fun Experience(
    fileUrl: Uri,
    modifier: Modifier = Modifier,
    userInfo: (() -> Map<String, Any>)? = null
) {
    val viewModel = viewModel<LoadExperienceViewModel>()
    val context = LocalContext.current

    Services.Inject { services ->
        if (viewModel.experience == null) {
            LaunchedEffect(true) {
                viewModel.loadExperience(context, services.fontLoader, fileUrl)
            }
        }

        CompositionLocalProvider(
            Environment.LocalAssetContext provides UnpackedTempfilesZipContext
        ) {
            viewModel.experience?.let { experienceModel ->
                CompositionLocalProvider(
                    Environment.LocalTypefaceMapping provides viewModel.typefaceMapping,
                    Environment.LocalUserInfo provides userInfo,
                    // provide the in-file URL parameters because query parameters aren't supported on file URIs
                    Environment.LocalUrlParameters provides experienceModel.urlParameters
                ) {
                    Experience(experienceModel, modifier = modifier)
                }
            }
        }
    }
}

/**
 * Load an experience from a Judo file ZIP container in memory.
 *
 * @param userInfo A closure that provides a hashmap of data to be available under `user.` in the
 *                 experience. If null, defaults to the values provided within the Experience.
 */
@Composable
fun Experience(
    data: ByteArray,
    modifier: Modifier = Modifier,
    userInfo: (() -> Map<String, Any>)? = null
) {
    // Display an Experience from the given ZIP archive that is already loaded into memory
    // in the given Data byte buffer.
    val viewModel = viewModel<LoadExperienceViewModel>()
    val context = LocalContext.current

    Services.Inject { services ->
        if (viewModel.experience == null) {
            LaunchedEffect(true) {
                viewModel.loadExperience(context, services.fontLoader, data)
            }
        }

        CompositionLocalProvider(Environment.LocalAssetContext provides UnpackedTempfilesZipContext) {
            viewModel.experience?.let { experienceModel ->
                CompositionLocalProvider(
                    Environment.LocalTypefaceMapping provides viewModel.typefaceMapping,
                    Environment.LocalUserInfo provides userInfo,
                    // provide the in-file URL parameters because query parameters can't be specified when loading from a bytearray
                    Environment.LocalUrlParameters provides experienceModel.urlParameters
                ) {
                    Experience(experienceModel, modifier = modifier)
                }
            }
        }
    }
}

/**
 * This presents an [ExperienceModel].
 *
 * Note this expects a [Environment.LocalAssetContext] and [Environment.LocalTypefaceMapping] to
 * have been setup.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Experience(
    experience: ExperienceModel,
    modifier: Modifier = Modifier,
    initialScreenID: String? = null
) {
    val tag = "Experience"

    // As the initial [app.judo.sdk.compose.model.nodes.Node] is always a [Screen], we create its [ScreenLayer] manually.
    // This [ScreenLayer] begins the chain for creating every children [app.judo.sdk.compose.model.nodes.Node] in the tree.
    var screenNode by remember { mutableStateOf<Screen?>(null) }
    var screens by remember { mutableStateOf<List<Screen>>(emptyList()) }
    val navController = rememberAnimatedNavController()
    val context = LocalContext.current

    // We need to hold Environment.LocalData information for passing it into child screens.
    // Serializing the Any type for using the navigation arguments is not achievable at the moment.
    val localDataByScreenId: MutableMap<String, Any?> = mutableMapOf()
    val closeActivity = { (context as? Activity)?.finish() }
    val popBackNavigationStack = {
        localDataByScreenId.remove(navController.currentBackStackEntry?.id)
        navController.popBackStack()
    }

    val urlScreenID = Environment.LocalUrlParameters.current["screenID"]
    Services.Inject { _ ->
        LaunchedEffect(initialScreenID, urlScreenID) {
            screens = experience.nodes.filterIsInstance<Screen>()

            val initialScreen = screens.firstOrNull { it.id == initialScreenID }
                ?: screens.firstOrNull { it.id == urlScreenID }
                ?: screens.firstOrNull { it.id == experience.initialScreenID }
                ?: screens.firstOrNull()

            if (initialScreen == null) {
                Log.e(tag, "Could not find initial Screen in this Judo experience.")
                return@LaunchedEffect
            }

            screenNode = initialScreen
        }

        // this is the authorizer provided by the user using the `.judoAuthorize` modifier.
        // [AuthorizerCallbackConsumer] below is used to capture that value and set it here.
        var authorizerFromModifier by remember { mutableStateOf<AuthorizerHandler?>(null) }
        var trackScreenCallback by remember { mutableStateOf<TrackScreenHandler?>(null) }

        // now, for both authorizer and userinfo, default to values provided from the file if they
        // have not been set by the user.

        // note: wrapped this in a little closure to keep it lazy and thus avoid a recomposition loop,
        // because it seems that we cannot read authorizerFromModifier in this scope.
        val authorize = { urlRequest: URLRequest ->
            val f = authorizerFromModifier ?: { request ->
                experience.authorizers.forEach { authorizer ->
                    authorizer.authorize(request)
                }
            }

            f(urlRequest)
        }

        val userInfoProvider = Environment.LocalUserInfo.current ?: {
            // if user info hasn't been provided, then we'll provide the defaults bundled with
            // the experience.
            experience.userInfo
        }

        screenNode?.let { initialScreen ->
            CompositionLocalProvider(
                Environment.LocalNavigateToScreen provides { destination, localData ->
                    localDataByScreenId[destination] = localData
                    navController.navigate(destination)
                },
                Environment.LocalNavigateUp provides { if (navController.previousBackStackEntry != null) popBackNavigationStack() else closeActivity() },
                Environment.LocalDismissExperience provides { closeActivity() },
                Environment.LocalExperienceModel provides experience,
                Environment.LocalDocumentFonts provides experience.fonts,
                Environment.LocalIsDarkTheme provides experience.appearance.getDarkModeValue(),
                Environment.LocalTrackScreenHandler provides trackScreenCallback,
                Environment.LocalAuthorizerHandler provides authorize,
                Environment.LocalUserInfo provides userInfoProvider
            ) {
                // Every screen needs to be created as a potential route here.
                // The first one is chosen by its id, so it's the only one to be composed at this point.
                AnimatedNavHost(
                    navController,
                    startDestination = initialScreen.id,
                    modifier = modifier.then(
                        AuthorizerCallbackConsumer { newCallback ->
                            // null check to prevent a certain bad case where the callback consumer
                            // (for unknown reasons) causes cycling back and forth between callback
                            // value from the authorizer modifier and null.
                            if (newCallback != null) {
                                authorizerFromModifier = newCallback
                            }
                        }
                    ).then(
                        TrackScreenCallbackConsumer { newCallback ->
                            // null check to prevent a certain bad case where the callback consumer
                            // (for unknown reasons) causes cycling back and forth between callback
                            // value from the track screen modifier and null.
                            if (newCallback != null) {
                                trackScreenCallback = newCallback
                            }
                        }
                    )
                ) {
                    screens.forEach { screen ->
                        composable(
                            route = screen.id,
                            enterTransition = { enterTransition() },
                            exitTransition = { exitTransition() },
                            popEnterTransition = { popEnterTransition() },
                            popExitTransition = { popExitTransition() }
                        ) {
                            BackHandler(enabled = screen.id != initialScreen.id) {
                                popBackNavigationStack()
                            }

                            CompositionLocalProvider(
                                Environment.LocalData provides localDataByScreenId[screen.id]
                            ) {
                                ScreenLayer(
                                    node = screen,
                                    appearance = experience.appearance
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * This view model is responsible for loading an Experience document ZIP container from a local
 * source.
 */
internal class LoadExperienceViewModel : ViewModel() {
    val tag = "LoadExperienceViewModel"
    var experience: ExperienceModel? by mutableStateOf(null)
    var typefaceMapping: FontLoader.TypeFaceMapping? by mutableStateOf(null)

    fun loadExperience(
        context: Context,
        fontLoader: FontLoader,
        zipFileUri: Uri
    ) {
        viewModelScope.launch(context = Dispatchers.IO) {
            ZipInputStream(context.contentResolver.openInputStream(zipFileUri)).use { zipInputStream ->
                try {
                    experience = ExperienceModel.fromZipStream(context, zipInputStream)
                    val fontSources = experience!!.fonts.flatMap { font ->
                        font.sources.apply {
                            if (this == null) Log.w(tag, "This file saved before Judo 1.11, custom fonts will not work.")
                        } ?: emptyList()
                    }

                    typefaceMapping = fontLoader.getTypefaceMappings(
                        context,
                        UnpackedTempfilesZipContext,
                        fontSources
                    )
                } catch (exception: Exception) {
                    Log.e(tag, "Unable to load experience: $exception")
                }
            }
        }
    }

    fun loadExperience(context: Context, fontLoader: FontLoader, zipFileData: ByteArray) {
        viewModelScope.launch(context = Dispatchers.IO) {
            ZipInputStream(ByteArrayInputStream(zipFileData)).use { zipInputStream ->
                try {
                    experience = ExperienceModel.fromZipStream(context, zipInputStream)

                    val fontSources = experience!!.fonts.flatMap { font ->
                        font.sources.apply {
                            if (this == null) Log.w(tag, "This file saved before Judo 1.11, custom fonts will not work.")
                        } ?: emptyList()
                    }

                    typefaceMapping = fontLoader.getTypefaceMappings(
                        context,
                        UnpackedTempfilesZipContext,
                        fontSources
                    )
                } catch (exception: Exception) {
                    Log.e(tag, "Unable to load experience: $exception")
                }
            }
        }
    }
}

/**
 * This is a [ModifierLocalConsumer] which allows us to get the
 * [Environment.ModifierLocalAuthorizerHandler] from modifier-space into layout space.
 */
private class AuthorizerCallbackConsumer(
    private val valueUpdated: (AuthorizerHandler?) -> Unit
) : ModifierLocalConsumer {
    override fun onModifierLocalsUpdated(scope: ModifierLocalReadScope) {
        scope.apply {
            valueUpdated(Environment.ModifierLocalAuthorizerHandler.current)
        }
    }
}

/**
 * This is a [ModifierLocalConsumer] which allows us to get the
 * [Environment.ModifierTrackScreenHandler] from modifier-space into layout space.
 */
private class TrackScreenCallbackConsumer(
    private val valueUpdated: (TrackScreenHandler?) -> Unit
) : ModifierLocalConsumer {
    override fun onModifierLocalsUpdated(scope: ModifierLocalReadScope) {
        scope.apply {
            valueUpdated(Environment.ModifierTrackScreenHandler.current)
        }
    }
}
