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

package app.judo.sdk.ui.layout.composition

import android.content.Context
import android.view.View
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.construction.construct
import app.judo.sdk.ui.layout.composition.positioning.computePosition
import app.judo.sdk.ui.layout.composition.sizing.computePosition
import app.judo.sdk.ui.layout.composition.sizing.computeSize
import app.judo.sdk.ui.layout.composition.sizing.isNearestParentStackHorizontal
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/***
 * Build the layout of the tree through traversing the nodes
 */
internal suspend fun TreeNode.toLayout(
    context: Context,
    resolvers: Resolvers
): List<View> {
    return when (val layer = value as Layer) {
        is Rectangle -> layer.construct(context, this, resolvers)
        is Image -> layer.construct(context, this, resolvers)
        is WebView -> {
            withContext(Dispatchers.Main) {
                layer.construct(context, this@toLayout, resolvers)
            }
        }
        is VStack -> layer.construct(context, this, resolvers)
        is ZStack -> layer.construct(context, this, resolvers)
        is HStack -> layer.construct(context, this, resolvers)
        is Divider -> layer.construct(context, resolvers)
        is Carousel -> layer.construct(context, this, resolvers)
        is PageControl -> layer.construct(context, this, resolvers)
        is ScrollContainer -> layer.construct(context, this, resolvers)
        is Audio -> {
            withContext(Dispatchers.Main) {
                layer.construct(context, this@toLayout, resolvers)
            }
        }
        is Video -> {
            withContext(Dispatchers.Main) {
                layer.construct(context, this@toLayout, resolvers)
            }
        }
        is Text -> layer.construct(context, this, resolvers)
        is Icon -> layer.construct(context, this, resolvers)
        is Spacer -> listOf(View(context))
        else -> throw IllegalStateException()
    }
}

/**
 * Computes the size of the view and relative positioning from parent and frame.
 */
internal fun TreeNode.computeSize(context: Context, dimensions: Dimensions) {
    when (val layer = this.value as Layer) {
        is Rectangle -> layer.computeSize(context, this, dimensions)
        is Image -> layer.computeSize(context, this, dimensions)
        is WebView -> layer.computeSize(context, this, dimensions)
        is ZStack -> layer.computeSize(context, this, dimensions)
        is VStack -> layer.computeSize(context, this, dimensions)
        is Screen -> layer.computeSize(context, this, dimensions)
        is ScrollContainer -> layer.computeSize(context, this, dimensions)
        is Divider -> layer.computeSize(context, this, dimensions)
        is PageControl -> layer.computeSize(context, this, dimensions)
        is Carousel -> layer.computeSize(context,this, dimensions)
        is Text -> layer.computeSize(context, this, dimensions)
        is HStack -> layer.computeSize(context, this, dimensions)
        is Spacer -> layer.computeSize(context, this, dimensions)
        is Audio -> layer.computeSize(context, this, dimensions)
        is Video -> layer.computeSize(context, this, dimensions)
        is Icon -> layer.computeSize(context, dimensions)
        else -> throw IllegalStateException()
    }
}

internal fun TreeNode.clearPositioning() {
    when (val layer = this.value as Layer) {
        is Rectangle -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
        }
        is Image -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
        }
        is WebView -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
        }
        is ZStack -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
            this.children.forEach { it.clearPositioning() }
        }
        is VStack -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
            this.children.forEach { it.clearPositioning() }
        }
        is HStack -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
            this.children.forEach { it.clearPositioning() }
        }
        is Screen -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            this.children.forEach { it.clearPositioning() }
        }
        is ScrollContainer -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
            this.children.forEach { it.clearPositioning() }
        }
        is Divider -> layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
        is PageControl -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
        }
        is Carousel -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
            this.children.forEach { it.clearPositioning() }
        }
        is Text -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
        }
        is Spacer -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
        }
        is Audio -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
        }
        is Video -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
            layer.background?.node?.singleNodeClearPositioning()
            layer.overlay?.node?.singleNodeClearPositioning()
        }
        is Icon -> {
            layer.sizeAndCoordinates = layer.sizeAndCoordinates.copy(x = 0f, y = 0f)
        }
        else -> throw IllegalStateException()
    }
}

internal fun Node.singleNodeClearPositioning() {
    when(this) {
        is Rectangle -> this.sizeAndCoordinates = this.sizeAndCoordinates.copy(x = 0f, y = 0f)
        is Image -> this.sizeAndCoordinates = this.sizeAndCoordinates.copy(x = 0f, y = 0f)
        is Text -> this.sizeAndCoordinates = this.sizeAndCoordinates.copy(x = 0f, y = 0f)
    }
}

internal fun TreeNode.clearSizeAndPositioning() {
    when (val layer = this.value as Layer) {
        is Rectangle -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
        }
        is Image -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
        }
        is WebView -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
        }
        is ZStack -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
            this.children.forEach { it.clearSizeAndPositioning() }
        }
        is VStack -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
            this.children.forEach { it.clearSizeAndPositioning() }
        }
        is Screen -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            this.children.forEach { it.clearSizeAndPositioning() }
        }
        is ScrollContainer -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
            this.children.forEach { it.clearSizeAndPositioning() }
        }
        is Divider -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
        }
        is PageControl -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
        }
        is Carousel -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
            this.children.forEach { it.clearSizeAndPositioning() }
        }
        is Text -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
        }
        is HStack -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
            this.children.forEach { it.clearSizeAndPositioning() }
        }
        is Spacer -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
        }
        is Audio -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
        }
        is Video -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
            layer.background?.node?.singleNodeClearSizeAndPositioning()
            layer.overlay?.node?.singleNodeClearSizeAndPositioning()
        }
        is Icon -> {
            layer.sizeAndCoordinates = SizeAndCoordinates()
        }
        else -> throw IllegalStateException()
    }
}

internal fun Node.singleNodeClearSizeAndPositioning() {
    when(this) {
        is Rectangle -> this.sizeAndCoordinates = SizeAndCoordinates()
        is Image -> this.sizeAndCoordinates = SizeAndCoordinates()
        is Text -> this.sizeAndCoordinates = SizeAndCoordinates()
    }
}

/**
 * Computes the absolute and final position of the view using absolute positioning of parent,
 * relative positioning set in [TreeNode.computeSize] and offset.
 */
internal fun Layer.computePosition(context: Context, treeNode: TreeNode, point: FloatPoint) {
    when (this) {
        is Rectangle -> computePosition(context, point)
        is Image -> computePosition(context, point)
        is WebView -> computePosition(context, point)
        is ZStack -> computePosition(context, treeNode, point)
        is VStack -> computePosition(context, treeNode, point)
        is HStack -> computePosition(context, treeNode, point)
        is Screen -> computePosition(context, treeNode)
        is ScrollContainer -> computePosition(context, treeNode, point)
        is Divider -> computePosition(context, point)
        is PageControl -> computePosition(context, point)
        is Carousel -> computePosition(context, treeNode, point)
        is Text -> computePosition(context, point)
        is Audio -> computePosition(context, point)
        is Video -> computePosition(context, point)
        is Spacer -> computePosition()
        is Icon -> computePosition(context, point)
    }
}

/***
 * To retrieve the layout of nodes not in the node tree to be used as backgrounds, overlays and masks.
 */
internal fun Node.toSingleLayerLayout(
    context: Context,
    treeNode: TreeNode,
    resolvers: Resolvers,
    maskPath: MaskPath?
): View {
    return when (val layer = this as Layer) {
         is Rectangle -> {
             layer.setMaskPath(maskPath)
             layer.construct(context, treeNode, resolvers).first()
         }
         is Text -> {
             layer.setMaskPath(maskPath)
             layer.construct(context, treeNode, resolvers).first()
         }
         is Image -> {
             layer.setMaskPath(maskPath)
             layer.construct(context, treeNode, resolvers).first()
         }
        else -> throw IllegalStateException()
    }.apply {
        isClickable = false
    }
}

@JsonClass(generateAdapter = true)
data class SizeAndCoordinates(
    val width: Float = 0f,
    val height: Float = 0f,
    val x: Float = 0f,
    val y: Float = 0f,
    val contentWidth: Float = 0f,
    val contentHeight: Float = 0f
) {
    fun intersects(x: Float, y: Float, width: Float, height: Float): Boolean {
        val xIntersect = (this.x <= x + width && this.x + this.width >= x)
        val yIntersect = (this.y <= y + height && this.y + this.height >= y)

        return xIntersect && yIntersect
    }
}


data class Dimensions(val width: Dimension, val height: Dimension)
sealed class Dimension {
    object Inf : Dimension()
    data class Value(val value: Float) : Dimension()
}

class PXFramer(val frame: Frame?) {
    fun getPixelFrame(context: Context): Frame? {
        return if (pxFrame == null) {
            pxFrame = frame?.toPxFrame(context)
            pxFrame
        } else {
            pxFrame
        }
    }
    private var pxFrame: Frame? = null
}

internal fun Layer.setFrameAlignment() {
    frame?.alignment?.let { alignment ->
        when(alignment) {
            Alignment.TOP -> {
                setX(getX() + ((sizeAndCoordinates.width - sizeAndCoordinates.contentWidth) / 2f))
            }
            Alignment.TOP_LEADING -> { }
            Alignment.TOP_TRAILING -> {
                setX(getX() + (sizeAndCoordinates.width - sizeAndCoordinates.contentWidth))
            }
            Alignment.BOTTOM -> {
                setX(getX() + ((sizeAndCoordinates.width - sizeAndCoordinates.contentWidth) / 2f))
                setY(getY() + sizeAndCoordinates.height - sizeAndCoordinates.contentHeight)
            }
            Alignment.BOTTOM_LEADING -> {
                setY(getY() + sizeAndCoordinates.height - sizeAndCoordinates.contentHeight)
            }
            Alignment.BOTTOM_TRAILING -> {
                setX(getX() + sizeAndCoordinates.width - sizeAndCoordinates.contentWidth)
                setY(getY() + sizeAndCoordinates.height - sizeAndCoordinates.contentHeight)
            }
            Alignment.LEADING -> {
                setY(getY() + ((sizeAndCoordinates.height - sizeAndCoordinates.contentHeight) / 2f))
            }
            Alignment.TRAILING -> {
                setX(getX() + sizeAndCoordinates.width - sizeAndCoordinates.contentWidth)
                setY(getY() + ((sizeAndCoordinates.height - sizeAndCoordinates.contentHeight) / 2f))
            }
            Alignment.CENTER -> {
                setX(getX() + ((sizeAndCoordinates.width - sizeAndCoordinates.contentWidth) / 2f))
                setY(getY() + ((sizeAndCoordinates.height - sizeAndCoordinates.contentHeight) / 2f))
            }
        }
    }
}

internal fun Node.computeSingleNodeSize(
    context: Context,
    treeNode: TreeNode,
    width: Float,
    height: Float
) {
    when (val layer = this as Layer) {
        is Rectangle -> layer.computeSize(context, treeNode, Dimensions(Dimension.Value(width), Dimension.Value(height)))
        is Text -> layer.computeSize(context, treeNode, Dimensions(Dimension.Value(width), Dimension.Value(height)))
        is Image -> layer.computeSize(context, treeNode, Dimensions(Dimension.Value(width), Dimension.Value(height)))
    }
}

internal fun Node.computeSingleNodeRelativePosition(
    width: Float,
    height: Float,
    alignment: Alignment
) {
    when (val layer = this as Layer) {
        is Rectangle -> layer.alignIn(width, height, alignment)
        is Text -> layer.alignIn(width, height, alignment)
        is Image -> layer.alignIn(width, height, alignment)
    }
}

internal fun Node.computeSingleNodeCoordinates(context: Context, anchorPoint: FloatPoint) {
    when (val layer = this as Layer) {
        is Rectangle -> {
            setFrameAlignment()
            val xOffset = layer.offset?.x?.dp?.toPx(context) ?: 0f
            val yOffset = layer.offset?.y?.dp?.toPx(context) ?: 0f

            layer.setX(anchorPoint.x + layer.getX() + xOffset)
            layer.setY(anchorPoint.y + layer.getY() + yOffset)
            adjustPositionForPadding(context, layer.padding)
        }
        is Text -> {
            setFrameAlignment()
            val xOffset = layer.offset?.x?.dp?.toPx(context) ?: 0f
            val yOffset = layer.offset?.y?.dp?.toPx(context) ?: 0f

            layer.setX(anchorPoint.x + layer.getX() + xOffset)
            layer.setY(anchorPoint.y + layer.getY() + yOffset)
            adjustPositionForPadding(context, layer.padding)
        }
        is Image -> {
            setFrameAlignment()
            val xOffset = layer.offset?.x?.dp?.toPx(context) ?: 0f
            val yOffset = layer.offset?.y?.dp?.toPx(context) ?: 0f

            layer.setX(anchorPoint.x + layer.getX() + xOffset)
            layer.setY(anchorPoint.y + layer.getY() + yOffset)
            adjustPositionForPadding(context, layer.padding)
        }
    }
}

enum class ViewBehavior {
    // always fills available space
    EXPAND_FILL,
    // always wraps to size of content
    WRAP,
}

internal fun Layer.setX(x: Float) {
    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(x = x)
}

internal fun Layer.setY(y: Float) {
    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(y = y)
}

internal fun Layer.setHeight(height: Float) {
    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(height = height)
}

internal fun Layer.setWidth(width: Float) {
    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(width = width)
}

internal fun Layer.getY(): Float {
    return sizeAndCoordinates.y
}

internal fun Layer.getX(): Float {
    return sizeAndCoordinates.x
}

internal fun TreeNode.setX(x: Float) {
    (this.value as Layer).sizeAndCoordinates = (this.value as Layer).sizeAndCoordinates.copy(x = x)
}

internal fun TreeNode.setY(y: Float) {
    (this.value as Layer).sizeAndCoordinates = (this.value as Layer).sizeAndCoordinates.copy(y = y)
}

internal fun TreeNode.getY(): Float {
    return (this.value as Layer).sizeAndCoordinates.y
}

internal fun TreeNode.getX(): Float {
    return (this.value as Layer).sizeAndCoordinates.x
}

internal fun TreeNode.getHeight(): Float {
    return (this.value as Layer).sizeAndCoordinates.height
}
internal fun TreeNode.setHeight(height: Float) {
    (this.value as Layer).sizeAndCoordinates = (this.value as Layer).sizeAndCoordinates.copy(height = height)
}

internal fun TreeNode.addWidth(width: Float) {
    val currentWidth = (this.value as Layer).sizeAndCoordinates.width
    (this.value as Layer).sizeAndCoordinates = (this.value as Layer).sizeAndCoordinates.copy(width = width + currentWidth)
}

internal fun TreeNode.addHeight(height: Float) {
    val currentHeight = (this.value as Layer).sizeAndCoordinates.height
    (this.value as Layer).sizeAndCoordinates = (this.value as Layer).sizeAndCoordinates.copy(height = height + currentHeight)
}

internal fun TreeNode.removeHeight(height: Float) {
    val currentHeight = (this.value as Layer).sizeAndCoordinates.height
    (this.value as Layer).sizeAndCoordinates = (this.value as Layer).sizeAndCoordinates.copy(height = currentHeight - height)
}

internal fun TreeNode.setWidth(width: Float) {
    (this.value as Layer).sizeAndCoordinates = (this.value as Layer).sizeAndCoordinates.copy(width = width)
}
internal fun TreeNode.getWidth(): Float {
    return (this.value as Layer).sizeAndCoordinates.width
}

internal fun TreeNode.horizontalBehavior(): ViewBehavior {
    return when (val layer = this.value) {
        is Rectangle -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Icon -> {
            when (layer.frame?.maxWidth) {
                null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Image -> {
            when {
                layer.resizingMode == ResizingMode.ORIGINAL && layer.frame?.maxWidth == null -> ViewBehavior.WRAP
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is ScrollContainer -> {
            if (layer.axis == Axis.HORIZONTAL) {
                when {
                    layer.frame?.width != null -> ViewBehavior.WRAP
                    else -> ViewBehavior.EXPAND_FILL
                }
            } else {
                when {
                    layer.frame?.width != null -> ViewBehavior.WRAP
                    this.children.any { it.horizontalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                    else -> ViewBehavior.WRAP
                }
            }
        }
        is Video -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Audio -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Divider -> {
            val isVertical = isNearestParentStackHorizontal()
            if (isVertical) {
                when {
                    layer.frame?.maxWidth != null -> ViewBehavior.EXPAND_FILL
                    else -> ViewBehavior.WRAP
                }
            } else {
                when {
                    layer.frame?.width != null -> ViewBehavior.WRAP
                    else -> ViewBehavior.EXPAND_FILL
                }
            }
        }
        is PageControl -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Text -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is WebView -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Carousel -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is ZStack -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                this.children.any { it.horizontalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is VStack -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                this.children.any { it.horizontalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is HStack -> {
            when {
                layer.frame?.width != null -> ViewBehavior.WRAP
                this.children.any { it.horizontalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is Spacer -> when {
            this.parent?.value is HStack && (layer.frame?.width != null) -> ViewBehavior.WRAP
            this.parent?.value is HStack -> ViewBehavior.EXPAND_FILL
            else -> ViewBehavior.WRAP
        }
        else -> ViewBehavior.EXPAND_FILL
    }
}

internal fun TreeNode.verticalBehavior(): ViewBehavior {
    return when (val layer = this.value) {
        is Rectangle -> {
            when {
                layer.frame?.height != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Icon -> {
            when (layer.frame?.maxHeight) {
                null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Image -> {
            when {
                layer.resizingMode == ResizingMode.ORIGINAL && layer.frame?.maxHeight == null -> ViewBehavior.WRAP
                layer.frame?.height != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is ScrollContainer -> {
            if (layer.axis == Axis.VERTICAL) {
                when {
                    layer.frame?.height != null -> ViewBehavior.WRAP
                    else -> ViewBehavior.EXPAND_FILL
                }
            } else {
                when {
                    layer.frame?.height != null -> ViewBehavior.WRAP
                    this.children.any { it.verticalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                    else -> ViewBehavior.WRAP
                }
            }
        }
        is Video -> {
            when {
                layer.frame?.height != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Audio -> {
            when {
                layer.frame?.maxHeight != null -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is Divider -> {
            val isHorizontal = !isNearestParentStackHorizontal()
            if (isHorizontal) {
                when {
                    layer.frame?.maxHeight != null -> ViewBehavior.EXPAND_FILL
                    else -> ViewBehavior.WRAP
                }
            } else {
                when {
                    layer.frame?.height != null -> ViewBehavior.WRAP
                    else -> ViewBehavior.EXPAND_FILL
                }
            }
        }
        is PageControl ->  {
            when {
                layer.frame?.maxHeight != null -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is Text -> when {
            layer.frame?.maxHeight != null -> ViewBehavior.EXPAND_FILL
            else -> ViewBehavior.WRAP
        }
        is WebView -> {
            when {
                layer.frame?.height != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is Carousel -> {
            when {
                layer.frame?.height != null -> ViewBehavior.WRAP
                else -> ViewBehavior.EXPAND_FILL
            }
        }
        is ZStack -> {
            when {
                layer.frame?.height != null -> ViewBehavior.WRAP
                this.children.any { it.verticalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is VStack -> {
            when {
                layer.frame?.height != null -> ViewBehavior.WRAP
                this.children.any { it.verticalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is HStack -> {
            when {
                layer.frame?.height != null -> ViewBehavior.WRAP
                this.children.any { it.verticalBehavior() == ViewBehavior.EXPAND_FILL } -> ViewBehavior.EXPAND_FILL
                else -> ViewBehavior.WRAP
            }
        }
        is Spacer -> when {
            this.parent?.value is VStack && (layer.frame?.height != null) -> ViewBehavior.WRAP
            this.parent?.value is VStack -> ViewBehavior.EXPAND_FILL
            else -> ViewBehavior.WRAP
        }
        else -> ViewBehavior.EXPAND_FILL
    }
}

internal fun TreeNode.getFixedWidth(context: Context): Float {
    return when (val layer = this.value) {
        is Rectangle -> {
            layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        }
        is Video -> layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        is Audio -> layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        is Icon -> {
            when {
                layer.frame?.width != null -> layer.frame.width
                layer.frame?.minWidth != null -> layer.frame.minWidth
                else -> layer.pointSize.toFloat()
            }
        }
        is Image -> {
            when {
                layer.frame?.width != null -> layer.frame.width
                layer.frame?.minWidth != null -> layer.frame.minWidth
                layer.resizingMode == ResizingMode.ORIGINAL -> {
                    if (context.isDarkMode(appearance)) {
                        (layer.darkModeImageWidth?.toFloat() ?: layer.imageWidth?.toFloat() ?: 0f) / layer.resolution
                    } else {
                        (layer.imageWidth?.toFloat() ?: 0f) / layer.resolution
                    }
                }
                else -> 0f
            }
        }
        is ScrollContainer -> {
            if (layer.axis == Axis.VERTICAL) {
                when {
                    layer.frame?.width != null -> layer.frame.width
                    layer.frame?.minWidth != null -> layer.frame.minWidth
                    else -> this.children.maxOfOrNull { it.getFixedWidth(context) } ?: 0f
                }
            } else {
                when {
                    layer.frame?.width != null -> layer.frame.width
                    layer.frame?.minWidth != null -> layer.frame.minWidth
                    else -> 0f
                }
            }
        }
        is Divider -> {
            layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        }
        is PageControl -> layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        is Text -> layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        is WebView -> layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        is Carousel -> layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        is ZStack -> {
            when {
                layer.frame?.width != null -> layer.frame.width
                layer.frame?.minWidth != null -> layer.frame.minWidth
                else -> this.children.maxOfOrNull { it.getFixedWidth(context) } ?: 0f
            }
        }
        is VStack -> {
            when {
                layer.frame?.width != null -> layer.frame.width
                layer.frame?.minWidth != null -> layer.frame.minWidth
                else -> this.children.maxOfOrNull { it.getFixedWidth(context) } ?: 0f
            }
        }
        is HStack -> {
            when {
                layer.frame?.width != null -> layer.frame.width
                layer.frame?.minWidth != null -> layer.frame.minWidth
                else -> this.children.sumOf { it.getFixedWidth(context).toDouble() }.toFloat()
            }
        }
        is Spacer -> layer.frame?.width ?: layer.frame?.minWidth ?: 0f
        else -> 0f
    }
}

private fun TreeNode.getFixedHeight(context: Context): Float {
    return when (val layer = this.value) {
        is Rectangle -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        is Video -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        is Audio -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        is Icon -> {
            when {
                layer.frame?.height != null -> layer.frame.height
                layer.frame?.minHeight != null -> layer.frame.minHeight
                else -> layer.pointSize.toFloat()
            }
        }
        is Image -> {
            when {
                layer.frame?.height != null -> layer.frame.height
                layer.frame?.minHeight != null -> layer.frame.minHeight
                layer.resizingMode == ResizingMode.ORIGINAL -> {
                    if (context.isDarkMode(appearance)) {
                        ( (layer.darkModeImageHeight?.toFloat() ?: layer.imageHeight?.toFloat() ?: 0f) / layer.resolution)
                    } else {
                        ((layer.imageHeight?.toFloat() ?: 0f) / layer.resolution)
                    }
                }
                else -> 0f
            }
        }
        is ScrollContainer -> {
            if (layer.axis == Axis.HORIZONTAL) {
                when {
                    layer.frame?.height != null -> layer.frame.height
                    layer.frame?.minHeight != null -> layer.frame.minHeight
                    else -> this.children.maxOfOrNull { it.getFixedHeight(context) } ?: 0f
                }
            } else {
                when {
                    layer.frame?.height != null -> layer.frame.height
                    layer.frame?.minHeight != null -> layer.frame.minHeight
                    else -> 0f
                }
            }
        }
        is Divider -> {
            layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        }
        is PageControl -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        is Text -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        is WebView -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        is Carousel -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        is ZStack -> {
            when {
                layer.frame?.height != null -> layer.frame.height
                layer.frame?.minHeight != null -> layer.frame.minHeight
                else -> this.children.maxOfOrNull { it.getFixedHeight(context) } ?: 0f
            }
        }
        is VStack -> {
            when {
                layer.frame?.height != null -> layer.frame.height
                layer.frame?.minHeight != null -> layer.frame.minHeight
                else -> this.children.sumOf { it.getFixedHeight(context).toDouble() }.toFloat()

            }
        }
        is HStack -> {
            when {
                layer.frame?.height != null -> layer.frame.height
                layer.frame?.minHeight != null -> layer.frame.minHeight
                else -> this.children.maxOfOrNull { it.getFixedHeight(context) } ?: 0f
            }
        }
        is Spacer -> layer.frame?.height ?: layer.frame?.minHeight ?: 0f
        else -> 0f
    }
}

internal fun TreeNode.containsTextThatRequiresHorizontalSpace(): Boolean {
    return when (val layer = this.value) {
        is Rectangle -> false
        is Image -> false
        is ScrollContainer -> false
        is Divider -> false
        is PageControl ->false
        is Text -> layer.frame.unboundedWidth() && layer.desiresWidth
        is WebView -> false
        is Carousel -> false
        is ZStack -> (layer.frame.unboundedWidth() && this.children.any { it.containsTextThatRequiresHorizontalSpace() })
        is VStack -> (layer.frame.unboundedWidth() && this.children.any { it.containsTextThatRequiresHorizontalSpace() })
        is HStack -> (layer.frame.unboundedWidth() && this.children.any { it.containsTextThatRequiresHorizontalSpace() })
        is Spacer -> false
        else -> false
    }
}

internal fun TreeNode.getFirstText(): TreeNode? {
    return when (this.value) {
        is Text -> this
        is ZStack -> this.children.firstOrNull { it.getFirstText() != null }?.getFirstText()
        is VStack -> this.children.firstOrNull { it.getFirstText() != null }?.getFirstText()
        is HStack -> this.children.firstOrNull { it.getFirstText() != null }?.getFirstText()
        else -> null
    }
}

internal data class TreeNode(
    var value: Node,
    var parentId: String? = null,
    var children: MutableList<TreeNode> = mutableListOf(),
    val depth: Int = 0,
    val appearance: Appearance = Appearance.AUTO
) {
    var parent: TreeNode? = null

    fun addChild(node: TreeNode) {
        children.add(node)
        node.parentId = this.value.id
        node.parent = this
    }

    fun removeChild(id: String): TreeNode? {
        val child = children.find { it.value.id == id }
        children.remove(child)
        child?.parentId = null
        child?.parent = null
        return child
    }

    fun getFixedNodeWidth(context: Context): Float {
        if (fixedWidth == null) {
            fixedWidth = getFixedWidth(context).dp.toPx(context)
        } else {
            fixedWidth
        }
        return fixedWidth!!
    }
    fun getFixedNodeHeight(context: Context): Float {
        if (fixedHeight == null) {
            fixedHeight = getFixedHeight(context).dp.toPx(context)
        } else {
            fixedHeight
        }
        return fixedHeight!!
    }
    private var fixedWidth: Float? = null
    private var fixedHeight: Float? = null
}

internal fun TreeNode.rootNodeHeight(): Float {
    var currentNode: TreeNode = this
    while (currentNode.parent != null) {
        currentNode = currentNode.parent!!
    }
    return (currentNode.value as Layer).sizeAndCoordinates.height
}

private fun TreeNode.rootNode(): TreeNode {
    var currentNode: TreeNode = this
    while (currentNode.parent != null) {
        currentNode = currentNode.parent!!
    }
    return (currentNode)
}

internal fun TreeNode.findNodeWithID(id: String): TreeNode? {
    return rootNode().findDescendantWithID(id)
}

internal inline fun <reified T> TreeNode.findNearestAncestorID(): String? {
    var parent = this.parent

    while (parent != null && parent.value !is T) {
        parent = parent.parent
    }

    return parent?.value?.id
}

internal inline fun <reified T> TreeNode.findNearestAncestor(): T? {
    var parent = this.parent

    while (parent != null && parent.value !is T) {
        parent = parent.parent
    }

    return parent?.value as? T
}

internal fun TreeNode.getAllLeafNodes(): List<Node> {
    val descendants = mutableListOf<Node>()

    if (this.children.isEmpty()) {
        descendants.add(this.value);
    } else {
        this.children.forEach { descendants.addAll(it.getAllLeafNodes()) }
    }
    return descendants;
}

private fun TreeNode.findDescendantWithID(id: String): TreeNode? {
    return when (this.value.id) {
        id -> this
        else -> this.children.firstOrNull { it.findDescendantWithID(id) != null }?.findDescendantWithID(id)
    }
}

internal fun TreeNode.rootNodeWidth(): Float {
    var currentNode: TreeNode = this
    while (currentNode.parent != null) {
        currentNode = currentNode.parent!!
    }
    return (currentNode.value as Layer).sizeAndCoordinates.width
}
