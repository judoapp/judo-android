package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.layout.composition.*

internal fun Spacer.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val parentWidthConstraint = when {
        parentConstraints.width is Dimension.Value && treeNode.parent?.value is HStack -> parentConstraints.width.value
        else -> 0f
    }

    val parentHeightConstraint = when {
        parentConstraints.height is Dimension.Value && treeNode.parent?.value is VStack -> parentConstraints.height.value
        else -> 0f
    }

    val height: Float = when {
        pxFrame?.maxHeight is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, parentHeightConstraint)
        pxFrame?.maxHeight is MaxHeight.Infinite -> parentHeightConstraint
        pxFrame?.height != null -> pxFrame.height
        pxFrame?.minHeight != null -> maxOf(parentHeightConstraint, pxFrame.minHeight)
        else -> parentHeightConstraint
    }
    val width: Float = when {
        pxFrame?.maxWidth is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, parentWidthConstraint)
        pxFrame?.maxWidth is MaxWidth.Infinite -> parentWidthConstraint
        frame?.width != null -> pxFrame?.width!!
        frame?.minWidth != null -> maxOf(parentWidthConstraint, pxFrame?.minWidth!!)
        else -> parentWidthConstraint
    }

    this.sizeAndCoordinates = sizeAndCoordinates.copy(width = width, height = height, contentWidth = width, contentHeight = height)
}

internal fun Spacer.computePosition() {
    setFrameAlignment()
}