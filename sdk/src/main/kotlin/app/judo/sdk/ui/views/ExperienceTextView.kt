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