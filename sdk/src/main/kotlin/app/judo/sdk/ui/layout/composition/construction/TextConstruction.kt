package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import app.judo.sdk.api.models.Image
import app.judo.sdk.api.models.Rectangle
import app.judo.sdk.api.models.Text
import app.judo.sdk.api.models.TextAlignment
import app.judo.sdk.api.models.TextTransform
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.calculateDisplayableAreaFromMaskPath
import app.judo.sdk.ui.extensions.getSystemFontAttributes
import app.judo.sdk.ui.extensions.mapToFont
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.sizing.computeSize
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceTextView
import java.util.*

internal fun Text.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    val textView = ExperienceTextView(context, resolvers, shadow).apply {
        id = View.generateViewId()
        alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
        isClickable = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.toInt(),
            sizeAndCoordinates.contentHeight.toInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.toInt(), sizeAndCoordinates.y.toInt(), 0, 0)
        }

        val fontAttributes = this@construct.font.getSystemFontAttributes(context)
        val font = fontAttributes.weight.mapToFont()
        val fontStyle = this@construct.font.getEmphasisStyle() ?: font.style

        setTextColor(resolvers.colorResolver.resolveForColorInt(this@construct.textColor))
        setLineSpacing(0f, 1.0f)
        typeface = this@construct.typeface ?: Typeface.create(font.name, fontStyle)
        hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        includeFontPadding = false
        paint.isAntiAlias = true
        textSize = fontAttributes.size

        if (Build.VERSION.SDK_INT >= 29) {
            isFallbackLineSpacing = false
        }
        ellipsize = TextUtils.TruncateAt.END
        lineLimit?.let { maxLines = it }

        text = when (transform) {
            TextTransform.UPPERCASE -> this@construct.interpolatedText.toUpperCase(Locale.ROOT)
            TextTransform.LOWERCASE -> this@construct.interpolatedText.toLowerCase(Locale.ROOT)
            null -> this@construct.interpolatedText
        }

        if (sizeAndCoordinates.contentWidth.toInt() == 0) text = ""

        textAlignment = when (this@construct.textAlignment) {
            TextAlignment.LEADING -> View.TEXT_ALIGNMENT_TEXT_START
            TextAlignment.TRAILING -> View.TEXT_ALIGNMENT_TEXT_END
            else -> View.TEXT_ALIGNMENT_CENTER
        }
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers)

    when {
        action != null && (this.background?.node is Image || this.background?.node is Rectangle)  -> {
            val rippleDrawable = when (this.background.node) {
                is Rectangle -> createRipple(context, resolvers.statusBarColorResolver.color, this.background.node.cornerRadius)
                else -> createRipple(context, resolvers.statusBarColorResolver.color)
            }
            background?.foreground = rippleDrawable
            background?.setOnClickListener { resolvers.actionResolver(action) }
        }
        action != null && (this.overlay?.node is Image || this.overlay?.node is Rectangle) -> {
            val rippleDrawable = when (this.overlay.node) {
                is Rectangle -> createRipple(context, resolvers.statusBarColorResolver.color, this.overlay.node.cornerRadius)
                else -> createRipple(context, resolvers.statusBarColorResolver.color)
            }
            overlay?.foreground = rippleDrawable
            overlay?.setOnClickListener { resolvers.actionResolver(action) }
        }
        action != null -> {
            textView.foreground = createRipple(context, resolvers.statusBarColorResolver.color)
            textView.setOnClickListener { resolvers.actionResolver(action) }
        }
    }

    return listOfNotNull(background, textView, overlay)
}