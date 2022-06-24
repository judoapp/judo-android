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

package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.getSystemFontAttributes
import app.judo.sdk.ui.extensions.mapToFont
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*
import java.util.*
import kotlin.math.ceil

internal fun Text.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions, staticLayout: StaticLayout? = null) {
    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val heightConstraint: Dimension = when(val intrinsicHeight = parentConstraints.height) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(pxFrame.maxHeight.value)
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Inf
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                pxFrame?.minHeight != null -> Dimension.Value(pxFrame.minHeight)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(intrinsicHeight.value, pxFrame.minHeight))
                else -> Dimension.Value(intrinsicHeight.value)
            }
        }
    }

    val widthConstraint: Dimension = when(val intrinsicWidth = parentConstraints.width) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(pxFrame.maxWidth.value)
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Inf
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                pxFrame?.minWidth != null -> Dimension.Value(pxFrame.minWidth)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, intrinsicWidth.value))
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(intrinsicWidth.value)
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                pxFrame?.minWidth != null -> Dimension.Value(maxOf(intrinsicWidth.value, pxFrame.minWidth))
                else -> Dimension.Value(intrinsicWidth.value)
            }
        }
    }

    // measure text to determine text size

    val fontAttributes = this.font.getSystemFontAttributes()
    val font = fontAttributes.weight.mapToFont()
    val fontStyle = this.font.getEmphasisStyle() ?: font.style

    val typeface = typeface ?: Typeface.create(font.name, fontStyle)
    val textSize = if (fontAttributes.isDynamic) {
        fontAttributes.size.dp.value * context.resources.displayMetrics.scaledDensity
    } else {
        fontAttributes.size.dp.value * context.resources.displayMetrics.density
    }

    val text = when (transform) {
        TextTransform.UPPERCASE -> this.interpolatedText.toUpperCase(Locale.ROOT)
        TextTransform.LOWERCASE -> this.interpolatedText.toLowerCase(Locale.ROOT)
        null -> this.interpolatedText
    }

    val textAlignment = when (textAlignment) {
        TextAlignment.LEADING -> Paint.Align.LEFT
        TextAlignment.TRAILING -> Paint.Align.RIGHT
        else -> Paint.Align.CENTER
    }

    val paint = TextPaint().apply {
        this.textSize = textSize
        this.typeface = typeface
        this.isAntiAlias = true
        this.textAlign = textAlignment
    }

    val textLayoutAlign = when (textAlignment) {
        Paint.Align.CENTER -> Layout.Alignment.ALIGN_CENTER
        Paint.Align.LEFT -> Layout.Alignment.ALIGN_NORMAL
        Paint.Align.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
    }

    val widthForMeasure = when(widthConstraint) {
        is Dimension.Inf -> treeNode.rootNodeWidth()
        is Dimension.Value -> widthConstraint.value - (padding?.leading?.dp?.toPx(context) ?: 0f) - (padding?.trailing?.dp?.toPx(context) ?: 0f)
    }

    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)
    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)

    val layout = staticLayout ?: StaticLayout.Builder.obtain(text, 0, text.length, paint, maxOf(0f, ceil(widthForMeasure)).toInt())
        .setAlignment(textLayoutAlign)
        .setLineSpacing(0f, 1f)
        .setIncludePad(false)
        .setEllipsize(TextUtils.TruncateAt.END)
        .setMaxLines(lineLimit ?: Int.MAX_VALUE)
        .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE)
        .build()

    firstBaselineToTopDistance = layout.getLineBaseline(0).toFloat()

    val lineWidth = if (layout.lineCount == 1) {
        this.desiresWidth = layout.getEllipsisCount(0) > 0
        ceil(layout.getLineWidth(0))
    } else {
        this.desiresWidth = true
        var maxLineWidth = 0f

        for (lineNumber in 0 until layout.lineCount) {
            val lineEnd = layout.getLineEnd(lineNumber)
            val lineEndText = layout.text.subSequence(0, lineEnd)
            maxLineWidth = maxOf(ceil(layout.getLineWidth(lineNumber)), maxLineWidth)
        }

        maxLineWidth
    }

    val linesHeight = layout.height

    val nodeWidth = when {
        pxFrame?.maxWidth is MaxWidth.Finite && parentConstraints.width is Dimension.Inf -> minOf(pxFrame.maxWidth.value, lineWidth + horizontalPadding)
        pxFrame?.minWidth != null -> maxOf(lineWidth + horizontalPadding, pxFrame.minWidth)
        pxFrame.unboundedWidth() || widthConstraint is Dimension.Inf -> minOf(lineWidth, widthForMeasure) + horizontalPadding
        else -> (widthConstraint as Dimension.Value).value
    }

    val nodeHeight = when {
        pxFrame?.maxHeight is MaxHeight.Finite && parentConstraints.height is Dimension.Inf -> minOf(pxFrame.maxHeight.value, linesHeight.toFloat() + verticalPadding)
        pxFrame?.minHeight != null -> maxOf(linesHeight.toFloat() + verticalPadding, pxFrame.minHeight)
        pxFrame.unboundedHeight() || heightConstraint is Dimension.Inf -> linesHeight.toFloat() + verticalPadding
        else -> (heightConstraint as Dimension.Value).value
    }

    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
        width = nodeWidth,
        height = nodeHeight,
        contentHeight = linesHeight.toFloat(),
        contentWidth = lineWidth
    )

    background?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
}