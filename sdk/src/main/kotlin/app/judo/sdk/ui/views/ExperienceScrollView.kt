package app.judo.sdk.ui.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import app.judo.sdk.api.models.Shadow
import app.judo.sdk.ui.extensions.draw
import app.judo.sdk.ui.extensions.toDrawableShadow
import app.judo.sdk.ui.layout.Resolvers

internal class ExperienceScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val resolvers: Resolvers? = null
) : NestedScrollView(context, attrs, defStyleAttr) {

    var shadow: Shadow? = null
        set(value) {
            field = value
            value?.let { shadow ->
                resolvers?.let { resolvers ->
                    drawableShadow = shadow.toDrawableShadow(context, resolvers)
                }
            }
        }

    private var drawableShadow: DrawableShadow? = null

    override fun onDraw(canvas: Canvas?) {
        drawableShadow?.draw(this, canvas)

        super.onDraw(canvas)
    }
}