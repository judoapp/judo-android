package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.VStack
import app.judo.sdk.ui.extensions.createRipple
import app.judo.sdk.ui.extensions.setMaskPath
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.toLayout
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceView
import kotlin.math.roundToInt

internal fun VStack.construct(
    context: Context,
    treeNode: TreeNode,
    resolvers: Resolvers
): List<View> {
    val view = ExperienceView(context, resolvers = resolvers).apply {
        id = View.generateViewId()
        shadow = this@construct.shadow
        alpha = opacity ?: 1f
        isClickable = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        setWillNotDraw(true)
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.roundToInt(),
            sizeAndCoordinates.contentHeight.roundToInt()).apply {
            setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.toInt(), 0, 0)
        }
    }

    setMaskPathFromMask(context, mask, treeNode.appearance)
    treeNode.children.forEach { (it.value as Layer).setMaskPath(maskPath) }

    val childViews = treeNode.children.flatMap { it.toLayout(context, resolvers) }.toMutableList()


    this.action?.let { action ->
        view.foreground = createRipple(context, resolvers.statusBarColorResolver.color)
        view.setOnClickListener {
            resolvers.actionResolver(action)
        }
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    val constructedViews = listOfNotNull(background, view) + childViews + listOfNotNull(overlay)
    childViews.forEach { it.alpha = it.alpha * (opacity ?: 1f) }
    view.alpha = view.alpha * (opacity ?: 1f)

    return constructedViews
}