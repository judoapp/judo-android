package app.judo.sdk.ui.extensions

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.View
import app.judo.sdk.api.models.*
import app.judo.sdk.core.data.resolvers.ColorResolver
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.views.DrawableBorder
import app.judo.sdk.ui.views.DrawableFill
import app.judo.sdk.ui.views.DrawableShadow
import kotlin.math.roundToInt

internal fun DrawableBorder.draw(view: View, canvas: Canvas?) {
    val halfBorderWidthPx = width / 2
    val compensatedBorderRadius = if (radius - halfBorderWidthPx > 0) radius - halfBorderWidthPx else 0f

    canvas?.drawRoundRect(
        halfBorderWidthPx, halfBorderWidthPx, view.width - halfBorderWidthPx, view.height - halfBorderWidthPx,
        compensatedBorderRadius,
        compensatedBorderRadius,
        paint
    )
}

internal fun Paint.setColor(colorResolver: ColorResolver, colorVariants: ColorVariants) {
    val color = colorResolver.resolveForColorInt(colorVariants)
    setColor(color)
}

internal fun Border.toDrawableBorder(context: Context, resolvers: Resolvers, radius: Float): DrawableBorder {
    val borderPaint = Paint().apply {
        isAntiAlias = true
        setColor(resolvers.colorResolver, this@toDrawableBorder.color)
        style = Paint.Style.STROKE
        strokeWidth = width.dp.toPx(context)
    }

    return DrawableBorder(
        borderPaint,
        radius.dp.toPx(context),
        width = width.dp.toPx(context)
    )
}

internal fun DrawableShadow.draw(view: View, canvas: Canvas?) {
    canvas?.drawRoundRect(
        0f + offset.x,
        0f + offset.y,
        view.width.toFloat() + offset.x,
        view.height.toFloat() + offset.y,
        radius,
        radius,
        paint
    )
}

internal fun Shadow.toDrawableShadow(context: Context, resolvers: Resolvers, radius: Float = 0f): DrawableShadow {
    val shadowPaint = Paint().apply {
        setColor(resolvers.colorResolver, this@toDrawableShadow.color)
        maskFilter = BlurMaskFilter(blur.dp.toPx(context), BlurMaskFilter.Blur.NORMAL)
    }

    val shadowOffset = FloatPoint(x.dp.toPx(context), y.dp.toPx(context))

    return DrawableShadow(shadowPaint, radius.dp.toPx(context), shadowOffset)
}

internal fun DrawableFill.draw(view: View, canvas: Canvas?) {
    canvas?.drawRoundRect(
        0f,
        0f,
        view.width.toFloat(),
        view.height.toFloat(),
        radius,
        radius,
        paint
    )
}

internal fun Fill.toDrawableFill(view: View, context: Context, resolvers: Resolvers, radius: Float): DrawableFill {

    return when (this) {
        is Fill.FlatFill -> {
            val paint = Paint().apply {
                setColor(resolvers.colorResolver, this@toDrawableFill.color)
            }

            DrawableFill(paint, radius.dp.toPx(context))
        }
        is Fill.GradientFill -> {
            val resolvedGradient = resolvers.gradientResolver.resolveGradient(gradient)
            val startX = resolvedGradient.from[0]
            val startY = resolvedGradient.from[1]
            val endX = resolvedGradient.to[0]
            val endY = resolvedGradient.to[1]
            val colors = resolvedGradient.stops.map {
                Color.argb(
                    (it.color.alpha * 255).roundToInt(),
                    (it.color.red * 255).roundToInt(),
                    (it.color.green * 255).roundToInt(),
                    (it.color.blue * 255).roundToInt()
                )
            }.toIntArray()
            val positions = resolvedGradient.stops.map { it.position.toFloat() }.toFloatArray()
            val paint = Paint().apply {
                shader = LinearGradient(
                    startX * view.width,
                    startY * view.height,
                    endX * view.width,
                    endY * view.height,
                    colors,
                    positions,
                    Shader.TileMode.CLAMP
                )
            }

            DrawableFill(paint, radius.dp.toPx(context))
        }
    }
}

internal fun createRipple(context: Context, color: Int, cornerRadius: Float = 0f): RippleDrawable {
    val rippleColor = Color.argb(63, Color.red(color), Color.green(color), Color.blue(color))
    val pxRadius = cornerRadius.dp.toPx(context)
    val outerCornerRadius = floatArrayOf(pxRadius, pxRadius, pxRadius, pxRadius, pxRadius, pxRadius, pxRadius, pxRadius)
    val colorStateList = ColorStateList(arrayOf(intArrayOf(R.attr.state_enabled)), intArrayOf(rippleColor))
    val shapeDrawable = ShapeDrawable(RoundRectShape(outerCornerRadius, null, null))
    return RippleDrawable(colorStateList, null, shapeDrawable)
}