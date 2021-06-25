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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.webkit.WebView
import app.judo.sdk.api.models.MaskPath
import app.judo.sdk.api.models.Shadow
import app.judo.sdk.ui.extensions.draw
import app.judo.sdk.ui.extensions.toDrawableShadow
import app.judo.sdk.ui.layout.Resolvers

@SuppressLint("SetJavaScriptEnabled")
internal class ExperienceWebView @JvmOverloads constructor(
    context: Context,
    private val resolvers: Resolvers? = null,
    val maskPath: MaskPath? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    var shadow: Shadow? = null
        set(value) {
            field = value
            value?.let { shadow ->
                resolvers?.let { resolvers ->
                    drawableShadow = shadow.toDrawableShadow(context, resolvers)
                }
            }
        }

    var scrollEnabled: Boolean = true
    private var drawableShadow: DrawableShadow? = null

    init {
        settings.apply {
            javaScriptCanOpenWindowsAutomatically = false
            domStorageEnabled = true
            javaScriptEnabled = true
            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun onDraw(canvas: Canvas) {
        maskPath?.let {
            canvas.clipPath(it.path)
        }
        drawableShadow?.draw(this, canvas)
        super.onDraw(canvas)
    }

    override fun overScrollBy(
        deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int,
        scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int,
        maxOverScrollY: Int, isTouchEvent: Boolean
    ): Boolean {
        return if (!scrollEnabled) false else super.overScrollBy(
            deltaX,
            deltaY,
            scrollX,
            scrollY,
            scrollRangeX,
            scrollRangeY,
            maxOverScrollX,
            maxOverScrollY,
            isTouchEvent
        )
    }
}