package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun ScrollContainer.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate scroll container size
    val pxFrame = pxFramer.getPixelFrame(context)

    val heightConstraint: Dimension = when(val intrinsicHeight = parentConstraints.height) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(pxFrame.maxHeight.value)
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Inf
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                pxFrame?.minHeight != null -> Dimension.Value(pxFrame.minHeight)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(intrinsicHeight.value, pxFrame.minHeight))
                else -> Dimension.Value(intrinsicHeight.value)
            }
        }
    }

    val widthConstraint: Dimension = when(val intrinsicWidth = parentConstraints.width) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(pxFrame.maxWidth.value)
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Inf
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                pxFrame?.minWidth != null -> Dimension.Value(pxFrame.minWidth)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, intrinsicWidth.value))
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(intrinsicWidth.value)
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                pxFrame?.minWidth != null -> Dimension.Value(maxOf(intrinsicWidth.value, pxFrame.minWidth))
                else -> Dimension.Value(intrinsicWidth.value)
            }
        }
    }

    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)
    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)

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

            val horizontal = (widthForCalculation / aspectRatio) <= heightForCalculation

            val widthToHeight = if (horizontal) {
                widthForCalculation to (widthForCalculation / aspectRatio)
            } else {
                (heightForCalculation * aspectRatio) to heightForCalculation
            }

            val constraints = if (axis == Axis.VERTICAL) {
                Dimensions(Dimension.Value(widthToHeight.first - horizontalPadding), Dimension.Inf)
            } else {
                Dimensions(Dimension.Inf, Dimension.Value(widthToHeight.second - verticalPadding))
            }

            // set positions
            treeNode.children.forEach {
                it.computeSize(context, constraints)
            }

            if (axis == Axis.VERTICAL) {
                var totalHeight = verticalPadding
                treeNode.children.forEach { totalHeight += it.getHeight() }
                val width = (treeNode.children.maxOfOrNull { it.getWidth() } ?: 0f) + horizontalPadding

                this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                    width = widthToHeight.first,
                    height = widthToHeight.second,
                    contentWidth = width - horizontalPadding,
                    contentHeight = totalHeight - verticalPadding
                )

            } else {
                var totalWidth = horizontalPadding
                treeNode.children.forEach { totalWidth += it.getWidth() }
                val height = (treeNode.children.maxOfOrNull { it.getHeight() } ?: 0f) + verticalPadding

                this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                    width = widthToHeight.first,
                    height = widthToHeight.second,
                    contentWidth = totalWidth - horizontalPadding,
                    contentHeight = height - verticalPadding
                )
            }

        }
        else -> {
            val widthForChildMeasure = when (widthConstraint) {
                is Dimension.Inf -> Dimension.Inf
                is Dimension.Value -> Dimension.Value(
                    widthConstraint.value - (padding?.leading?.dp?.toPx(context) ?: 0f) - (padding?.trailing?.dp?.toPx(
                        context
                    ) ?: 0f)
                )
            }

            val heightForChildMeasure = when (heightConstraint) {
                is Dimension.Inf -> Dimension.Inf
                is Dimension.Value -> Dimension.Value(
                    heightConstraint.value - (padding?.top?.dp?.toPx(context) ?: 0f) - (padding?.bottom?.dp?.toPx(
                        context
                    ) ?: 0f)
                )
            }

            val constraints = if (axis == Axis.VERTICAL) {
                Dimensions(widthForChildMeasure, Dimension.Inf)
            } else {
                Dimensions(Dimension.Inf, heightForChildMeasure)
            }

            // set positions
            treeNode.children.forEach {
                it.computeSize(context, constraints)
            }

            if (axis == Axis.VERTICAL) {
                var totalHeight = verticalPadding
                treeNode.children.forEach { totalHeight += it.getHeight() }
                val width = (treeNode.children.maxOfOrNull { it.getWidth() } ?: 0f) + horizontalPadding

                val containerWidth = if (widthConstraint is Dimension.Value && frame?.unboundedWidth() == false) widthConstraint.value else width
                val containerHeight = if (heightConstraint is Dimension.Value) heightConstraint.value else totalHeight

                val contentHeight = totalHeight
                val contentWidth = width

                this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                    width = containerWidth,
                    height = containerHeight,
                    contentWidth = contentWidth - horizontalPadding,
                    contentHeight = contentHeight - verticalPadding
                )
            } else {
                var totalWidth = horizontalPadding
                treeNode.children.forEach { totalWidth += it.getWidth() }
                val height = (treeNode.children.maxOfOrNull { it.getHeight() } ?: 0f) + verticalPadding

                val containerWidth = if (widthConstraint is Dimension.Value) widthConstraint.value else totalWidth
                val containerHeight = if (heightConstraint is Dimension.Value && frame?.unboundedHeight() == false) heightConstraint.value else height

                val contentHeight = height
                val contentWidth = totalWidth

                this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                    width = containerWidth,
                    height = containerHeight,
                    contentWidth = contentWidth - horizontalPadding,
                    contentHeight = contentHeight - verticalPadding
                )
            }
        }
    }

    background?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
}