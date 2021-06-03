package app.judo.sdk.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import app.judo.sdk.api.models.Shadow
import app.judo.sdk.core.data.TextSkeleton
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.extensions.toSptoPx
import app.judo.sdk.ui.layout.Resolvers

internal class ExperienceTextView @JvmOverloads constructor(
    context: Context, resolvers: Resolvers? = null, shadow: Shadow? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {
    var skeleton: TextSkeleton? = null
        set(value) {
            if (value != null) {
                skeletonPaint = Paint().apply { color = value.color }
                field = value
            } else {
                skeletonPaint = null
                field = value
            }
        }
    var skeletonPaint: Paint? = null

    init {
        resolvers?.let {
            shadow?.let {
                val shadowColor = resolvers.colorResolver.resolveForColorInt(it.color)
                val pxBlur = it.blur.dp.toPx(context)
                val blurRadius = if (pxBlur <= 0f) 0.01f else pxBlur
                setShadowLayer(blurRadius, it.x.dp.toPx(context), it.y.dp.toPx(context), shadowColor)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let { _ ->
            skeleton?.let {
                canvas.drawRoundRect(
                    0f,
                    0f,
                    width.toFloat(),
                    it.height.dp.toSptoPx(context),
                    it.cornerRadius.dp.toPx(context),
                    it.cornerRadius.dp.toPx(context),
                    skeletonPaint!!
                )
            }
        }
    }
}