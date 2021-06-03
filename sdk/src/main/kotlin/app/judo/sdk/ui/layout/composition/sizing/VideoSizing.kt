package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.MaxHeight
import app.judo.sdk.api.models.MaxWidth
import app.judo.sdk.api.models.Video
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computeSingleNodeSize

internal fun Video.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val heightConstraint: Dimension = when(val intrinsicHeight = parentConstraints.height) {
        is Dimension.Inf -> {
            when {
                pxFrame?.minHeight != null -> Dimension.Value(pxFrame.minHeight)
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Inf
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Inf
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(intrinsicHeight.value, pxFrame.minHeight))
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                else -> Dimension.Value(intrinsicHeight.value)
            }
        }
    }

    val widthConstraint: Dimension = when(val intrinsicWidth = parentConstraints.width) {
        is Dimension.Inf -> {
            when {
                pxFrame?.minWidth != null -> Dimension.Value(pxFrame.minWidth)
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Inf
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Inf
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.minWidth != null -> Dimension.Value(maxOf(intrinsicWidth.value, pxFrame.minWidth))
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, intrinsicWidth.value))
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(intrinsicWidth.value)
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                else -> Dimension.Value(intrinsicWidth.value)
            }
        }
    }

    when {
        aspectRatio != null -> {
            var widthForCalculation = when (widthConstraint) {
                is Dimension.Inf -> 10000f
                is Dimension.Value -> widthConstraint.value
            }
            var heightForCalculation = when (heightConstraint) {
                is Dimension.Inf -> 10000f
                is Dimension.Value -> heightConstraint.value
            }

            // for when both dimensions are inf constrained
            if (widthConstraint is Dimension.Inf && heightConstraint is Dimension.Inf) {
                val horizontal = (treeNode.rootNodeWidth() / aspectRatio) <= treeNode.rootNodeHeight()
                if (horizontal) {
                    widthForCalculation = 0f
                } else {
                    heightForCalculation = 0f
                }
            }

            val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)
            val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)

            val horizontal = (widthForCalculation / aspectRatio) <= heightForCalculation

            val widthToHeight = if (horizontal) {
                widthForCalculation to (widthForCalculation / aspectRatio)
            } else {
                (heightForCalculation * aspectRatio) to heightForCalculation
            }

            val frameHeight = when {
                pxFrame?.height != null -> pxFrame.height
                pxFrame?.minHeight != null -> maxOf(pxFrame.minHeight, widthToHeight.second)
                pxFrame?.maxHeight is MaxHeight.Infinite && heightConstraint is Dimension.Value -> heightConstraint.value
                else -> null
            }

            val frameWidth = when {
                pxFrame?.width != null -> pxFrame.width
                pxFrame?.minWidth != null -> maxOf(pxFrame.minWidth, widthToHeight.first)
                pxFrame?.maxWidth is MaxWidth.Infinite && widthConstraint is Dimension.Value -> widthConstraint.value
                else -> null
            }

            this.sizeAndCoordinates = sizeAndCoordinates.copy(
                width = frameWidth ?: widthToHeight.first,
                height = frameHeight ?: widthToHeight.second,
                contentWidth = widthToHeight.first - horizontalPadding,
                contentHeight = widthToHeight.second - verticalPadding
            )
        }
        else -> {
            val width = when (widthConstraint) {
                is Dimension.Inf -> 0f
                is Dimension.Value -> widthConstraint.value
            }
            val height = when (heightConstraint) {
                is Dimension.Inf -> 0f
                is Dimension.Value -> heightConstraint.value
            }

            val contentWidth = width - (padding?.leading?.dp?.toPx(context) ?: 0f) - (padding?.trailing?.dp?.toPx(context) ?: 0f)
            val contentHeight = height - (padding?.top?.dp?.toPx(context) ?: 0f) - (padding?.bottom?.dp?.toPx(context) ?: 0f)

            this.sizeAndCoordinates = sizeAndCoordinates.copy(width = width, height = height, contentWidth = contentWidth, contentHeight = contentHeight)
        }
    }

    background?.node?.computeSingleNodeSize(context, treeNode, sizeAndCoordinates.width, sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, sizeAndCoordinates.width, sizeAndCoordinates.height)
}