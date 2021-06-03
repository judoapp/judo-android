package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.judo.sdk.api.models.Image
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.data.BlurHashDecoder
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.applyImageWithScaleType
import app.judo.sdk.ui.extensions.calculateDisplayableAreaFromMaskPath
import app.judo.sdk.ui.extensions.crossfadeWith
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

internal fun Image.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    fun createExperienceImageView(): ExperienceImageView {
        return ExperienceImageView(context, resolvers = resolvers, shadow = shadow, resizingMode = resizingMode, maskPath = maskPath).apply {
            id = View.generateViewId()
            scaleType = ImageView.ScaleType.FIT_XY
            alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
            isClickable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
            layoutParams = FrameLayout.LayoutParams(
                sizeAndCoordinates.contentWidth.roundToInt(),
                sizeAndCoordinates.contentHeight.roundToInt()
            ).apply {
                setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
            }
        }
    }

    val imageView = createExperienceImageView()

    action?.let {
        imageView.foreground = createRipple(context, resolvers.statusBarColorResolver.color)
        imageView.setOnClickListener {
            resolvers.actionResolver(action)
        }
    }

    var blurHashView: AppCompatImageView? = null
    val lifecycleOwner = context as LifecycleOwner
    val url = if (context.isDarkMode(treeNode.appearance)) interpolatedDarkModeImageURL ?: interpolatedImageURL else interpolatedImageURL
    val shouldDisplayBlurHash = !Environment.current.imageService.isImageCached(url) && blurHash != null

    fun getImage(url: String, onComplete: (Drawable) -> Unit) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            Environment.current.imageService
                .getImageAsync(ImageService.Request(url))
                .await().drawable?.let { drawable ->
                    withContext(Dispatchers.Main) {
                        onComplete(drawable)
                    }
            }
        }
    }

    when {
        drawable != null -> {
            val drawable = if (context.isDarkMode(treeNode.appearance) && darkModeDrawable != null) darkModeDrawable else drawable
            imageView.applyImageWithScaleType(this, drawable!!)
        }
        shouldDisplayBlurHash -> {
            val blurHash = if (context.isDarkMode(treeNode.appearance) && darkModeBlurHash != null) darkModeBlurHash else blurHash
            val blurHashBitmap = BlurHashDecoder.decode(blurHash, 20, 20)

            blurHashBitmap?.let {
                blurHashView = createExperienceImageView()
                blurHashView?.applyImageWithScaleType(this, BitmapDrawable(context.resources, blurHashBitmap))
            }

            getImage(url) {
                blurHashView!!.crossfadeWith(imageView, imageView.alpha)
                imageView.applyImageWithScaleType(this@construct, it)
            }
        }
        else -> {
            getImage(url) {
                imageView.applyImageWithScaleType(this@construct, it)
            }
        }
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    return listOfNotNull(background, imageView, blurHashView, overlay)
}