package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computeSingleNodeSize

internal fun PageControl.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val pageControlDefaultHeight = when (style) {
        is PageControlStyle.ImagePageControlStyle -> {
            if (style.currentImage.resizingMode == ResizingMode.ORIGINAL && style.normalImage.resizingMode == ResizingMode.ORIGINAL) {
                val currentImageHeight = (style.currentImage.imageHeight ?: 0).dp.toPx(context) / style.currentImage.resolution
                val normalImageHeight = (style.normalImage.imageHeight ?: 0).dp.toPx(context) / style.normalImage.resolution
                maxOf(normalImageHeight, currentImageHeight)
            } else {
                getPageControlHeight(context)
            }
        }
        else -> getPageControlHeight(context)
    }

    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)
    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)

    val height: Dimension.Value = when(val intrinsicHeight = parentConstraints.height) {
        is Dimension.Inf -> {
            when {
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(pageControlDefaultHeight + verticalPadding, pxFrame.minHeight))
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, pageControlDefaultHeight + verticalPadding))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(pageControlDefaultHeight + verticalPadding)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                else -> Dimension.Value(pageControlDefaultHeight + verticalPadding)
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(pageControlDefaultHeight + verticalPadding, pxFrame.minHeight))
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                else -> Dimension.Value(pageControlDefaultHeight + verticalPadding)
            }
        }
    }

    val indicatorCount = treeNode.findNodeWithID(carouselID)?.children?.count() ?: 1
    val spaceBetweenIndicators = 9.dp.toPx(context)
    val indicatorDiameter = 7.dp.toPx(context)
    val defaultPageControlWidth = (indicatorCount * indicatorDiameter) + (spaceBetweenIndicators * (indicatorCount - 1))

    val width: Dimension.Value = when(val intrinsicWidth = parentConstraints.width) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, defaultPageControlWidth + horizontalPadding))
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(defaultPageControlWidth + horizontalPadding)
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                pxFrame?.minWidth != null -> Dimension.Value(maxOf(defaultPageControlWidth + horizontalPadding, pxFrame.minWidth))
                else -> Dimension.Value(defaultPageControlWidth + horizontalPadding)
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

    this.sizeAndCoordinates = sizeAndCoordinates.copy(
        width = width.value,
        height = height.value,
        contentHeight = pageControlDefaultHeight,
        contentWidth = width.value - horizontalPadding
    )

    background?.node?.computeSingleNodeSize(context, treeNode, width.value, height.value)
    overlay?.node?.computeSingleNodeSize(context, treeNode, width.value, height.value)
}
