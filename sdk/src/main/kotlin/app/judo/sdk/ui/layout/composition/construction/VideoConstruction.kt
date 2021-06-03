package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.judo.sdk.R
import app.judo.sdk.api.models.Video
import app.judo.sdk.api.models.VideoResizingMode
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.ui.extensions.calculateDisplayableAreaFromMaskPath
import app.judo.sdk.ui.extensions.doOnDetach
import app.judo.sdk.ui.extensions.doOnPreDraw
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.CustomStyledPlayerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

internal fun Video.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)
    val player = SimpleExoPlayer.Builder(context).build()
    val video = CustomStyledPlayerView(context, maskPath).apply {
        id = View.generateViewId()
        tag = this@construct.id
        alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.roundToInt(),
            sizeAndCoordinates.contentHeight.roundToInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
        }
    }

    player.repeatMode = when(looping) {
        true -> Player.REPEAT_MODE_ALL
        false -> Player.REPEAT_MODE_OFF
    }
    video.useController = showControls
    video.resizeMode = if (resizingMode == VideoResizingMode.RESIZE_TO_FIT) {
        AspectRatioFrameLayout.RESIZE_MODE_FIT
    } else {
        AspectRatioFrameLayout.RESIZE_MODE_FILL
    }
    if (removeAudio) {
        player.volume = 0f
    }

    player.addMediaItem(MediaItem.fromUri(interpolatedSourceURL))
    video.player = player
    video.useArtwork = true

    video.findViewById<ImageButton>(R.id.exo_prev).apply {
        setImageDrawable(null)
        setOnClickListener(null)
    }
    video.findViewById<ImageButton>(R.id.exo_next).apply {
        setImageDrawable(null)
        setOnClickListener(null)
    }
    video.useArtwork = false
    player.prepare()

    fun setArtwork(drawable: Drawable) {
        val artworkView = video.findViewById<ImageView>(R.id.exo_artwork)
        artworkView?.setImageDrawable(drawable)
        artworkView?.visibility = View.VISIBLE
    }

    // this needs to wait until the player state is ready so we know the video aspect ratio so we can match the thumbnail
    interpolatedPosterImageURL?.let { posterImageURL ->
        (context as LifecycleOwner).lifecycleScope.launch(Dispatchers.IO) {
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

    player.addListener(object : Player.EventListener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                val artworkView = video.findViewById<ImageView>(R.id.exo_artwork)
                artworkView?.visibility = View.INVISIBLE
                player.removeListener(this)
            }
        }
    })

    video.doOnPreDraw {
        if (autoPlay && video.getGlobalVisibleRect(Rect())) player.play()

        video.doOnDetach {
            it.tag = null
            player.release()
        }
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    return listOfNotNull(background, video, overlay)
}