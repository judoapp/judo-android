package app.judo.sdk.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.judo.sdk.R
import app.judo.sdk.api.models.MaskPath
import app.judo.sdk.api.models.ResizingMode
import app.judo.sdk.api.models.VideoResizingMode
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.ui.MediaPlayerInstanceManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.*

internal class ExperienceMediaPlayerView @JvmOverloads constructor(
    context: Context,
    private val maskPath: MaskPath? = null,
    private val looping: Boolean,
    private val autoPlay: Boolean,
    private val sourceURL: String,
    private val showControls: Boolean,
    private val resizingMode: VideoResizingMode? = null,
    private val removeAudio: Boolean = false,
    private val posterImageURL: String? = null,
    private val timeoutControls: Boolean = true,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : PlayerView(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas?) {
        maskPath?.let { canvas?.clipPath(it.path) }
        super.onDraw(canvas)
    }

    init {
        findViewById<ImageButton>(R.id.exo_prev)?.apply {
            setImageDrawable(null)
            setOnClickListener(null)
        }
        findViewById<ImageButton>(R.id.exo_next)?.apply {
            setImageDrawable(null)
            setOnClickListener(null)
        }
    }

    fun pausePlayer() {
        player?.pause()
    }

    fun setupIfVisible(visibleInParent: Boolean = true) {
        val visible = this.getGlobalVisibleRect(Rect()) && visibleInParent
        val tag = tag as? String ?: return
        if (!autoPlay) {
            if (visible && (!MediaPlayerInstanceManager.instanceStillAvailable(tag) || player == null)) {
                val player = MediaPlayerInstanceManager.getInstance(tag, context)
                this.player = player
                setupPlayer(context, player)
            }
        } else {
            setupAndPlayIfVisible(visible)
        }
    }

    private fun setupAndPlayIfVisible(visible: Boolean) {
        val tag = tag as? String ?: return
        when {
            visible && (!MediaPlayerInstanceManager.instanceStillAvailable(tag) || player == null) -> {
                val exoPlayer = MediaPlayerInstanceManager.getInstance(tag, context)
                this.player = exoPlayer
                setupPlayer(context, exoPlayer)
                player?.play()
            }
            visible && MediaPlayerInstanceManager.instanceStillAvailable(tag) -> {
                player?.play()
            }
            else -> player?.pause()
        }
    }

    private fun setupPlayer(context: Context, player: SimpleExoPlayer) {
        player.repeatMode = when(looping) {
            true -> Player.REPEAT_MODE_ALL
            false -> Player.REPEAT_MODE_OFF
        }
        useController = showControls
        if (!timeoutControls) controllerShowTimeoutMs = -1
        if (removeAudio) {
            player.volume = 0f
        }
        useArtwork = false

        player.addMediaItem(MediaItem.fromUri(sourceURL))
        player.prepare()
        resizeMode = if (resizingMode == VideoResizingMode.RESIZE_TO_FIT) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }

        fun setArtwork(drawable: Drawable) {
            val artworkView = findViewById<ImageView>(R.id.exo_artwork)
            artworkView?.setImageDrawable(drawable)
            artworkView?.visibility = View.VISIBLE
        }

        player.addListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    val artworkView = findViewById<ImageView>(R.id.exo_artwork)
                    artworkView?.visibility = View.INVISIBLE
                    player.removeListener(this)
                }
            }
        })

        posterImageURL?.let { posterImageURL ->
            (context as? LifecycleOwner)?.lifecycleScope?.launchWhenResumed {
                Environment.current.imageService
                    .getImageAsync(ImageService.Request(posterImageURL))
                    .await().drawable?.let { drawable ->
                        withContext(Dispatchers.Main) {
                            if (player.playbackState == Player.STATE_READY && !player.isPlaying) {
                                setArtwork(drawable)
                            } else {
                                player.addListener(object : Player.EventListener {
                                    override fun onPlaybackStateChanged(state: Int) {
                                        super.onPlaybackStateChanged(state)
                                        if (state == Player.STATE_READY && !player.isPlaying) {
                                            setArtwork(drawable)
                                            player.removeListener(this)
                                        }
                                    }
                                })
                            }
                        }
                    }
            }
        }
    }
}
