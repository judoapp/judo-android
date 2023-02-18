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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import app.judo.sdk.compose.model.values.GradientReference
import app.judo.sdk.compose.model.values.GradientValue
import app.judo.sdk.compose.ui.Environment

@Composable
internal fun GradientReference.getComposeBrush(
    size: Size
): Brush = getComposeBrush(Environment.LocalIsDarkTheme.current, size)

internal fun GradientReference.getComposeBrush(
    isDarkMode: Boolean,
    size: Size
): Brush {
    return when (this) {
        is GradientReference.CustomGradient -> {
            customGradient.composeBrush(size)
        }

        is GradientReference.DocumentGradient -> {
            if (isDarkMode) {
                documentGradient?.darkMode?.composeBrush(size) ?: SolidColor(Color.Transparent)
            } else {
                documentGradient?.default?.composeBrush(size) ?: SolidColor(Color.Transparent)
            }
        }
    }
}

private fun toOffset(coord: List<Float>, size: Size): Offset {
    if (coord.count() < 2) {
        return Offset(0f, 0f)
    }

    val (x, y) = coord
    return Offset(x * size.width, y * size.height)
}

private fun GradientValue.composeBrush(size: Size): Brush {
    val colorStops = stops.map { stop ->
        Pair(stop.position, stop.color.composeColor)
    }.toTypedArray()

    val start = toOffset(from, size)
    val end = toOffset(to, size)

    return Brush.linearGradient(colorStops = colorStops, start, end)
}
