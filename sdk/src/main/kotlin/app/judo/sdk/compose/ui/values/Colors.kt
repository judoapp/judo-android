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

package app.judo.sdk.compose.ui.values

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.model.values.ColorValue
import app.judo.sdk.compose.ui.Environment

fun getRandomColor(): Color {
    return Color(255, (0..150).random(), (0..150).random(), (25..255).random())
}

fun getRandomColor(numberOfChildren: Int): Color {
    val upperBound = 255 / maxOf(numberOfChildren, 1)
    val alphaUpperBound = 100 / maxOf(numberOfChildren, 1)

    return Color(255, (0..upperBound).random(), (0..upperBound).random(), (alphaUpperBound / 2..alphaUpperBound).random())
}

@Composable
internal fun ColorReference.getComposeColor(): Color = getComposeColor(Environment.LocalIsDarkTheme.current)

internal fun ColorReference.getComposeColor(
    isDarkMode: Boolean
): Color {
    return when (this) {
        is ColorReference.CustomColor -> {
            this.customColor.composeColor
        }
        is ColorReference.DocumentColor -> {
            if (isDarkMode) {
                this.documentColor?.darkMode?.composeColor ?: this.documentColor?.default?.composeColor ?: Color.Transparent
            } else {
                this.documentColor?.default?.composeColor ?: Color.Transparent
            }
        }
        is ColorReference.SystemColor -> {
            val systemColor = SystemColors[this.colorName] ?: return Color.Transparent
            if (isDarkMode) {
                systemColor.dark.composeColor
            } else {
                systemColor.universal.composeColor
            }
        }
    }
}

internal val ColorValue.composeColor: Color
    get() = Color(
        red = red, green = green, blue = blue, alpha = alpha
    )
