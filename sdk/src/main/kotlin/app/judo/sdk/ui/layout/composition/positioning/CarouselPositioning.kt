package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.Carousel
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.Layer
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun Carousel.computePosition(context: Context, treeNode: TreeNode, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

    val contentWidth = sizeAndCoordinates.contentWidth
    val contentHeight = sizeAndCoordinates.contentHeight

    treeNode.children.forEach {
        it.setY(((contentHeight - (it.getHeight())) / 2f) + it.getY())
        it.setX(((contentWidth - it.getWidth()) / 2f) + it.getX())
    }

    background?.node?.computeSingleNodeRelativePosition(
        this.sizeAndCoordinates.width,
        this.sizeAndCoordinates.height,
        background.alignment
    )
    overlay?.node?.computeSingleNodeRelativePosition(
        this.sizeAndCoordinates.width,
        this.sizeAndCoordinates.height,
        overlay.alignment
    )

    background?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))
    overlay?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))

    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    // set child positions to 0,0 because children are added to carousel container frame, not screen frame so should
    // be positioned relative to carousel frame
    treeNode.children.forEach {
        (it.value as Layer).computePosition(context, it, FloatPoint(0f, 0f))
    }
}
