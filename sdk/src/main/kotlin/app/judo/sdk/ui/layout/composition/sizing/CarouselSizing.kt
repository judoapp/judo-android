package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.Carousel
import app.judo.sdk.api.models.MaxHeight
import app.judo.sdk.api.models.MaxWidth
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun Carousel.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
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
                    widthForCalculation = treeNode.rootNodeWidth()
                } else {
                    heightForCalculation = treeNode.rootNodeWidth()
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

            // ask children to compute sizes given bounds
            treeNode.children.forEach {
                it.computeSize(context,
                    Dimensions(
                        Dimension.Value(widthToHeight.first - horizontalPadding),
                        Dimension.Value(widthToHeight.second - verticalPadding)
                    )
                )
            }

            this.sizeAndCoordinates = sizeAndCoordinates.copy(
                width = widthToHeight.first,
                height = widthToHeight.second,
                contentWidth = widthToHeight.first - horizontalPadding,
                contentHeight = widthToHeight.second - verticalPadding
            )

        }
        else -> {
            val widthForChildMeasure = when(widthConstraint) {
                is Dimension.Inf -> Dimension.Inf
                is Dimension.Value -> Dimension.Value(widthConstraint.value - (padding?.leading?.dp?.toPx(context) ?: 0f) - (padding?.trailing?.dp?.toPx(context) ?: 0f))
            }

            val heightForChildMeasure = when(heightConstraint) {
                is Dimension.Inf -> Dimension.Inf
                is Dimension.Value -> Dimension.Value(heightConstraint.value - (padding?.top?.dp?.toPx(context) ?: 0f) - (padding?.bottom?.dp?.toPx(context) ?: 0f))
            }

            val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)
            val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)

            // ask children to compute sizes given bounds
            treeNode.children.forEach {
                it.computeSize(context, Dimensions(widthForChildMeasure, heightForChildMeasure))
            }

            val actualWidth = when(widthConstraint) {
                is Dimension.Inf -> 0f
                is Dimension.Value -> widthConstraint.value
            }
            val actualHeight = when(heightConstraint) {
                is Dimension.Inf -> 0f
                is Dimension.Value -> heightConstraint.value
            }

            this.sizeAndCoordinates = sizeAndCoordinates.copy(
                width = actualWidth,
                height = actualHeight,
                contentWidth = actualWidth - horizontalPadding,
                contentHeight = actualHeight - verticalPadding
            )
        }
    }

    background?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
}