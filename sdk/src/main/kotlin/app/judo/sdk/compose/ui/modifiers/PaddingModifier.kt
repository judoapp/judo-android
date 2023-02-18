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

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.ui.layers.RectangleLayer
import app.judo.sdk.compose.ui.layers.TextLayer
import app.judo.sdk.compose.ui.layers.stacks.VStackLayer
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.layout.judoMeasure
import app.judo.sdk.compose.ui.layout.mapMaxIntrinsicWidthAsMeasure
import app.judo.sdk.compose.ui.utils.unlessInfinity

@Composable
internal fun PaddingModifier(
    padding: Padding?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (padding != null) {
        content(
            modifier
                .judoPadding(padding)
        )
    } else {
        content(modifier)
    }
}

private fun Modifier.judoPadding(padding: Padding) = this
    .then(JudoPadding(padding))

private class JudoPadding(val padding: Padding) : LayoutModifier {
    val horizontalPaddingValues get() = (padding.leading + padding.trailing).dp
    val verticalPaddingValues get() = (padding.top + padding.bottom).dp

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(
            Constraints(
                maxWidth = maxOf(constraints.maxWidth.unlessInfinity { it - horizontalPaddingValues.roundToPx() }, 0),

                maxHeight = maxOf(constraints.maxHeight.unlessInfinity { it - verticalPaddingValues.roundToPx() }, 0),

            )
        )

        return layout(
            // TODO: hey minOf() to constraints seems totes wrong here? that looks like it would make the padded layer
            // shrink to max size allowed by constraints, but that is wrong. fix this later.
            width = minOf(constraints.maxWidth, placeable.width + horizontalPaddingValues.roundToPx()),
            height = minOf(constraints.maxHeight, placeable.height + verticalPaddingValues.roundToPx())
        ) {
            placeable.placeRelative(padding.leading.dp.roundToPx(), padding.top.dp.roundToPx())
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
            val childSize = measurable.judoMeasure(
                Size(
                    maxOf(proposedWidth.unlessInfinity { it - horizontalPaddingValues.roundToPx() }, 0),
                    maxOf(proposedHeight.unlessInfinity { it - verticalPaddingValues.roundToPx() }, 0)
                )
            )

            Size(
                childSize.width + horizontalPaddingValues.roundToPx(),
                childSize.height + verticalPaddingValues.roundToPx()
            )
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // basically padding should be added to minimum and maximum flex. be careful of infinities when adding to maximum.
            val childFlex = measurable.judoHorizontalFlex()

            val lower = childFlex.first + horizontalPaddingValues.roundToPx()
            val upper = childFlex.last.unlessInfinity { it + horizontalPaddingValues.roundToPx() }

            IntRange(lower, upper)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // basically padding should be added to minimum and maximum flex. be careful of infinities when adding to maximum.
            val childFlex = measurable.judoVerticalFlex()

            val lower = childFlex.first + verticalPaddingValues.roundToPx()
            val upper = childFlex.last.unlessInfinity { it + verticalPaddingValues.roundToPx() }

            IntRange(lower, upper)
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
private fun TestWrapsLayer() {
    // test that the padding wraps the layer as expected (if unconstrained)
    PaddingModifier(padding = Padding(20f), modifier = Modifier) { modifier ->
        TextLayer(text = "Judo Rocks", modifier = modifier)
    }
}

@Preview
@Composable
private fun TestFitConstraints() {
    // this should show a 20x20 layer with no blue visible because all you can see
    // is the padding. Remember padding is applied before frame.
    PaddingModifier(padding = Padding(20f), modifier = Modifier.requiredSize(20.dp)) { modifier ->
        Box(
            modifier = modifier.background(Color.Blue)
        )
    }
}

// the same as the above, but using Frame modifier instead of requiredSize().
@Preview
@Composable
private fun IntegrationTestFitFrameConstraints() {
    RectangleLayer(
        fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
        judoModifiers = JudoModifiers(
            padding = Padding(20f),
            frame = Frame(width = 20f, height = 20f, alignment = Alignment.CENTER)
        )
    )
}

@Preview
@Composable
private fun PaddingInStack() {
    VStackLayer(
        spacing = 0f
    ) {
        TextLayer(
            text = "Judo rocks",
            judoModifiers = JudoModifiers(
                padding = Padding(
                    20f
                )
            )
        )
    }
}
