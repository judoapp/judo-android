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

package app.judo.sdk.compose.ui.modifiers

import android.util.Log
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.model.values.Fill
import app.judo.sdk.compose.ui.layers.RectangleLayer
import app.judo.sdk.compose.ui.layers.stacks.HStackLayer
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.layout.mapMaxIntrinsicWidthAsMeasure
import kotlin.math.roundToInt

@Composable
internal fun AspectRatioModifier(
    aspectRatio: Float?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (aspectRatio != null) {
        content(
            modifier
                .then(AspectRatioLayoutModifier(aspectRatio))
        )
    } else {
        content(modifier)
    }
}

private class AspectRatioLayoutModifier(val aspectRatio: Float) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val aspectRatio = if (aspectRatio == 0f) 1f else aspectRatio
        // Note: ratios greater than 1 are wide, less than 1 are tall. width / height.

        // Imagine a (aspectRatio, 1) dimension rectangle that we are scaling up to fit.

        val widthFraction = if (constraints.maxWidth == Constraints.Infinity) {
            Float.POSITIVE_INFINITY
        } else {
            constraints.maxWidth / aspectRatio
        }

        val heightFraction = if (constraints.maxHeight == Constraints.Infinity) {
            Float.POSITIVE_INFINITY
        } else {
            constraints.maxHeight.toFloat() // divided by 1f, the height dimension of that rectangle
        }

        // thus we want the minimum of these values for "fit" behaviour.
        val fraction = minOf(widthFraction, heightFraction)

        if (fraction == Float.POSITIVE_INFINITY) {
            // if both constraint dimensions are infinity (will not happen in normal operation),
            // there would be no constraint and our image size would be infinity. Illegal state.
            Log.e("AspectRatioModifier", "Illegally getting infinity constraints in both dimensions")
            return layout(0, 0) {
            }
        }

        val placeable = measurable.measure(
            Constraints(
                maxWidth = (fraction * aspectRatio).roundToInt(),
                maxHeight = fraction.roundToInt()
            )
        )

        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
            val aspectRatio = if (aspectRatio == 0f) 1f else aspectRatio
            // Note: ratios greater than 1 are wide, less than 1 are tall. width / height.

            // Imagine a (aspectRatio, 1) dimension rectangle that we are scaling up to fit.

            val widthFraction = if (proposedWidth == Constraints.Infinity) {
                Float.POSITIVE_INFINITY
            } else {
                proposedWidth / aspectRatio
            }

            val heightFraction = if (proposedHeight == Constraints.Infinity) {
                Float.POSITIVE_INFINITY
            } else {
                proposedHeight.toFloat() // divided by 1f, the height dimension of that rectangle
            }

            // thus we want the minimum of these values for "fit" behaviour.
            val fraction = minOf(widthFraction, heightFraction)

            if (fraction == Float.POSITIVE_INFINITY) {
                // if both constraint dimensions are infinity (will not happen in normal operation),
                // there would be no constraint and our image size would be infinity. Illegal state.
                Log.e("AspectRatioModifier", "Illegally getting infinity constraints in both dimensions")
                return@mapMaxIntrinsicWidthAsMeasure Size(0, 0)
            }

            val childSize = measurable.judoMeasure(
                Size(
                    (fraction * aspectRatio).roundToInt(),
                    fraction.roundToInt()
                )
            )

            childSize
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            if (aspectRatio < 1) {
                // tall. inflexible
                IntRange(0, 0)
            } else {
                // wide. flexible.
                IntRange(0, Constraints.Infinity)
            }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            if (aspectRatio < 1) {
                // tall. flexible
                IntRange(0, Constraints.Infinity)
            } else {
                // wide. inflexible.
                IntRange(0, 0)
            }
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        throw IllegalStateException("Only call maxIntrinsicWidth, with packed parameter, on Judo measurables.")
    }
}

@Preview
@Composable
fun SquareRectangle() {
    RectangleLayer(
        fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
        modifier = Modifier.then(AspectRatioLayoutModifier(1.0f))
    )
}

@Preview
@Composable
fun HorizontalRectangle() {
    RectangleLayer(
        fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
        modifier = Modifier.then(AspectRatioLayoutModifier(1.5f))
    )
}

@Preview
@Composable
fun VerticalRectangle() {
    RectangleLayer(
        fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
        modifier = Modifier.then(AspectRatioLayoutModifier(0.5f))
    )
}

@Preview
@Composable
fun IntegrationInsideStack() {
    // Validate the intrinsics are working well by embedding in a stack.
    HStackLayer {
        RectangleLayer(
            fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
            modifier = Modifier.then(AspectRatioLayoutModifier(1.0f))
        )
    }
}
