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

package app.judo.sdk.ui.extensions

import android.graphics.Typeface
import app.judo.sdk.api.models.Font
import app.judo.sdk.api.models.FontStyle
import app.judo.sdk.api.models.FontWeight

internal fun Font.getSystemFontAttributes(): Font.Fixed {
    return when (this) {
        is Font.Fixed -> this
        is Font.Dynamic -> resolveDynamicFontStyle(this.textStyle)
        is Font.Custom -> Font.Fixed(size = this.size, weight = FontWeight.Regular, isDynamic = isDynamic)
    }
}

internal fun Font.getEmphasisStyle(): Int? {
    return when (this) {
        is Font.Dynamic -> {
            when {
                this.emphases.bold && this.emphases.italic -> Typeface.BOLD_ITALIC
                this.emphases.bold -> Typeface.BOLD
                this.emphases.italic -> Typeface.ITALIC
                else -> null
            }
        }
        else -> null
    }
}

internal data class FrameworkFont(val name: String, val style: Int)

internal fun FontWeight.mapToFont(): FrameworkFont {
    return when (this) {
        FontWeight.UltraLight -> FrameworkFont("sans-serif-thin", Typeface.NORMAL)
        FontWeight.Thin -> FrameworkFont("sans-serif-thin", Typeface.NORMAL)
        FontWeight.Light -> FrameworkFont("sans-serif-light", Typeface.NORMAL)
        FontWeight.Regular -> FrameworkFont("sans-serif", Typeface.NORMAL)
        FontWeight.Medium -> FrameworkFont("sans-serif-medium", Typeface.NORMAL)
        FontWeight.SemiBold -> FrameworkFont("sans-serif-medium", Typeface.NORMAL)
        FontWeight.Bold -> FrameworkFont("sans-serif", Typeface.BOLD)
        FontWeight.Heavy -> FrameworkFont("sans-serif", Typeface.BOLD)
        FontWeight.Black -> FrameworkFont("sans-serif-black", Typeface.NORMAL)
    }
}

private fun resolveDynamicFontStyle(textStyle: String): Font.Fixed {
    val fixedFont = when (FontStyle.getStyleFromCode(textStyle)) {
        FontStyle.LARGE_TITLE -> Font.Fixed(size = 34f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.TITLE_1 -> Font.Fixed(size = 28f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.TITLE_2 -> Font.Fixed(size = 22f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.TITLE_3 -> Font.Fixed(size = 20f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.HEADLINE -> Font.Fixed(size = 17f, weight = FontWeight.SemiBold, isDynamic = true)
        FontStyle.BODY -> Font.Fixed(size = 17f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.CALLOUT -> Font.Fixed(size = 16f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.SUBHEADLINE -> Font.Fixed(size = 15f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.FOOTNOTE -> Font.Fixed(size = 13f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.CAPTION_1 -> Font.Fixed(size = 12f, weight = FontWeight.Regular, isDynamic = true)
        FontStyle.CAPTION_2 -> Font.Fixed(size = 11f, weight = FontWeight.Regular, isDynamic = true)
    }

    return fixedFont
}
