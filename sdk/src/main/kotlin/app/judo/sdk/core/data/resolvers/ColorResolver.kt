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

package app.judo.sdk.core.data.resolvers

import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import app.judo.sdk.api.models.Appearance
import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.ui.extensions.convertTo
import app.judo.sdk.ui.extensions.isDarkMode
import kotlin.math.roundToInt

internal class ColorResolver(private val context: Context, private val appearance: Appearance) {

    @ColorInt
    fun resolveForColorInt(color: ColorVariants): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resolveConvertedSRGBColor(color)
        } else {
            resolveSRGBColor(color)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @ColorInt
    private fun resolveConvertedSRGBColor(color: ColorVariants): Int {
        val darkMode = context.isDarkMode(appearance)

        val colorForEnvironment = when {
            darkMode -> color.darkMode ?: color.darkModeHighContrast ?: color.default
            else -> color.default
        }

        val colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
        return Color.valueOf(Color.valueOf(
            colorForEnvironment.red,
            colorForEnvironment.green,
            colorForEnvironment.blue,
            colorForEnvironment.alpha,
            colorSpace
        ).pack().convertTo(ColorSpace.Named.SRGB)).toArgb()
    }

    @ColorInt
    private fun resolveSRGBColor(color: ColorVariants): Int {
        val darkMode = context.isDarkMode(appearance)

        val colorForEnvironment = when {
            darkMode -> color.darkMode ?: color.darkModeHighContrast ?: color.default
            else -> color.default
        }

        return Color.argb(
            (colorForEnvironment.alpha * 255).roundToInt(),
            (colorForEnvironment.red * 255).roundToInt(),
            (colorForEnvironment.green * 255).roundToInt(),
            (colorForEnvironment.blue * 255).roundToInt()
        )
    }
}
