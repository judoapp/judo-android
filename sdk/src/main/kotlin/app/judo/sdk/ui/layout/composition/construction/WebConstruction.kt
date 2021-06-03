package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import android.widget.FrameLayout
import app.judo.sdk.api.models.WebView
import app.judo.sdk.ui.extensions.calculateDisplayableAreaFromMaskPath
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceWebView
import kotlin.math.roundToInt

internal fun WebView.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    val webView = ExperienceWebView(context, resolvers, maskPath).apply {
        id = View.generateViewId()
        alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        shadow = this@construct.shadow
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.roundToInt(),
            sizeAndCoordinates.contentHeight.roundToInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
        }
        setBackgroundColor(Color.TRANSPARENT)
        // in order to enable setting opacity on the content of the Webview
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        scrollEnabled = isScrollEnabled
        loadUrl(this@construct.interpolatedURL)
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    return listOfNotNull(background, webView, overlay)
}