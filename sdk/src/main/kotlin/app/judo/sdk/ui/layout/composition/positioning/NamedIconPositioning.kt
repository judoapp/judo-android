package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.NamedIcon
import app.judo.sdk.ui.extensions.adjustPositionForPadding
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun NamedIcon.computePosition(context: Context, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

    // set frame alignment
    setFrameAlignment()

    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    adjustPositionForPadding(context, padding)
}