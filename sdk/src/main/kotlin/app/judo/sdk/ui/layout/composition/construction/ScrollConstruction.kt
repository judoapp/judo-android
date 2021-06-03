package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import app.judo.sdk.api.models.Audio
import app.judo.sdk.api.models.Axis
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.ScrollContainer
import app.judo.sdk.api.models.Video
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.setMaskPath
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.extensions.toIntPx
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.getAllLeafNodes
import app.judo.sdk.ui.layout.composition.toLayout
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceScrollView
import app.judo.sdk.ui.views.CustomStyledPlayerView
import app.judo.sdk.ui.views.HorizontalExperienceScrollView
import java.util.UUID

internal fun ScrollContainer.construct(
    context: Context,
    treeNode: TreeNode,
    resolvers: Resolvers
): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)

    val leafNodes = treeNode.getAllLeafNodes()
    val mediaChildIDs = leafNodes.filter { it is Audio && it.autoPlay || it is Video && it.autoPlay }.map { it.id }

    val scrollViewInnerFrame = FrameLayout(context).apply {
        id = View.generateViewId()
        isClickable = false
        clipChildren = false
        clipToOutline = false
        layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    val scrollView = if (this.axis == Axis.VERTICAL) {
        ExperienceScrollView(context, resolvers = resolvers).apply {
            id = View.generateViewId()
            tag = UUID.fromString(this@construct.id)
            alpha = opacity ?: 1f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                forceHasOverlappingRendering(false)
            }
            clipChildren = true
            clipToOutline = true
            shadow = this@construct.shadow
            isVerticalScrollBarEnabled = !this@construct.disableScrollBar
            isHorizontalScrollBarEnabled = !this@construct.disableScrollBar
            layoutParams = FrameLayout.LayoutParams(
                this@construct.sizeAndCoordinates.width.toInt(),
                this@construct.sizeAndCoordinates.height.toInt()
            ).apply {
                setMargins(sizeAndCoordinates.x.toInt(), sizeAndCoordinates.y.toInt(), 0, 0)
                setPadding(0, 0, 0, padding?.bottom?.dp?.toIntPx(context) ?: 0)
            }
        }
    } else {
        HorizontalExperienceScrollView(context, resolvers = resolvers).apply {
            id = View.generateViewId()
            tag = UUID.fromString(this@construct.id)
            alpha = opacity ?: 1f
            shadow = this@construct.shadow
            clipChildren = true
            clipToOutline = true
            isVerticalScrollBarEnabled = !this@construct.disableScrollBar
            isHorizontalScrollBarEnabled = !this@construct.disableScrollBar
            layoutParams = FrameLayout.LayoutParams(
                this@construct.sizeAndCoordinates.width.toInt(),
                this@construct.sizeAndCoordinates.height.toInt()
            ).apply {
                setMargins(sizeAndCoordinates.x.toInt(), sizeAndCoordinates.y.toInt(), 0, 0)
                setPadding(0, 0, padding?.trailing?.dp?.toIntPx(context) ?: 0, 0)
            }
        }
    }

    scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
        mediaChildIDs.forEach {
            scrollView.findViewWithTag<CustomStyledPlayerView>(it)?.playIfVisibleOrPause()
        }
    }

    treeNode.children.forEach { (it.value as Layer).setMaskPath(maskPath) }

    treeNode.children.forEach {
        val views = it.toLayout(context, resolvers)
        val node = it.value as Layer
        val width = node.sizeAndCoordinates.width
        val height = node.sizeAndCoordinates.height

        // some views like hstacks/vstacks don't have views that represent their size so we have to add a view that gives
        // the scrollview the correct size instead of relying on the size of its child
        val viewForSizing =
            View(context).apply { layoutParams = FrameLayout.LayoutParams(width.toInt(), height.toInt()) }
        scrollViewInnerFrame.addView(viewForSizing)
        views.forEach { view -> scrollViewInnerFrame.addView(view) }
    }

    scrollView.addView(scrollViewInnerFrame)

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    return listOfNotNull(background, scrollView, overlay)
}