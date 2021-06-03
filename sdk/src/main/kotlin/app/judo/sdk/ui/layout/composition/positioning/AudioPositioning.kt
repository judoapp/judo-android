package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.Audio
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.ui.extensions.adjustPositionForPadding
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*
import app.judo.sdk.ui.layout.composition.getX
import app.judo.sdk.ui.layout.composition.setFrameAlignment
import app.judo.sdk.ui.layout.composition.setX
import app.judo.sdk.ui.layout.composition.setY

internal fun Audio.computePosition(context: Context, point: FloatPoint) {
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

    // relative positioning to parent + absolute positioning of parent + offset
    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    adjustPositionForPadding(context, padding)
}
