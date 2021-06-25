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

package app.judo.sdk.ui.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.ViewCompat
import app.judo.sdk.api.models.Image
import app.judo.sdk.api.models.ResizingMode
import app.judo.sdk.ui.drawables.GIFDrawable
import coil.drawable.MovieDrawable
import coil.drawable.ScaleDrawable
import kotlin.math.roundToInt

internal fun ImageView.applyImageWithScaleType(image: Image, drawable: Drawable) {
    // coil decodes gifs and creates either ScaleDrawable or MovieDrawable depending on OS version
    // we take these drawables and scale gifs with GIFScaleDrawable
    if (drawable is ScaleDrawable || drawable is MovieDrawable) {
        when (image.resizingMode) {
            ResizingMode.SCALE_TO_FIT, ResizingMode.STRETCH, ResizingMode.ORIGINAL -> {
                scaleType = ImageView.ScaleType.FIT_XY
                val animatedDrawable = if (drawable is ScaleDrawable) {
                    GIFDrawable(drawable.child)
                } else {
                    GIFDrawable(drawable)
                }
                setImageDrawable(animatedDrawable)
            }
            ResizingMode.SCALE_TO_FILL -> {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageDrawable(drawable)
                cropToPadding = true
            }
            ResizingMode.TILE -> {
                val bitmap = drawable.toBitmap()
                val scaledWidth = (bitmap.width * context.resources.displayMetrics.density) / image.resolution
                val scaledHeight = (bitmap.height * context.resources.displayMetrics.density) / image.resolution
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth.roundToInt(), scaledHeight.roundToInt(), false)

                setImageDrawable(scaledBitmap.toDrawable(context.resources).apply {
                    tileModeY = Shader.TileMode.REPEAT
                    tileModeX = Shader.TileMode.REPEAT
                })
            }
        }
    }
    // scaling bitmap for resizing modes
    else {
        val bitmap = drawable.toBitmap()
        when (image.resizingMode) {
            ResizingMode.SCALE_TO_FIT -> {
                scaleType = ImageView.ScaleType.FIT_XY
                setImageBitmap(bitmap)
            }
            ResizingMode.SCALE_TO_FILL -> {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageBitmap(bitmap)
                cropToPadding = true
            }
            ResizingMode.TILE -> {
                val scaledWidth = (bitmap.width * context.resources.displayMetrics.density) / image.resolution
                val scaledHeight = (bitmap.height * context.resources.displayMetrics.density) / image.resolution
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth.roundToInt(), scaledHeight.roundToInt(), false)

                setImageDrawable(scaledBitmap.toDrawable(context.resources).apply {
                    tileModeY = Shader.TileMode.REPEAT
                    tileModeX = Shader.TileMode.REPEAT
                })
            }
            ResizingMode.STRETCH -> {
                scaleType = ImageView.ScaleType.FIT_XY
                setImageBitmap(bitmap)
            }
            ResizingMode.ORIGINAL -> {
                scaleType = ImageView.ScaleType.FIT_XY
                setImageBitmap(bitmap)
            }
        }
    }
}

internal fun View.crossfadeWith(enteringView: View, enteringViewEndAlpha: Float, duration: Long = 200L) {
    enteringView.apply {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(enteringViewEndAlpha)
            .setDuration(duration)
            .setListener(null)
    }

    animate()
        .alpha(0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
            }
        })
}

inline fun View.doOnGlobalLayout(crossinline action: (view: View) -> Unit) {
    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action(this@doOnGlobalLayout)
            when {
                vto.isAlive -> vto.removeOnGlobalLayoutListener(this)
                else -> viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
    })
}

inline fun View.doOnLayout(crossinline action: (view: View) -> Unit) {
    if (ViewCompat.isLaidOut(this) && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextLayout {
            action(it)
        }
    }
}

inline fun View.doOnNextLayout(crossinline action: (view: View) -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)
            action(view)
        }
    })
}

inline fun View.doOnPreDraw(crossinline action: (view: View) -> Unit): OneShotPreDrawListener {
    return OneShotPreDrawListener.add(this) { action(this) }
}

inline fun View.doOnDetach(crossinline action: (view: View) -> Unit) {
    if (!ViewCompat.isAttachedToWindow(this)) {
        action(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {}

            override fun onViewDetachedFromWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                action(view)
            }
        })
    }
}