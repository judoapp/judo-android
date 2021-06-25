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

package app.judo.sdk.core.data

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import app.judo.sdk.api.models.FontStyle
import app.judo.sdk.core.utils.FontAttributes
import kotlin.math.roundToInt

internal class SkeletonMapper {
    fun resolveTextSkeleton(fontAttributes: FontAttributes, textColor: Int): TextSkeleton {
        val colorAlpha = Color.alpha(textColor)
        val skeletonColor = ColorUtils.setAlphaComponent(textColor, (colorAlpha.toFloat() * 0.08f).roundToInt())

        return when (fontAttributes) {
            is FontAttributes.Fixed -> {
                val cornerRadius = when {
                    fontAttributes.size > 22f -> 5f
                    fontAttributes.size > 14f -> 3f
                    else -> 2f
                }

                val height = fontAttributes.size * 0.7f

                TextSkeleton(height, cornerRadius, skeletonColor)
            }
            is FontAttributes.Dynamic -> {
                when (fontAttributes.fontStyle) {
                    FontStyle.LARGE_TITLE.code -> TextSkeleton(24f, 5f, skeletonColor)
                    FontStyle.TITLE_1.code -> TextSkeleton(20f, 3f, skeletonColor)
                    FontStyle.TITLE_2.code -> TextSkeleton(15.5f, 3f, skeletonColor)
                    FontStyle.TITLE_3.code -> TextSkeleton(14f, 2f, skeletonColor)
                    FontStyle.HEADLINE.code -> TextSkeleton(12f, 2f, skeletonColor)
                    FontStyle.BODY.code -> TextSkeleton(12f, 2f, skeletonColor)
                    FontStyle.CALLOUT.code -> TextSkeleton(11.5f, 2f, skeletonColor)
                    FontStyle.SUBHEADLINE.code -> TextSkeleton(10.5f, 2f, skeletonColor)
                    FontStyle.FOOTNOTE.code -> TextSkeleton(9f, 2f, skeletonColor)
                    FontStyle.CAPTION_1.code -> TextSkeleton(8.5f, 2f, skeletonColor)
                    FontStyle.CAPTION_2.code -> TextSkeleton(8f, 2f, skeletonColor)
                    else -> TODO("account for other font styles")
                }
            }
        }
    }
}

internal data class TextSkeleton(val height: Float, val cornerRadius: Float, val color: Int)