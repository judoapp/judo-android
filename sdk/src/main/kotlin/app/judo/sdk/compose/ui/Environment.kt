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

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.navigation.NavHostController
import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.nodes.Screen
import app.judo.sdk.compose.model.values.Axis
import app.judo.sdk.compose.model.values.DocumentFont
import app.judo.sdk.compose.model.values.ExperienceModel
import app.judo.sdk.compose.ui.fonts.FontLoader
import app.judo.sdk.compose.vendor.accompanist.pager.ExperimentalPagerApi
import app.judo.sdk.compose.vendor.accompanist.pager.PagerState

/**
 * This object contains all the Composition Local properties used within the Compose hierarchy.
 *
 * There are analogous to the SwiftUI environment.
 */
internal object Environment {
    /**
     * Various singletons and services.
     */
    val LocalServices = compositionLocalOf<Services?> { null }

    /**
     * This composition local contains the data loaded by a parent data source, if any.
     */
    val LocalData = compositionLocalOf<Any?> { null }

    /**
     * The URL parameters (if applicable) provided with the URL used to load the experience.
     */
    val LocalUrlParameters = compositionLocalOf<Map<String, String>> { emptyMap() }

    /**
     * A method for retrieving User Info, if one is set.
     */
    val LocalUserInfo = compositionLocalOf<(() -> Map<String, Any>)?> { null }

    /**
     * The remote URL (if applicable) that this experience was loaded from.
     */
    val LocalExperienceUrl = compositionLocalOf<Uri?> { null }

    /**
     * Remote id for the current Experience. Updated when loading a remote Experience if applicable.
     */
    val LocalExperienceId = compositionLocalOf<String?> { null }

    /**
     * Remote name for the current Experience. Updated when loading a remote Experience if applicable.
     */
    val LocalExperienceName = compositionLocalOf<String?> { null }

    /**
     * The node object from the experience model that this layer is being rendered from.
     *
     * It serves to punch through a few layers to aid in certain cases where heuristics
     * are necessary (such as getting details about the associated node within the Shadow modifier).
     */
    val LocalNode = compositionLocalOf<Node?> { null }

    /**
     * The experience model object that this layer is being rendered from.
     */
    val LocalExperienceModel = compositionLocalOf<ExperienceModel?> { null }

    /**
     * The screen model object that this layer is being rendered from.
     */
    val LocalScreen = compositionLocalOf<Screen?> { null }

    /**
     * Indicates the dark mode appearance setting of the parent screen.
     */
    val LocalIsDarkTheme = compositionLocalOf<Boolean> { false }

    /**
     * Indicates the axis of the nearest parent stack.
     */
    val LocalStackAxis = compositionLocalOf<Axis?> { null }

    /**
     * In cases where a collection can contain multiple carousels, this index provides
     * disambiguation to distinguish the states of the carousels.
     */
    val LocalCollectionIndex = compositionLocalOf { 0 }
    val LocalCarouselStates = mutableStateMapOf<ViewID, CarouselState>()

    /**
     * The anonymous function to be called when a [app.judo.sdk.compose.model.values.Action.PerformSegue] action is activated.
     * Set by [Experience], this lets any child easily access the overarching Experience navigation graph (through [NavHostController]).
     */
    val LocalNavigateToScreen = compositionLocalOf<((String, Any?) -> Unit)?> { null }
    val LocalNavigateUp = compositionLocalOf<(() -> Unit)?> { null }
    val LocalDismissExperience = compositionLocalOf<(() -> Unit)?> { null }

    val LocalAssetContext = compositionLocalOf<AssetContext> { UnpackedTempfilesZipContext }

    /**
     * Set by [Experience], this allows the registered callback for an authorizer to be used
     * by any Data Sources within the Experience.
     */
    val LocalAuthorizerHandler = compositionLocalOf<AuthorizerHandler?> { null }

    /**
     * Set by [Experience], this allows the registered callback for an authorizer to be used
     * by any Data Sources within the Experience.
     */
    val LocalTrackScreenHandler = compositionLocalOf<TrackScreenHandler?> { null }

    /**
     * Typefaces loaded from font sources.
     */
    val LocalTypefaceMapping = compositionLocalOf<FontLoader.TypeFaceMapping?> { null }

    /**
     * Pre-defined font styles specified in the document.
     */
    val LocalDocumentFonts = compositionLocalOf<List<DocumentFont>> { emptyList() }

    /**
     * The registered callback, if set, of a custom action handler.
     */
    val ModifierLocalCustomActionHandler = modifierLocalOf<CustomActionHandler?> { null }

    /**
     * The registered callback, if set, of an authorizer handler.
     */
    val ModifierLocalAuthorizerHandler = modifierLocalOf<AuthorizerHandler?> { null }

    /**
     * The registered callback, if set, for screen tracking analytics.
     */
    val ModifierTrackScreenHandler = modifierLocalOf<TrackScreenHandler?> { null }
}

internal data class ViewID(
    val nodeID: String,
    val collectionIndex: Int
)

@OptIn(ExperimentalPagerApi::class)
internal data class CarouselState(
    val pagerState: PagerState,
    val startIndex: Int,
    val collectionSize: Int,
    val isLoopEnabled: Boolean
)
