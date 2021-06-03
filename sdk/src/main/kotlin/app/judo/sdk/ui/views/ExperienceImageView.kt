package app.judo.sdk.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import app.judo.sdk.R
import app.judo.sdk.api.models.Border
import app.judo.sdk.api.models.MaskPath
import app.judo.sdk.api.models.ResizingMode
import app.judo.sdk.api.models.Shadow
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.layout.Resolvers
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.properties.Delegates

internal class ExperienceImageView @JvmOverloads constructor(
    context: Context,
    val border: Border? = null,
    val resolvers: Resolvers? = null,
    val shadow: Shadow? = null,
    val resizingMode: ResizingMode? = null,
    val maskPath: MaskPath? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var skeleton = false

    private val skeletonPaint = Paint().apply { color = context.resources.getColor(R.color.judo_sdk_imageSkeleton, null) }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        if (shadow != null && resolvers != null)
            setColor(resolvers.colorResolver, shadow.color)
    }

    private var scaledShadowBitmap: Bitmap? = null

    private var offsetX = 0
    private var offsetY = 0

    private var bitmap: Bitmap? by Delegates.observable(null) { _, _, newBitmap ->
        newBitmap?.let {
            shadow?.let {
                resolvers?.let {
                    scaleShadowBitmap()
                }
            }
        }
    }

    //region Lifecycle
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        scaleShadowBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scaleShadowBitmap()
    }

    //endregion Lifecycle
    override fun onDraw(canvas: Canvas?) {
        maskPath?.let {
            canvas?.clipPath(it.path)
        }

        scaledShadowBitmap?.let { bm ->
            canvas?.run {
                drawBitmap(
                    bm,
                    offsetX.toFloat(),
                    offsetY.toFloat(),
                    shadowPaint
                )
            }
        }

        super.onDraw(canvas)

        canvas?.let {
            if (skeleton) {
                it.drawRect(0f, 0f, width.toFloat(), height.toFloat(), skeletonPaint)
            }
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        bitmap = bm
        super.setImageBitmap(bm)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        bitmap = drawable?.toBitmap()

        super.setImageDrawable(drawable)
        if (drawable is Animatable) drawable.start()
    }

    private fun scaleShadowBitmap(newWidth: Int = width, newHeight: Int = height) {
        val viewWidth = newWidth - (paddingStart + paddingEnd)
        val viewHeight = newHeight - (paddingTop + paddingBottom)
        val theViewIsPhysicallyVisible = viewHeight > 0 && viewWidth > 0

        if (theViewIsPhysicallyVisible && shadow != null && resolvers != null) {
            bitmap?.let {
                    when (resizingMode) {
                        ResizingMode.STRETCH -> {
                            val scaledBitmap = it.scale(viewWidth, viewHeight)

                            val shadowMapAndOffset = scaledBitmap.mapToShadowAndOffset(shadow.blur.dp.toPx(context))

                            offsetX = shadowMapAndOffset.second.x + shadow.x.dp.toIntPx(context)
                            offsetY = shadowMapAndOffset.second.y + shadow.y.dp.toIntPx(context)

                            scaledShadowBitmap = shadowMapAndOffset.first
                        }
                        ResizingMode.SCALE_TO_FIT -> {
                            // find dimension that will fill the view bounds first
                            val horizontalImage = it.width.toFloat() / viewWidth >= it.height.toFloat() / viewHeight

                            // scale based upon the dimension that fills the view bounds first
                            val scaledBitmap = if (horizontalImage) {
                                it.scale(viewWidth, ((it.height.toFloat() / it.width.toFloat()) * viewWidth).roundToInt())
                            } else {
                                it.scale((viewHeight / (it.height.toFloat() / it.width.toFloat()).roundToInt()), viewHeight)
                            }
                            val shadowMapAndOffset = scaledBitmap.mapToShadowAndOffset(shadow.blur.dp.toPx(context))

                            val centerX = ((viewWidth - scaledBitmap.width) / 2f).roundToInt()
                            val centerY = ((viewHeight - scaledBitmap.height) / 2f).roundToInt()

                            offsetX = shadowMapAndOffset.second.x + shadow.x.dp.toIntPx(context) + centerX
                            offsetY = shadowMapAndOffset.second.y + shadow.y.dp.toIntPx(context) + centerY

                            scaledShadowBitmap = shadowMapAndOffset.first
                        }
                        ResizingMode.SCALE_TO_FILL -> {
                            // find dimension that will fill the view bounds first
                            val horizontalImage = it.width.toFloat() / viewWidth >= it.height.toFloat() / viewHeight

                            // scale based upon the dimension that fills the view bounds last
                            val scaledBitmap = if (horizontalImage) {
                                it.scale((viewHeight / (it.height.toFloat() / it.width.toFloat())).roundToInt(), viewHeight)
                            } else {
                                it.scale(viewWidth, ((it.height.toFloat() / it.width.toFloat()) * viewWidth).roundToInt())
                            }
                            val shadowMapAndOffset = scaledBitmap.mapToShadowAndOffset(shadow.blur.dp.toPx(context))

                            val centerX = ((viewWidth - scaledBitmap.width) / 2f).roundToInt()
                            val centerY = ((viewHeight - scaledBitmap.height) / 2f).roundToInt()

                            offsetX = shadowMapAndOffset.second.x + shadow.x.dp.toIntPx(context) + centerX
                            offsetY = shadowMapAndOffset.second.y + shadow.y.dp.toIntPx(context) + centerY

                            scaledShadowBitmap = shadowMapAndOffset.first
                        }
                        ResizingMode.TILE -> scaledShadowBitmap = buildTileShadow(it, shadow)
                        ResizingMode.ORIGINAL -> {
                            val shadowMapAndOffset = it.mapToShadowAndOffset(shadow.blur.dp.toPx(context))

                            val centerX = ((viewWidth - it.width) / 2f).roundToInt()
                            val centerY = ((viewHeight - it.height) / 2f).roundToInt()

                            offsetX = shadowMapAndOffset.second.x + shadow.x.dp.toIntPx(context) + centerX
                            offsetY = shadowMapAndOffset.second.y + shadow.y.dp.toIntPx(context) + centerY

                            scaledShadowBitmap = shadowMapAndOffset.first
                        }
                    }
                }
        }
    }

    private fun buildTileShadow(bitmap: Bitmap, shadow: Shadow): Bitmap {
        val shadowMapAndOffset = bitmap.mapToShadowAndOffset(shadow.blur.dp.toPx(context))
        offsetX = shadowMapAndOffset.second.x + shadow.x.dp.toIntPx(context)
        offsetY = shadowMapAndOffset.second.y + shadow.y.dp.toIntPx(context)

        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        val blur = shadow.blur.dp.toPx(context)

        // calculate number of shadows in both dimensions so we know how many to draw in each dimension
        val numberOfHorizontalShadows = ceil(width.toFloat() / bitmapWidth.toFloat()).toInt()
        val numberOfVerticalShadows = ceil(height.toFloat() / bitmapHeight.toFloat()).toInt()

        // calculate the edge shadow remainder so we know how much of the edge shadows to draw
        val horizontalRemainder = (width.toFloat() % bitmapWidth.toFloat())
        val verticalRemainder = (height.toFloat() % bitmapHeight.toFloat())

        // build the remainder bitmaps from the source bitmap so we can later draw to canvas
        val horizontalRemainderBitmap = Bitmap.createBitmap(bitmap, 0, 0, horizontalRemainder.roundToInt(), bitmapHeight)
            .mapToShadowAndOffset(blur)

        val verticalRemainderBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, verticalRemainder.roundToInt())
            .mapToShadowAndOffset(blur)

        val diagonalRemainderBitmap = Bitmap.createBitmap(bitmap, 0, 0, horizontalRemainder.roundToInt(), verticalRemainder.roundToInt())
            .mapToShadowAndOffset(blur)

        // calculate the total width and height of the shadows so we can use to create a single bitmap of all the shadows
        val totalShadowWidth = (width.toFloat() / bitmapWidth.toFloat()) * shadowMapAndOffset.first.width
        val totalShadowHeight = (height.toFloat() / bitmapHeight.toFloat()) * shadowMapAndOffset.first.height

        val tiledBitmap = Bitmap.createBitmap(totalShadowWidth.roundToInt(), totalShadowHeight.roundToInt(), Bitmap.Config.ARGB_8888)
        val tiledBitmapCanvas = Canvas(tiledBitmap)

        // draw each row and column of shadows to a bitmap so we can use a single bitmap for all the tiled shadows
        // the last row and column will contain partial shadows depending on the horizontal and vertical remainder previously calculated
        for (row in 0 until numberOfVerticalShadows) {
            for (column in 0 until numberOfHorizontalShadows) {
                when {
                    row == numberOfVerticalShadows - 1 && column == numberOfHorizontalShadows - 1 && verticalRemainder > 0 && horizontalRemainder > 0 -> {
                        tiledBitmapCanvas.drawBitmap(
                            diagonalRemainderBitmap.first,
                            (column * bitmapWidth).toFloat(),
                            (row * bitmapHeight).toFloat(),
                            shadowPaint
                        )
                    }
                    row == numberOfVerticalShadows - 1 && verticalRemainder > 0 -> {
                        tiledBitmapCanvas.drawBitmap(
                            verticalRemainderBitmap.first,
                            (column * bitmapWidth).toFloat(),
                            (row * bitmapHeight).toFloat(),
                            shadowPaint
                        )
                    }
                    column == numberOfHorizontalShadows - 1 && horizontalRemainder > 0 -> {
                        tiledBitmapCanvas.drawBitmap(
                            horizontalRemainderBitmap.first,
                            (column * bitmapWidth).toFloat(),
                            (row * bitmapHeight).toFloat(),
                            shadowPaint
                        )
                    }
                    else -> tiledBitmapCanvas.drawBitmap(
                        shadowMapAndOffset.first,
                        (column * bitmapWidth).toFloat(),
                        (row * bitmapHeight).toFloat(),
                        shadowPaint
                    )
                }
            }
        }
        return tiledBitmap
    }
}
