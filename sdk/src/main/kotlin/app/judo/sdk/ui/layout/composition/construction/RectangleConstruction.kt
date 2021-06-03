package app.judo.sdk.ui.layout.composition.construction

import android.R
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import app.judo.sdk.api.models.Rectangle
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.*
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceView
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.calculateDisplayableAreaFromMaskPath
import app.judo.sdk.ui.extensions.setMaskPathFromMask

internal fun Rectangle.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    val rectangle = ExperienceView(context, resolvers, cornerRadius, maskPath).apply {
        id = View.generateViewId()
        shadow = this@construct.shadow
        fill = this@construct.fill
        border = this@construct.border
        isClickable = false
        alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.toInt(),
            sizeAndCoordinates.contentHeight.toInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.toInt(), sizeAndCoordinates.y.toInt(), 0, 0)
        }
    }

    action?.let {
        rectangle.foreground = createRipple(context, resolvers.statusBarColorResolver.color, cornerRadius)
        rectangle.setOnClickListener { _ ->
            resolvers.actionResolver(it)
        }
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    return listOfNotNull(background, rectangle, overlay)
}
