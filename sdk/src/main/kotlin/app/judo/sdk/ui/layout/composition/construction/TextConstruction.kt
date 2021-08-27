/*
 * Copyright (c) 2020-present, Rover Labs, Inc. All rights reserved.
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Rover.
 *
 * This copyright notice shall be included in all copies or substantial portions of
 * the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import app.judo.sdk.api.models.*
import app.judo.sdk.core.data.TextSkeleton
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.calculateDisplayableAreaFromMaskPath
import app.judo.sdk.ui.extensions.getSystemFontAttributes
import app.judo.sdk.ui.extensions.mapToFont
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.findNearestAncestor
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceTextView
import java.util.*
import kotlin.math.roundToInt

internal fun Text.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    val textView = ExperienceTextView(context, resolvers, shadow).apply {
        id = View.generateViewId()
        alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
        isClickable = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.roundToInt(),
            sizeAndCoordinates.contentHeight.roundToInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
        }

        val fontAttributes = this@construct.font.getSystemFontAttributes()
        val font = fontAttributes.weight.mapToFont()
        val fontStyle = this@construct.font.getEmphasisStyle() ?: font.style

        setTextColor(resolvers.colorResolver.resolveForColorInt(this@construct.textColor))
        setLineSpacing(0f, 1.0f)
        typeface = this@construct.typeface ?: Typeface.create(font.name, fontStyle)
        hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        includeFontPadding = false
        paint.isAntiAlias = true

        if (fontAttributes.isDynamic) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, fontAttributes.size)
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontAttributes.size)
        }

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

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)

    when {
        action != null && (this.background?.node is Image || this.background?.node is Rectangle)  -> {
            val rippleDrawable = when (this.background.node) {
                is Rectangle -> createRipple(context, resolvers.statusBarColorResolver.color, this.background.node.cornerRadius)
                else -> createRipple(context, resolvers.statusBarColorResolver.color)
            }
            background?.foreground = rippleDrawable
            background?.setOnClickListener {
                resolvers.actionResolver(action!!, treeNode.value)
            }
        }
        action != null && (this.overlay?.node is Image || this.overlay?.node is Rectangle) -> {
            val rippleDrawable = when (this.overlay.node) {
                is Rectangle -> createRipple(context, resolvers.statusBarColorResolver.color, this.overlay.node.cornerRadius)
                else -> createRipple(context, resolvers.statusBarColorResolver.color)
            }
            overlay?.foreground = rippleDrawable
            overlay?.setOnClickListener {
                resolvers.actionResolver(action!!, treeNode.value)
            }
        }
        action != null -> {
            textView.foreground = createRipple(context, resolvers.statusBarColorResolver.color)
            textView.setOnClickListener {
                val action = action ?: return@setOnClickListener
                resolvers.actionResolver(action, treeNode.value)
            }
        }
    }

    return listOfNotNull(background, textView, overlay)
}