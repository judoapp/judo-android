package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import app.judo.sdk.R
import app.judo.sdk.api.models.Audio
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
import kotlin.math.roundToInt

internal fun Audio.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)
    val player = SimpleExoPlayer.Builder(context).build()
    val audio = CustomStyledPlayerView(context, maskPath).apply {
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

    player.addMediaItem(MediaItem.fromUri(interpolatedSourceURL))
    player.prepare()
    audio.controllerShowTimeoutMs = -1
    audio.player = player
    audio.useArtwork = false

    audio.findViewById<ImageButton>(R.id.exo_prev).apply {
        setImageDrawable(null)
        setOnClickListener(null)
    }
    audio.findViewById<ImageButton>(R.id.exo_next).apply {
        setImageDrawable(null)
        setOnClickListener(null)
    }

    audio.doOnPreDraw {
        if (autoPlay && audio.getGlobalVisibleRect(Rect())) player.play()

        audio.doOnDetach {
            it.tag = null
            player.release()
        }
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    return listOfNotNull(background, audio, overlay)
}