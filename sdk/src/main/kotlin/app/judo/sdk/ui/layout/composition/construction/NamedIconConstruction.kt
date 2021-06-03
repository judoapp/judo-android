package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import app.judo.sdk.api.models.NamedIcon
import app.judo.sdk.api.models.ResizingMode
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.views.ExperienceImageView

internal fun NamedIcon.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    fun createExperienceImageView(): ExperienceImageView {
        return ExperienceImageView(context, resolvers = resolvers, shadow = shadow, resizingMode = ResizingMode.ORIGINAL, maskPath = maskPath).apply {
            id = View.generateViewId()
            scaleType = ImageView.ScaleType.FIT_XY
            alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
            isClickable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
            layoutParams = FrameLayout.LayoutParams(
                sizeAndCoordinates.contentWidth.toInt(),
                sizeAndCoordinates.contentHeight.toInt()
            ).apply {
                setMargins(sizeAndCoordinates.x.toInt(), sizeAndCoordinates.y.toInt(), 0, 0)
            }
        }
    }

    val imageView = createExperienceImageView()

    val resourceId: Int = context.resources.getIdentifier(
        "judo_sdk_${icon.materialName}".replace(".", "_"), "drawable",
        context.packageName
    )
    try {
        imageView.setImageDrawable(ResourcesCompat.getDrawable(context.resources, resourceId, null)
            ?.apply { setTint(resolvers.colorResolver.resolveForColorInt(color)) })

    } catch (e : Resources.NotFoundException) {
        Log.d("Named Icon $id", "icon ${icon.materialName} not found")
    }

    action?.let {
        imageView.foreground = createRipple(context, resolvers.statusBarColorResolver.color)
        imageView.setOnClickListener {
            resolvers.actionResolver(action)
        }
    }

    return listOfNotNull(imageView)
}