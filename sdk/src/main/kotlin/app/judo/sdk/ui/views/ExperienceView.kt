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
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.View
import app.judo.sdk.api.models.Border
import app.judo.sdk.api.models.Fill
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.MaskPath
import app.judo.sdk.api.models.Rectangle
import app.judo.sdk.api.models.Shadow
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.draw
import app.judo.sdk.ui.extensions.toDrawableBorder
import app.judo.sdk.ui.extensions.toDrawableFill
import app.judo.sdk.ui.extensions.toDrawableShadow
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.Resolvers

data class DrawableShadow(val paint: Paint, val radius: Float, val offset: FloatPoint)
data class DrawableBorder(val paint: Paint, val radius: Float, val width: Float)
data class DrawableFill(val paint: Paint, val radius: Float)

internal class ExperienceView @JvmOverloads constructor(
    context: Context,
    private val resolvers: Resolvers? = null,
    private val radius: Float = 0f,
    private val maskPath: MaskPath? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var fill: Fill? = null
    set(value) {
        field = value
        value?.let { fill ->
            resolvers?.let { resolvers ->
                this.drawableFill = fill.toDrawableFill(this, context, resolvers, radius)
            }
        }
    }

    var border: Border? = null
    set(value) {
        field = value
        value?.let { border ->
            resolvers?.let { resolvers ->
                drawableBorder = border.toDrawableBorder(context, resolvers, radius)
            }
        }
    }

    var shadow: Shadow? = null
        set(value) {
            field = value
            value?.let { shadow ->
                resolvers?.let { resolvers ->
                    drawableShadow = shadow.toDrawableShadow(context, resolvers, radius)
                }
            }
        }

    private var drawableShadow: DrawableShadow? = null
    private var drawableBorder: DrawableBorder? = null
    private var drawableFill: DrawableFill? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fill?.let {
            resolvers?.let { resolvers ->
                this.drawableFill = it.toDrawableFill(this, context, resolvers, radius)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        maskPath?.let {
            canvas?.clipPath(it.path)
        }

        drawableShadow?.draw(this, canvas)
        drawableFill?.draw(this, canvas)
        drawableBorder?.draw(this, canvas)
    }
}
