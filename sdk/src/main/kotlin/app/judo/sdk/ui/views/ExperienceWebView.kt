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