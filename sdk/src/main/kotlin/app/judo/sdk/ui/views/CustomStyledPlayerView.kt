package app.judo.sdk.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import app.judo.sdk.api.models.MaskPath
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView

internal class CustomStyledPlayerView @JvmOverloads constructor(
    context: Context,
    private val maskPath: MaskPath? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : PlayerView(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas?) {
        maskPath?.let { canvas?.clipPath(it.path) }

        super.onDraw(canvas)
    }

    fun playIfVisibleOrPause() {
        val visible = this.getGlobalVisibleRect(Rect())
        if (visible) {
            if (player?.isPlaying == false) player?.play()
        } else {
            player?.pause()
        }
    }

    fun playIfVisibleOrPauseIfPlaying() {
        val visible = this.getGlobalVisibleRect(Rect())
        if (visible && player?.isPlaying == false) {
            player?.play()
        } else {
            player?.pause()
        }
    }
}