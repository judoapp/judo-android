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

package app.judo.sdk.ui.state

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates

internal sealed class ViewState {
    open var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()
}

internal data class AppBarViewState(
    val id: String,
    val hideUpIcon: Boolean,
    val buttonColor: ColorVariants,
    val title: String,
    val titleFont: Font,
    val titleColor: ColorVariants,
    val backgroundColor: ColorVariants,
    val typeface: Typeface? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class AudioViewState(
    val id: String,
    val sourceURL: String,
    val autoPlay: Boolean,
    val looping: Boolean,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class CarouselViewState(
    val id: String,
    val isLoopEnabled: Boolean,
    val opacity: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val aspectRatio: Float? = null,
    val accessibility: Accessibility? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class DividerViewState(
    val id: String,
    val backgroundColor: ColorVariants,
    val offset: Point? = null,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class HStackViewState(
    val id: String,
    val spacing: Float,
    val alignment: VerticalAlignment,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val border: Border? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class IconViewState(
    val id: String,
    val icon: NamedIcon,
    val color: ColorVariants,
    val pointSize: Int,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val border: Border? = null,
    val mask: ViewState? = null,
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class ImageViewState(
    val id: String,
    val resolution: Float,
    val resizingMode: ResizingMode,
    val blurHash: String? = null,
    val darkModeBlurHash: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val darkModeImageWidth: Int? = null,
    val darkModeImageHeight: Int? = null,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    val action: (() -> Unit)? = null,
    val getImage: suspend () -> Drawable? = { null },
    val getDarkModeImage: suspend () -> Drawable? = { null },
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class MenuItemViewState(
    val id: String,
    val title: String,
    val showAsAction: MenuItemVisibility,
    val iconMaterialName: String,
    val contentDescription: String? = null,
    val actionDescription: String? = null,
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class PageControlViewState(
    val id: String,
    val hidesForSinglePage: Boolean,
    val carouselID: String? = null,
    val style: PageControlStyleViewState,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class RectangleViewState(
    val id: String,
    val fill: Fill,
    val cornerRadius: Float,
    val aspectRatio: Float? = null,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val border: Border? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class ScreenViewState(
    val id: String,
    val backgroundColor: ColorVariants,
    val androidStatusBarBackgroundColor: ColorVariants,
    val androidStatusBarStyle: StatusBarStyle,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class ScrollContainerViewState(
    val id: String,
    val name: String? = null,
    val axis: Axis,
    val disableScrollBar: Boolean,
    val aspectRatio: Float? = null,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class SpacerViewState(
    val id: String,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class TextViewState(
    val id: String,
    val text: String,
    val font: Font,
    val textColor: ColorVariants,
    val textAlignment: TextAlignment,
    val lineLimit: Int? = null,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    val typeface: Typeface? = null,
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class VideoViewState(
    val id: String,
    val sourceURL: String,
    val resizingMode: VideoResizingMode,
    val autoPlay: Boolean,
    val showControls: Boolean,
    val looping: Boolean,
    val removeAudio: Boolean,
    val padding: Padding? = null,
    val aspectRatio: Float? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    val getPosterImage: suspend () -> Drawable? = { null },
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class VStackViewState(
    val id: String,
    val spacing: Float,
    val alignment: HorizontalAlignment,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val border: Border? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class WebViewViewState(
    val id: String,
    val source: WebViewSource,
    val isScrollEnabled: Boolean,
    val aspectRatio: Float? = null,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val accessibility: Accessibility? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()

internal data class ZStackViewState(
    val id: String,
    val alignment: Alignment,
    val padding: Padding? = null,
    val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val border: Border? = null,
    val mask: ViewState? = null,
    val backgroundAndAlignment: Pair<ViewState, Alignment>? = null,
    val overlayAndAlignment: Pair<ViewState, Alignment>? = null,
    val action: (() -> Unit)? = null,
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates(),
) : ViewState()