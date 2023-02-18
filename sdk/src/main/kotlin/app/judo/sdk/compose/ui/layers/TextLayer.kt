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

package app.judo.sdk.compose.ui.layers

import android.os.Trace
import android.util.Log
import android.util.Size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.judo.sdk.compose.model.nodes.Text
import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.data.Interpolator
import app.judo.sdk.compose.ui.data.makeDataContext
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.layout.mapMaxIntrinsicWidthAsMeasure
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.values.getComposeColor

@Composable
internal fun TextLayer(node: Text) {
    TextLayer(
        text = node.text,
        transform = node.transform,
        textColor = node.textColor,
        shadow = node.shadow,
        textAlignment = node.textAlignment,
        lineLimit = node.lineLimit,
        font = node.font,
        judoModifiers = JudoModifiers(node)
    )
}

@Composable
internal fun TextLayer(
    text: String,
    modifier: Modifier = Modifier,
    transform: TextTransform? = null,
    textColor: ColorReference = ColorReference.SystemColor("label"),
    shadow: app.judo.sdk.compose.model.values.Shadow? = null,
    textAlignment: TextAlignment = TextAlignment.LEADING,
    lineLimit: Int? = null,
    font: Font = Font.Dynamic("body", Emphases(false, false)),
    judoModifiers: JudoModifiers = JudoModifiers()
) {
    val stringTable = Environment.LocalExperienceModel.current?.localizations
    val localizedText = stringTable?.resolve(text) ?: text

    val dataContext = makeDataContext(
        userInfo = Environment.LocalUserInfo.current?.invoke() ?: emptyMap(),
        urlParameters = Environment.LocalUrlParameters.current,
        data = Environment.LocalData.current
    )
    val interpolator = Interpolator(
        dataContext
    )
    val interpolatedText = interpolator.interpolate(localizedText)
    interpolatedText?.let {
        LayerBox(judoModifiers = judoModifiers, modifier = modifier) {
            Layout({
                InnerTextLayer(
                    interpolatedText = it,
                    transform = transform,
                    textColor = textColor,
                    shadow = shadow,
                    textAlignment = textAlignment,
                    lineLimit = lineLimit,
                    font = font
                )
            }, measurePolicy = textLayerMeasurePolicy())
        }
    }
}

@Composable
private fun InnerTextLayer(
    interpolatedText: String,
    transform: TextTransform?,
    textColor: ColorReference,
    shadow: app.judo.sdk.compose.model.values.Shadow?,
    textAlignment: TextAlignment,
    lineLimit: Int?,
    font: Font
) {
    val tag = "InnerTextLayer"
    val transformedText = when (transform) {
        TextTransform.UPPERCASE -> {
            interpolatedText.uppercase()
        }
        TextTransform.LOWERCASE -> {
            interpolatedText.lowercase()
        }
        null -> interpolatedText
    }

    @Composable
    fun textForFont(
        size: TextUnit,
        weight: androidx.compose.ui.text.font.FontWeight?,
        family: FontFamily?,
        style: androidx.compose.ui.text.font.FontStyle?
    ) {
        var textShadow: androidx.compose.ui.graphics.Shadow? = null

        shadow?.let {
            textShadow = Shadow(
                color = it.color.getComposeColor(Environment.LocalIsDarkTheme.current),
                offset = Offset(
                    with(LocalDensity.current) { it.x.dp.toPx() },
                    with(LocalDensity.current) { it.y.dp.toPx() }
                ),
                blurRadius = it.blur.toFloat()
            )
        }

        Text(
            text = transformedText,
            color = textColor.getComposeColor(),
            fontSize = size,
            fontWeight = weight,
            fontFamily = family,
            fontStyle = style,
            textAlign = textAlignment.composeValue,
            maxLines = lineLimit ?: Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                shadow = textShadow
            )
        )
    }

    @Composable
    fun textForFixedFont(font: Font.Fixed, setItalic: Boolean = false) {
        textForFont(
            size = font.size.sp,
            weight = font.weight.composeValue,
            family = null, // system font
            style = if (setItalic) androidx.compose.ui.text.font.FontStyle.Italic else null
        )
    }

    @Composable
    fun textForCustomFont(fontName: String, size: Float) {
        val typeFaceMapping = Environment.LocalTypefaceMapping.current
        if (typeFaceMapping == null) {
            // fall back to system
            Log.e(tag, "Typeface mappings missing, falling back to system font")
            textForFont(
                size = size.sp,
                family = null, // system font
                weight = null,
                style = null
            )
            return
        }
        val typeface = typeFaceMapping.mapping[fontName]?.let { FontFamily(it) }

        if (typeface == null) {
            Log.e(tag, "Custom font missing: $fontName, only these are available: $typeFaceMapping")
        }

        textForFont(
            size = size.sp,
            family = typeface,
            weight = null,
            style = null
        )
    }

    when (font) {
        is Font.Custom -> {
            // can use custom font face.
            textForCustomFont(
                fontName = font.fontName,
                size = font.size
            )
        }
        is Font.Document -> {
            // Equivalent to custom, but using a style defined on a DocumentFont.
            val documentFont = Environment.LocalDocumentFonts.current.firstOrNull { it.fontFamily == font.fontFamily }

            if (documentFont == null) {
                Log.e(tag, "Document Font not found for font family '${font.fontFamily}'. Falling back to system.")
                textForFixedFont(
                    font = Font.Fixed(size = 17f, weight = FontWeight.Regular)
                )
            } else {
                val customFont = documentFont.styleByName(font.textStyle)
                if (customFont == null) {
                    Log.e(tag, "Text style not matched for '${font.textStyle}'. Falling back to system.")
                    textForFixedFont(
                        font = Font.Fixed(size = 17f, weight = FontWeight.Regular)
                    )
                } else {
                    textForCustomFont(fontName = customFont.fontName, size = customFont.size)
                }
            }
        }
        is Font.Dynamic -> {
            // SwiftUI's built-in styles.
            val fixedFont = when (FontStyle.getStyleFromCode(font.textStyle)) {
                FontStyle.LARGE_TITLE -> Font.Fixed(size = 34f, weight = FontWeight.Regular)
                FontStyle.TITLE_1 -> Font.Fixed(size = 28f, weight = FontWeight.Regular)
                FontStyle.TITLE_2 -> Font.Fixed(size = 22f, weight = FontWeight.Regular)
                FontStyle.TITLE_3 -> Font.Fixed(size = 20f, weight = FontWeight.Regular)
                FontStyle.HEADLINE -> Font.Fixed(size = 17f, weight = FontWeight.SemiBold)
                FontStyle.BODY -> Font.Fixed(size = 17f, weight = FontWeight.Regular)
                FontStyle.CALLOUT -> Font.Fixed(size = 16f, weight = FontWeight.Regular)
                FontStyle.SUBHEADLINE -> Font.Fixed(size = 15f, weight = FontWeight.Regular)
                FontStyle.FOOTNOTE -> Font.Fixed(size = 13f, weight = FontWeight.Regular)
                FontStyle.CAPTION_1 -> Font.Fixed(size = 12f, weight = FontWeight.Regular)
                FontStyle.CAPTION_2 -> Font.Fixed(size = 11f, weight = FontWeight.Regular)
            }

            // in order to support the Bold emphasis, we have to change the font weight.
            val fixedFontWithRevisedWeight = fixedFont.copy(
                weight = if (font.emphases.bold) { FontWeight.Bold } else fixedFont.weight
            )

            textForFixedFont(font = fixedFontWithRevisedWeight, font.emphases.italic)
        }
        is Font.Fixed -> {
            textForFixedFont(font = font)
        }
    }
}

private val FontWeight.composeValue: androidx.compose.ui.text.font.FontWeight
    get() = when (this) {
        FontWeight.UltraLight -> androidx.compose.ui.text.font.FontWeight.ExtraLight
        FontWeight.Thin -> androidx.compose.ui.text.font.FontWeight.Thin
        FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
        FontWeight.Regular -> androidx.compose.ui.text.font.FontWeight.Normal
        FontWeight.Medium -> androidx.compose.ui.text.font.FontWeight.Medium
        FontWeight.SemiBold -> androidx.compose.ui.text.font.FontWeight.SemiBold
        FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
        FontWeight.Heavy -> androidx.compose.ui.text.font.FontWeight.ExtraBold
        FontWeight.Black -> androidx.compose.ui.text.font.FontWeight.Black
    }

private val TextAlignment.composeValue: TextAlign
    get() {
        return when (this) {
            TextAlignment.CENTER -> TextAlign.Center
            TextAlignment.LEADING -> TextAlign.Left
            TextAlignment.TRAILING -> TextAlign.Right
        }
    }

internal fun textLayerMeasurePolicy(): MeasurePolicy {
    return object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            Trace.beginSection("TextLayerMeasurePolicy::measure")
            val placeables = measurables.map { measurable ->
                // Allows text to overflow vertically when needed (such as inside a small frame).
                measurable.measure(constraints.copy(maxHeight = Constraints.Infinity))
            }

            val l = layout(placeables.maxOf { it.width }, placeables.maxOf { it.height }) {
                placeables.forEach { placeable ->
                    placeable.place(0, 0)
                }
            }
            Trace.endSection()
            return l
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            Trace.beginSection("TextLayerMeasurePolicy::intrinsicMeasure")

            return try {
                mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
                    val textMeasurable =
                        measurables.firstOrNull() ?: return@mapMaxIntrinsicWidthAsMeasure Size(0, 0)
                    // call Jetpack Compose's text intrinsics to get the ultimate size of the text.

                    // Note: if we are proposed a value larger than the max value that can be reprsented by JP Compose's constraints,
                    // that should be because a greatest finite value was involved upstream, as part of flexibility sort.

                    // in that case, we should clamp to max allowable value in JP constraints.
                    // Now, the max value Constraints can represent varies because of focus bits,
                    // an internal implementation detail. The smallest allowable value appears to
                    // 13 bits (see [Constraints.HeightMask] and [Constraints.WidthMask]).
                    val maxConstraintVal = 2 shl 13

                    // calculate width first, since that will determine the height.
                    // For the wrapping behaviour, text will clamp to proposed Width.
                    val textWidth = minOf(
                        proposedWidth,
                        // in case we are proposed greatestFiniteValue, clamp it to maximum safe constraint value.
                        textMeasurable.maxIntrinsicWidth(minOf(proposedHeight, maxConstraintVal))
                    )

                    val textHeight = textMeasurable.maxIntrinsicHeight(textWidth)

                    Size(textWidth, textHeight)
                }
            } finally {
                Trace.endSection()
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            return mapMinIntrinsicAsFlex {
                // so for width, text is infinite flexible.
                IntRange(0, Constraints.Infinity)
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            return mapMinIntrinsicAsFlex {
                // text is not flexible on vertical. it takes up as much space as it needs
                // It is constrained by horizontal dimension, which we don't have available here,
                // so we can't calculate the *actual* size. So we'll return a range of 0,0 to
                // indicate inflexibility.
                IntRange(0, 0)

                // This is a great tradeoff of accuracy for speed & simplicity.

                // Because this range does not actually represent the text's final
                // vertical size, this means that upstream stacks may not score their own
                // flexibility exactly accurately.
                //
                // However, as long the general point is "inflexible", in the vast majority of cases
                // the scoring doesn't need to be that oaccurate, just as long as the general idea
                // of flexible layers vs inflexible layers gets captured enough that stacks even
                // further up can generally rank order their children correctly.
                //
                // Avoiding passing a cross dim (or even a full width/height) proposal to these
                // flex intrinsic methods allows for keeping flex sorting as cheap & simple as
                // possible. Sorting is very hot path. Speed is more important than accuracy.
                //
                // Handling proposal data involves adding a lot of slow complexity to the sorting
                // code path, particularly thorugh the stacks which would now need to run virtually
                // the full 3-pass (1 flex pass and then two measure passes) stack algorithm.
            }
        }

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            throw IllegalStateException("Only call maxIntrinsicWidth, with packed parameter, on Judo measurables.")
        }
    }
}
