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

package app.judo.sdk.ui.drawables

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import app.judo.sdk.ui.extensions.withSave

internal class GIFDrawable(private val child: Drawable) : Drawable(), Drawable.Callback, Animatable {
    private var childDx = 0f
    private var childDy = 0f
    private var childXScale = 1f
    private var childYScale = 1f

    init {
        child.callback = this
    }

    override fun draw(canvas: Canvas) {
        canvas.withSave {
            translate(childDx, childDy)
            scale(childXScale, childYScale)
            child.draw(this)
        }
    }

    override fun getAlpha() = child.alpha

    override fun setAlpha(alpha: Int) {
        child.alpha = alpha
    }

    @Suppress("DEPRECATION")
    override fun getOpacity() = child.opacity

    override fun getColorFilter() = child.colorFilter

    override fun setColorFilter(colorFilter: ColorFilter?) {
        child.colorFilter = colorFilter
    }

    override fun onBoundsChange(bounds: Rect) {
        val width = child.intrinsicWidth
        val height = child.intrinsicHeight
        if (width <= 0 || height <= 0) {
            child.bounds = bounds
            childDx = 0f
            childDy = 0f
            return
        }

        val targetWidth = bounds.width()
        val targetHeight = bounds.height()

        childXScale = targetWidth.toFloat() / width.toFloat()
        childYScale = targetHeight.toFloat() / height.toFloat()

        child.setBounds(0, 0, targetWidth, targetHeight)

        childDx = bounds.left.toFloat()
        childDy = bounds.top.toFloat()
    }

    override fun onLevelChange(level: Int) = child.setLevel(level)

    override fun onStateChange(state: IntArray) = child.setState(state)

    override fun getIntrinsicWidth() = child.intrinsicWidth

    override fun getIntrinsicHeight() = child.intrinsicHeight

    override fun unscheduleDrawable(who: Drawable, what: Runnable) = unscheduleSelf(what)

    override fun invalidateDrawable(who: Drawable) = invalidateSelf()

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) = scheduleSelf(what, `when`)

    override fun setTint(tintColor: Int) = child.setTint(tintColor)

    override fun setTintList(tint: ColorStateList?) = child.setTintList(tint)

    override fun setTintMode(tintMode: PorterDuff.Mode?) = child.setTintMode(tintMode)

    override fun isRunning() = child is Animatable && child.isRunning

    override fun start() {
        if (child is Animatable) child.start()
    }

    override fun stop() {
        if (child is Animatable) child.stop()
    }
}