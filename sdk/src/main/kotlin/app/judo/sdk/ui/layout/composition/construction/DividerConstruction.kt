package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import app.judo.sdk.api.models.Divider
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.views.DividerView
import kotlin.math.roundToInt

internal fun Divider.construct(context: Context, resolvers: Resolvers): List<View> {
    val divider = DividerView(context, backgroundColor, resolvers).apply {
        id = View.generateViewId()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        setWillNotDraw(false)
        isClickable = false
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.roundToInt(),
            sizeAndCoordinates.contentHeight.roundToInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
        }
    }

    return listOf(divider)
}