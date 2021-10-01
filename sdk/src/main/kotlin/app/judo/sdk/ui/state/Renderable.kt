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
import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.emptyDataContext
import app.judo.sdk.core.interfaces.Actionable
import app.judo.sdk.core.interfaces.Imagery
import app.judo.sdk.core.interfaces.Typefaceable
import app.judo.sdk.core.interpolation.ProtoInterpolator
import app.judo.sdk.core.lang.Interpolatable
import app.judo.sdk.core.lang.Translatable
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.core.utils.Translator
import java.util.*

internal sealed class Renderable {

    abstract val node: Node

    open var dataContext: DataContext = emptyDataContext()

    abstract fun toViewState(): ViewState?

    protected fun Pair<Renderable, Alignment>?.toViewState(): Pair<ViewState, Alignment>? {
        return this?.let { (modelState, alignment) ->
            modelState.toViewState()?.let { viewState -> viewState to alignment }
        }
    }

}

internal data class AppBarRenderable(
    override val node: AppBar,
    var typeface: Typeface? = null,
    var computedTitle: String = node.title,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable {

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (node.titleFont as? Font.Custom)?.fontName?.let {
            typeface = typefaces[it]
        }
    }

    override fun translate(translator: Translator) {
        computedTitle = translator.translate(computedTitle)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {
        computedTitle = interpolator(computedTitle, dataContext) ?: computedTitle
    }

    override fun toViewState(): ViewState {
        return with(node) {
            AppBarViewState(
                id = id,
                hideUpIcon = hideUpIcon,
                buttonColor = buttonColor,
                title = computedTitle,
                titleFont = titleFont,
                titleColor = titleColor,
                backgroundColor = backgroundColor,
                typeface = this@AppBarRenderable.typeface
            )
        }
    }
}

internal data class AudioRenderable(
    override val node: Audio,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedSourceURL: String = node.sourceURL,
    var computedAccessibility: Accessibility? = node.accessibility,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery {

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedSourceURL = interpolator(
            dataContext = dataContext,
            theTextToInterpolate = computedSourceURL
        ) ?: computedSourceURL

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ViewState {
        return with(node) {
            AudioViewState(
                id = id,
                sourceURL = computedSourceURL,
                autoPlay = autoPlay,
                looping = looping,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = computedAccessibility,
                mask = this@AudioRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
            )
        }
    }
}

internal data class CarouselRenderable(
    override val node: Carousel,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedAccessibility: Accessibility? = node.accessibility,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery {

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ViewState {
        return with(node) {
            CarouselViewState(
                id = id,
                isLoopEnabled = isLoopEnabled,
                opacity = opacity,
                offset = offset,
                shadow = shadow,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                aspectRatio = aspectRatio,
                accessibility = computedAccessibility,
                mask = this@CarouselRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
            )
        }
    }
}

internal data class CollectionRenderable(
    override val node: app.judo.sdk.api.models.Collection,
    var computedFilters: List<Condition> = emptyList(),
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Interpolatable {

    override fun interpolate(interpolator: ProtoInterpolator) {

        computedFilters = node.filters.map { condition ->
            condition.copy(
                value = (condition.value as? String)?.let { conditionValue ->
                    interpolator(conditionValue, dataContext)
                } ?: condition.value
            )
        }

    }

    override fun toViewState(): ViewState? = null

}

internal data class ConditionalRenderable(
    override val node: Conditional,
    var computedConditions: List<Condition> = node.conditions,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Interpolatable {

    override fun interpolate(interpolator: ProtoInterpolator) {

        computedConditions = node.conditions.map { condition ->
            condition.copy(
                value = (condition.value as? String)?.let { conditionValue ->
                    interpolator(conditionValue, dataContext)
                } ?: condition.value
            )
        }

    }

    override fun toViewState(): ViewState? = null
}

internal data class DataSourceRenderable(
    override val node: DataSource,
    var data: Any? = null,
    override var dataContext: DataContext = emptyDataContext()
) : Renderable(), Interpolatable {

    override fun interpolate(interpolator: ProtoInterpolator) {

        data = (data as? String)?.let {
            interpolator(it, dataContext) ?: data
        }

    }

    override fun toViewState(): ViewState? = null
}

internal data class DividerRenderable(
    override val node: Divider,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable() {
    override fun toViewState(): ViewState {
        return with(node) {
            DividerViewState(
                id = id,
                backgroundColor = backgroundColor,
                offset = offset,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                sizeAndCoordinates = sizeAndCoordinates
            )
        }
    }
}

internal data class HStackRenderable(
    override val node: HStack,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedAction: Action? = node.action,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

    }

    override fun toViewState(): ViewState {
        return with(node) {
            HStackViewState(
                id = id,
                spacing = spacing,
                alignment = alignment,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                border = border,
                mask = this@HStackRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
                action = actionHandler
            )
        }
    }
}

internal data class IconRenderable(
    override val node: Icon,
    val mask: Renderable? = null,
    var computedAction: Action? = node.action,
    var computedAccessibility: Accessibility? = node.accessibility,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }


    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ViewState {
        return with(node) {
            IconViewState(
                id = id,
                icon = icon,
                color = color,
                pointSize = pointSize,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = computedAccessibility,
                border = border,
                mask = this@IconRenderable.mask?.toViewState(),
                action = actionHandler
            )
        }
    }
}

internal data class ImageRenderable(
    override val node: Image,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedAction: Action? = node.action,
    var computedImageURL: String = node.imageURL,
    var imageGetter: suspend () -> Drawable? = { null },
    var computedDarkModeImageURL: String? = node.darkModeImageURL,
    var darkModeImageGetter: suspend () -> Drawable? = { null },
    var computedAccessibility: Accessibility? = node.accessibility,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)

        imageGetter = {
            imageService.getImageAsync(
                ImageService.Request(
                    computedImageURL
                )
            ).await().drawable
        }

        computedDarkModeImageURL?.let {
            darkModeImageGetter = {
                imageService.getImageAsync(ImageService.Request(it)).await().drawable
            }
        }

    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

        computedImageURL = interpolator(computedImageURL, dataContext) ?: computedImageURL
        computedDarkModeImageURL = computedDarkModeImageURL?.let { interpolator(it, dataContext) }

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ImageViewState {
        return with(node) {
            ImageViewState(
                id = id,
                resolution = resolution,
                resizingMode = resizingMode,
                blurHash = blurHash,
                darkModeBlurHash = darkModeBlurHash,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                darkModeImageWidth = darkModeImageWidth,
                darkModeImageHeight = darkModeImageHeight,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = computedAccessibility,
                mask = this@ImageRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
                getImage = imageGetter,
                getDarkModeImage = darkModeImageGetter,
                action = actionHandler,
            )
        }
    }
}

internal data class MenuItemRenderable(
    override val node: MenuItem,
    var computedTitle: String = node.title,
    var computedAction: Action? = node.action,
    var computedContentDescription: String? = node.contentDescription,
    var computedActionDescription: String? = node.actionDescription,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }


    override fun translate(translator: Translator) {
        computedTitle = translator.translate(computedTitle)
        computedContentDescription = computedContentDescription?.let {
            translator.translate(it)
        }
        computedActionDescription = computedActionDescription?.let {
            translator.translate(it)
        }
    }

    override fun interpolate(interpolator: ProtoInterpolator) {
        computedTitle = interpolator(computedTitle, dataContext) ?: computedTitle

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

        computedContentDescription =
            computedContentDescription?.let {
                interpolator(it, dataContext)
            }

        computedActionDescription =
            computedActionDescription?.let {
                interpolator(it, dataContext)
            }
    }

    override fun toViewState(): ViewState {
        return with(node) {
            MenuItemViewState(
                id = id,
                title = computedTitle,
                showAsAction = showAsAction,
                iconMaterialName = iconMaterialName,
                contentDescription = computedContentDescription,
                actionDescription = computedActionDescription,
                action = actionHandler,
            )
        }
    }
}

internal data class PageControlRenderable(
    override val node: PageControl,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var renderablePageControlStyle: RenderablePageControlStyle,
    var computedAccessibility: Accessibility? = node.accessibility,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery {

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (renderablePageControlStyle as? RenderablePageControlStyle.ImagePageControlStyle)?.let {
            it.currentImage.setImageGetters(imageService)
            it.normalImage.setImageGetters(imageService)
        }
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (renderablePageControlStyle as? RenderablePageControlStyle.ImagePageControlStyle)?.let {
            it.currentImage.setTypeface(typefaces)
            it.normalImage.setTypeface(typefaces)
        }
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        (renderablePageControlStyle as? RenderablePageControlStyle.ImagePageControlStyle)?.let {
            it.currentImage.translate(translator)
            it.normalImage.translate(translator)
        }

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        (renderablePageControlStyle as? RenderablePageControlStyle.ImagePageControlStyle)?.let {
            it.currentImage.interpolate(interpolator)
            it.normalImage.interpolate(interpolator)
        }

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): PageControlViewState {
        return with(node) {

            val pageControlStyleViewState: PageControlStyleViewState = when (
                val renderableStyle = renderablePageControlStyle
            ) {
                RenderablePageControlStyle.DarkPageControlStyle -> PageControlStyleViewState.DarkPageControlStyle
                RenderablePageControlStyle.DefaultPageControlStyle -> PageControlStyleViewState.DefaultPageControlStyle
                RenderablePageControlStyle.InvertedPageControlStyle -> PageControlStyleViewState.InvertedPageControlStyle
                RenderablePageControlStyle.LightPageControlStyle -> PageControlStyleViewState.LightPageControlStyle
                is RenderablePageControlStyle.CustomPageControlStyle -> PageControlStyleViewState.CustomPageControlStyle(
                    normalColor = renderableStyle.normalColor,
                    currentColor = renderableStyle.currentColor
                )
                is RenderablePageControlStyle.ImagePageControlStyle -> PageControlStyleViewState.ImagePageControlStyle(
                    normalColor = renderableStyle.normalColor,
                    currentColor = renderableStyle.currentColor,
                    normalImage = renderableStyle.normalImage.toViewState(),
                    currentImage = renderableStyle.currentImage.toViewState()
                )
            }

            PageControlViewState(
                id = id,
                hidesForSinglePage = hidesForSinglePage,
                carouselID = carouselID,
                style = pageControlStyleViewState,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = accessibility,
                mask = this@PageControlRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
            )

        }
    }
}

internal data class RectangleRenderable(
    override val node: Rectangle,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedAction: Action? = node.action,
    var computedAccessibility: Accessibility? = node.accessibility,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }


    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ViewState {
        return with(node) {
            RectangleViewState(
                id = id,
                fill = fill,
                cornerRadius = cornerRadius,
                aspectRatio = aspectRatio,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = computedAccessibility,
                border = border,
                mask = this@RectangleRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
                action = actionHandler,
            )
        }
    }
}

internal data class ScrollContainerRenderable(
    override val node: ScrollContainer,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery {

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {
        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {
        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
    }

    override fun toViewState(): ViewState {
        return with(node) {
            ScrollContainerViewState(
                id = id,
                name = name,
                axis = axis,
                disableScrollBar = disableScrollBar,
                aspectRatio = aspectRatio,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                mask = this@ScrollContainerRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
            )

        }
    }
}

internal data class ScreenRenderable(
    override val node: Screen,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable() {
    override fun toViewState(): ViewState {
        return with(node) {
            ScreenViewState(
                id = id,
                backgroundColor = backgroundColor,
                androidStatusBarBackgroundColor = androidStatusBarBackgroundColor,
                androidStatusBarStyle = androidStatusBarStyle,
            )
        }
    }
}

internal data class SpacerRenderable(
    override val node: Spacer,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable() {
    override fun toViewState(): ViewState {
        return with(node) {
            SpacerViewState(
                id = id,
                frame = frame,
                layoutPriority = layoutPriority
            )
        }
    }
}

internal data class TextRenderable(
    override val node: Text,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedText: String = node.text,
    var computedAction: Action? = node.action,
    var computedAccessibility: Accessibility? = node.accessibility,
    var typeface: Typeface? = null,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }


    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)

        (node.font as? Font.Custom)?.fontName?.let {
            typeface = typefaces[it]
        }

    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        computedText = translator.translate(computedText)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

        computedText = interpolator(computedText, dataContext) ?: computedText

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ViewState {
        return with(node) {

            val transformedText = when (transform) {
                TextTransform.UPPERCASE -> {
                    computedText.toUpperCase(Locale.getDefault())
                }
                TextTransform.LOWERCASE -> {
                    computedText.toLowerCase(Locale.getDefault())
                }
                null -> computedText
            }

            TextViewState(
                id = id,
                text = transformedText,
                font = font,
                textColor = textColor,
                textAlignment = textAlignment,
                lineLimit = lineLimit,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = computedAccessibility,
                mask = this@TextRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
                typeface = this@TextRenderable.typeface,
                action = actionHandler,
            )

        }
    }
}

internal data class VideoRenderable(
    override val node: Video,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var posterImageGetter: suspend () -> Drawable? = { null },
    var computedSourceURL: String = node.sourceURL,
    var computedPosterSourceURL: String? = node.posterImageURL,
    var computedAction: Action? = node.action,
    var computedAccessibility: Accessibility? = node.accessibility,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }


    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)

        posterImageGetter = {
            computedPosterSourceURL?.let {
                imageService.getImageAsync(ImageService.Request(it)).await().drawable
            }
        }

    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)


        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

        computedSourceURL = interpolator(computedSourceURL, dataContext) ?: computedSourceURL

        computedPosterSourceURL = computedPosterSourceURL?.let {
            interpolator(it, dataContext) ?: computedPosterSourceURL
        }

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ViewState {
        return with(node) {
            VideoViewState(
                id = id,
                sourceURL = computedSourceURL,
                resizingMode = resizingMode,
                autoPlay = autoPlay,
                showControls = showControls,
                looping = looping,
                removeAudio = removeAudio,
                padding = padding,
                aspectRatio = aspectRatio,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = computedAccessibility,
                mask = this@VideoRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
                getPosterImage = posterImageGetter,
                action = actionHandler,
            )
        }
    }
}

internal data class VStackRenderable(
    override val node: VStack,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedAction: Action? = node.action,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {
        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {
        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction

    }

    override fun toViewState(): ViewState {
        return with(node) {
            VStackViewState(
                id = id,
                spacing = spacing,
                alignment = alignment,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                border = border,
                mask = this@VStackRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
                action = actionHandler,
            )
        }
    }
}

internal data class WebViewRenderable(
    override val node: WebView,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedSource: WebViewSource = node.source,
    var computedAccessibility: Accessibility? = node.accessibility,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery {

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {

        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)

        computedAccessibility = computedAccessibility?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {

        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedSource = computedSource.let { source ->
            when (source) {
                is WebViewSource.HTML -> {
                    source
                }
                is WebViewSource.URL -> {
                    val value = interpolator(source.value, dataContext) ?: source.value
                    WebViewSource.URL(value)
                }
            }
        }

        computedAccessibility = computedAccessibility?.interpolate(
            dataContext = dataContext,
            interpolator = interpolator
        )
    }

    override fun toViewState(): ViewState {
        return with(node) {
            WebViewViewState(
                id = id,
                source = computedSource,
                isScrollEnabled = isScrollEnabled,
                aspectRatio = aspectRatio,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                accessibility = computedAccessibility,
                mask = this@WebViewRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
            )
        }
    }
}

internal data class ZStackRenderable(
    override val node: ZStack,
    val mask: Renderable? = null,
    val backgroundAndAlignment: Pair<Renderable, Alignment>? = null,
    val overlayAndAlignment: Pair<Renderable, Alignment>? = null,
    var computedAction: Action? = node.action,
    var actionHandler: (() -> Unit)? = null,
    override var dataContext: DataContext = emptyDataContext(),
) : Renderable(), Translatable, Interpolatable, Typefaceable, Imagery, Actionable {

    override fun setActionHandler(
        actionHandler: (
            node: Node,
            action: Action,
            dataContext: DataContext
        ) -> Unit
    ) {
        computedAction?.let { action ->
            this.actionHandler = {
                actionHandler(node, action, dataContext)
            }
        }
    }

    override fun setImageGetters(imageService: ImageService) {
        (mask as? Imagery)?.setImageGetters(imageService)
        (backgroundAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
        (overlayAndAlignment?.first as? Imagery)?.setImageGetters(imageService)
    }

    override fun setTypeface(typefaces: Map<String, Typeface>) {
        (mask as? Typefaceable)?.setTypeface(typefaces)
        (backgroundAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
        (overlayAndAlignment?.first as? Typefaceable)?.setTypeface(typefaces)
    }

    override fun translate(translator: Translator) {
        (mask as? Translatable)?.translate(translator)
        (backgroundAndAlignment?.first as? Translatable)?.translate(translator)
        (overlayAndAlignment?.first as? Translatable)?.translate(translator)
    }

    override fun interpolate(interpolator: ProtoInterpolator) {
        (mask as? Interpolatable)?.interpolate(interpolator)
        (backgroundAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)
        (overlayAndAlignment?.first as? Interpolatable)?.interpolate(interpolator)

        computedAction = computedAction?.interpolate(dataContext, interpolator) ?: computedAction
    }

    override fun toViewState(): ViewState {
        return with(node) {
            ZStackViewState(
                id = id,
                alignment = alignment,
                padding = padding,
                frame = frame,
                layoutPriority = layoutPriority,
                offset = offset,
                shadow = shadow,
                opacity = opacity,
                border = border,
                mask = this@ZStackRenderable.mask?.toViewState(),
                backgroundAndAlignment = backgroundAndAlignment.toViewState(),
                overlayAndAlignment = overlayAndAlignment.toViewState(),
                action = actionHandler,
            )
        }
    }
}

internal fun Action.interpolate(dataContext: DataContext, interpolator: ProtoInterpolator): Action {
    return when (this) {
        is Action.Close -> {
            this
        }
        is Action.Custom -> {
            this
        }
        is Action.OpenURL -> {
            copy(
                url = interpolator(url, dataContext) ?: url
            )
        }
        is Action.PerformSegue -> {
            this
        }
        is Action.PresentWebsite -> {
            copy(
                url = interpolator(url, dataContext) ?: url
            )
        }
    }
}

internal fun Accessibility.translate(translator: Translator): Accessibility {
    return copy(
        label = label?.let { translator.translate(it) }
    )
}

internal fun Accessibility.interpolate(
    dataContext: app.judo.sdk.core.data.DataContext,
    interpolator: ProtoInterpolator
): Accessibility {
    return copy(
        label = label?.let {
            interpolator(dataContext = dataContext, theTextToInterpolate = it) ?: it
        }
    )
}
