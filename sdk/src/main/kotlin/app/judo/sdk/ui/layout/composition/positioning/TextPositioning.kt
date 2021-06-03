package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.Text
import app.judo.sdk.ui.extensions.adjustPositionForPadding
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun Text.computePosition(context: Context, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

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

    // set frame alignment
    setFrameAlignment()

    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    adjustPositionForPadding(context, padding)
}