package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.HorizontalAlignment
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.VStack
import app.judo.sdk.ui.extensions.adjustPositionForPadding
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun VStack.computePosition(context: Context, treeNode: TreeNode, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

    val maxChildWidth = treeNode.children.maxOfOrNull { it.getWidth() } ?: 0f
    var totalHeight = 0f
    val spacing = spacing.dp.toPx(context)
    treeNode.children.forEachIndexed { index, child ->
        child.setY(totalHeight + child.getY())

        // set alignment
        when (alignment) {
            // adding current child x and y in case child has a frame
            HorizontalAlignment.LEADING -> child.setX(0f + child.getX())
            HorizontalAlignment.TRAILING -> {
                val x = maxChildWidth - child.getWidth()
                child.setX(x + child.getX())
            }
            HorizontalAlignment.CENTER -> {
                val x = (maxChildWidth - child.getWidth()) / 2f
                child.setX(x + child.getX())
            }
        }
        totalHeight += child.getHeight()
        if (index != treeNode.children.lastIndex) totalHeight += spacing
    }

    background?.node?.computeSingleNodeRelativePosition(this.sizeAndCoordinates.width, this.sizeAndCoordinates.height, background.alignment)
    overlay?.node?.computeSingleNodeRelativePosition(this.sizeAndCoordinates.width, this.sizeAndCoordinates.height, overlay.alignment)
    background?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))
    overlay?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))

    // set frame alignment
    setFrameAlignment()

    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    adjustPositionForPadding(context, padding)

    treeNode.children.forEach {
        (it.value as Layer).computePosition(context, it, FloatPoint(getX(), getY()))
    }
}