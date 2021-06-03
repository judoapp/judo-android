package app.judo.sdk.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.ui.extensions.setColor
import app.judo.sdk.ui.layout.Resolvers

internal class DividerView @JvmOverloads constructor(
    context: Context,
    private val backgroundColor: ColorVariants? = null,
    private val resolvers: Resolvers? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        resolvers?.let { resolvers ->
            backgroundColor?.let {
                setColor(resolvers.colorResolver, it)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        resolvers?.let {
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }
}