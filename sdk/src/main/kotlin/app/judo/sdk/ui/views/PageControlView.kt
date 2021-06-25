/*
 * Copyright (c) 2020-present, Rover Labs, Inc. All rights reserved.
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Rover.
 *
 * This copyright notice shall be included in all copies or substantial portions of
 * the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package app.judo.sdk.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.api.models.MaskPath
import app.judo.sdk.api.models.Point
import app.judo.sdk.api.models.Shadow
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.layout.Resolvers
import kotlin.properties.Delegates

internal class PageControlView @JvmOverloads constructor(
    context: Context,
    private val maskPath: MaskPath? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val imageIndicators: Boolean = false,
) : View(context, attrs, defStyleAttr) {

    lateinit var resolvers: Resolvers
    lateinit var pageIndicatorColor: ColorVariants
    lateinit var currentPageIndicatorColor: ColorVariants
    var shadow: Shadow? = null
        set(value) {
            field = value
            value?.let { shadow -> drawableShadow = shadow.toDrawableShadow(context, resolvers) }
        }
    var hidesForSinglePage: Boolean = false
    private var drawableShadow: DrawableShadow? = null

    var indicatorCount = 0
        set(value) {
            field = value
            if (imageIndicators) {
                calculateIndicatorPositionsForImages(currentlySelectedItem, currentItemShapeBitmapAndOffset, normalItemShapeBitmapAndOffset)
            } else {
                calculateIndicatorPositions()
            }
        }

    var currentlySelectedItem = 0
        set(value) {
            field = value
            if (imageIndicators) calculateIndicatorPositionsForImages(currentlySelectedItem, currentItemShapeBitmapAndOffset, normalItemShapeBitmapAndOffset)
            invalidate()
        }

    private var currentItemShapeBitmapAndOffset: Pair<Bitmap, Point>? = null
    private var normalItemShapeBitmapAndOffset: Pair<Bitmap, Point>? = null

    var currentItemBitmap: Bitmap? by Delegates.observable(null) { _, _, newBitmap ->
        newBitmap?.let {
            currentItemShapeBitmapAndOffset = it.mapToShadowAndOffset()
        }
        calculateIndicatorPositionsForImages(currentlySelectedItem, currentItemShapeBitmapAndOffset, normalItemShapeBitmapAndOffset)
        if (normalItemShapeBitmapAndOffset != null) invalidate()
    }
    var normalItemBitmap: Bitmap? by Delegates.observable(null) { _, _, newBitmap ->
        newBitmap?.let {
            normalItemShapeBitmapAndOffset = it.mapToShadowAndOffset()
        }
        calculateIndicatorPositionsForImages(currentlySelectedItem, currentItemShapeBitmapAndOffset, normalItemShapeBitmapAndOffset)
        if (currentItemShapeBitmapAndOffset != null) invalidate()
    }

    var imageIndicatorPositionInfo: List<PointF> = emptyList()

    private val spaceBetweenIndicators = 9.dp.toPx(context)
    private val indicatorRadius = 3.5.dp.toPx(context)
    private val indicatorCenterToCenterDistance = spaceBetweenIndicators + (indicatorRadius * 2)

    private val currentPagePaint by lazy {
        Paint().apply {
            setColor(resolvers.colorResolver, currentPageIndicatorColor)
            isAntiAlias = true
        }
    }

    private val unselectedPagePaint by lazy {
        Paint().apply {
            setColor(resolvers.colorResolver, pageIndicatorColor)
            isAntiAlias = true
        }
    }

    private var firstIndicatorX = 0f
    private var indicatorY = 0f

    private fun shouldDraw() = indicatorCount > 1 || !hidesForSinglePage && indicatorCount == 1

    override fun onDraw(canvas: Canvas?) {
        if (shouldDraw()) {
            maskPath?.let {
                canvas?.clipPath(it.path)
            }
            drawableShadow?.draw(this, canvas)

            if (imageIndicators) {
                if (imageIndicatorPositionInfo.isNotEmpty()) {
                    for (i in 0 until indicatorCount) {
                        if (i == currentlySelectedItem) {
                            currentItemShapeBitmapAndOffset?.let {
                                canvas?.drawBitmap(
                                    it.first,
                                    imageIndicatorPositionInfo[i].x,
                                    imageIndicatorPositionInfo[i].y,
                                    currentPagePaint
                                )
                            }

                        } else {
                            normalItemShapeBitmapAndOffset?.let {
                                canvas?.drawBitmap(
                                    it.first,
                                    imageIndicatorPositionInfo[i].x,
                                    imageIndicatorPositionInfo[i].y,
                                    unselectedPagePaint
                                )
                            }
                        }
                    }
                }
            } else {
                for (i in 0 until indicatorCount) {
                    canvas?.drawCircle(
                        firstIndicatorX + (i * indicatorCenterToCenterDistance),
                        indicatorY,
                        indicatorRadius,
                        if (i == currentlySelectedItem) currentPagePaint else unselectedPagePaint
                    )
                }
            }


        }
    }

    private fun calculateIndicatorPositions() {
        val indicatorDiameter = indicatorRadius * 2
        val totalIndicatorsWidth = (indicatorCount * indicatorDiameter) + (spaceBetweenIndicators * (indicatorCount - 1))

        // adding indicator radius here because circles are drawn to canvas at the center of position given
        firstIndicatorX = ((width - totalIndicatorsWidth) / 2f) + (indicatorDiameter / 2f)
        indicatorY = ((height - indicatorDiameter) / 2f) + (indicatorDiameter / 2f)
    }

    private fun calculateIndicatorPositionsForImages(position: Int, currentItem: Pair<Bitmap, Point>?, normalItem: Pair<Bitmap, Point>?) {
        if (currentItem != null && normalItem != null) {
            val currentItemWidth = currentItem.first.width
            val normalItemWidth = normalItem.first.width

            val currentItemHeight = currentItem.first.height
            val normalItemHeight = normalItem.first.height

            val currentItemY = ((height - currentItemHeight) / 2f)
            val normalItemY = ((height - normalItemHeight) / 2f)

            val normalItemWidths = normalItemWidth * (indicatorCount - 1)
            val totalIndicatorsWidth = (normalItemWidths + currentItemWidth) + (spaceBetweenIndicators * (indicatorCount - 1))
            val firstIndicatorX = ((width - totalIndicatorsWidth) / 2f)
            var currentXPosition = firstIndicatorX
            val positionInfo: MutableList<PointF> = mutableListOf()
            for (i in 0 until indicatorCount) {
                if (position == i) {
                    positionInfo.add(PointF(currentXPosition, currentItemY))
                    currentXPosition += (currentItemWidth + spaceBetweenIndicators)
                } else {
                    positionInfo.add(PointF(currentXPosition, normalItemY))
                    currentXPosition += (normalItemWidth + spaceBetweenIndicators)
                }
            }

            imageIndicatorPositionInfo = positionInfo
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (imageIndicators) {
            calculateIndicatorPositionsForImages(currentlySelectedItem, currentItemShapeBitmapAndOffset, normalItemShapeBitmapAndOffset)
        } else {
            calculateIndicatorPositions()
        }
    }
}
