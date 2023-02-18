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

import android.graphics.Point
import android.os.Trace
import android.util.Size
import android.util.SizeF
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.ui.layers.RectangleLayer
import app.judo.sdk.compose.ui.layers.stacks.HStackLayer
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.layout.mapMaxIntrinsicWidthAsMeasure
import app.judo.sdk.compose.ui.utils.ifInfinity
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun FrameModifier(
    frame: Frame?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (frame != null) {
        content(
            modifier
                .judoFrame(frame)
        )
    } else {
        content(modifier)
    }
}

internal fun Modifier.judoFrame(frame: Frame) = this
    .then(JudoFrame(frame))

private class JudoFrame(val frame: Frame) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        data class SizingResult(val placeable: Placeable, val size: Size)

        val (placeable, size) = if (frame.isFixed) {
            val fixedWidth = frame.width?.dp?.roundToPx()
            val fixedHeight = frame.height?.dp?.roundToPx()

            val childConstraints = Constraints(
                maxWidth = fixedWidth ?: constraints.maxWidth,
                maxHeight = fixedHeight ?: constraints.maxHeight
            )
            val placeable = measurable.measure(childConstraints)
            val width = frame.width?.dp?.roundToPx() ?: placeable.width
            val height = frame.height?.dp?.roundToPx() ?: placeable.height

            SizingResult(placeable, Size(width, height))
        } else {
            // From objc.io:
            //  A flexible frame clamps the proposed size between two values. If both a minimum and
            //  maximum width are set and the proposed width is between those values, the flexible frame
            //  becomes as wide as proposed, ignoring its content view's size. And if we define only a
            //  minimum or only a maximum width, the content's width is used as the missing constraint.

            // process in points/dp rather than pixels:
            var proposed = ProposedSize(
                if (constraints.maxWidth == Constraints.Infinity) Float.POSITIVE_INFINITY else constraints.maxWidth.toDp().value,
                if (constraints.maxHeight == Constraints.Infinity) Float.POSITIVE_INFINITY else constraints.maxHeight.toDp().value
            )

            frame.minWidth?.let { minWidth ->
                if (minWidth > proposed.width || constraints.maxWidth == Constraints.Infinity) {
                    proposed = ProposedSize(minWidth, proposed.height)
                }
            }
            frame.maxWidth?.floatValue?.let { maxWidth ->
                if (maxWidth < proposed.width) {
                    proposed = ProposedSize(maxWidth, proposed.height)
                }
            }

            frame.minHeight?.let { minHeight ->
                if (minHeight > proposed.height || constraints.maxHeight == Constraints.Infinity) {
                    proposed = ProposedSize(proposed.width, minHeight)
                }
            }

            frame.maxHeight?.floatValue?.let { maxHeight ->
                if (maxHeight < proposed.height) {
                    proposed = ProposedSize(proposed.width, maxHeight)
                }
            }

            val placeable = measurable.measure(
                Constraints(
                    maxWidth = proposed.width.dp.roundToPx(),
                    maxHeight = proposed.height.dp.roundToPx()
                )
            )

            var result = SizeF(placeable.width.toDp().value, placeable.height.toDp().value)
            frame.minWidth?.let { minWidth ->
                result = SizeF(
                    max(minWidth, min(result.width, proposed.width)),
                    result.height
                )
            }
            frame.maxWidth?.floatValue?.let { maxWidth ->
                result = SizeF(
                    min(maxWidth, max(result.width, proposed.width.ifInfinity { 0f })),
                    result.height
                )
            }

            frame.minHeight?.let { minHeight ->
                result = SizeF(
                    result.width,
                    max(minHeight, min(result.height, proposed.height))

                )
            }
            frame.maxHeight?.floatValue?.let { maxHeight ->
                result = SizeF(
                    result.width,
                    min(maxHeight, max(result.height, proposed.height.ifInfinity { 0f }))

                )
            }

            SizingResult(placeable, Size(result.width.dp.roundToPx(), result.height.dp.roundToPx()))
        }

        // if we ended up with an infinity, then fall back to the placeable's measured size since
        // that appears to be the observed SwiftUI behaviour.
        // TODO
        val width = size.width.ifInfinity {
            placeable.width
        }
        val height = size.height.ifInfinity {
            placeable.height
        }

        return layout(width, height) {
            val centerWidth = { maxOf(width / 2 - placeable.width / 2, 0) }
            val centerHeight = { maxOf(height / 2 - placeable.height / 2, 0) }
            val right = { width - placeable.width }
            val bottom = { height - placeable.height }

            val position: Point = when (frame.alignment) {
                Alignment.TOP -> Point(centerWidth(), 0)
                Alignment.BOTTOM -> Point(centerWidth(), bottom())
                Alignment.LEADING -> Point(0, centerHeight())
                Alignment.TRAILING -> Point(right(), centerHeight())
                Alignment.TOP_LEADING -> Point(0, 0)
                Alignment.TOP_TRAILING -> Point(right(), 0)
                Alignment.BOTTOM_LEADING -> Point(0, bottom())
                Alignment.BOTTOM_TRAILING -> Point(right(), bottom())
                else -> Point(
                    centerWidth(),
                    centerHeight()
                )
            }

            placeable.placeRelative(position.x, position.y)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
            data class SizingResult(val childSize: Size, val size: Size)

            val (childSize, size) = if (frame.isFixed) {
                val fixedWidth = frame.width?.dp?.roundToPx()
                val fixedHeight = frame.height?.dp?.roundToPx()

                val childSize = measurable.judoMeasure(
                    Size(
                        fixedWidth ?: proposedWidth,
                        fixedHeight ?: proposedHeight
                    )
                )
                val width = frame.width?.dp?.roundToPx() ?: childSize.width
                val height = frame.height?.dp?.roundToPx() ?: childSize.height

                SizingResult(childSize, Size(width, height))
            } else {
                // From objc.io:
                //  A flexible frame clamps the proposed size between two values. If both a minimum and
                //  maximum width are set and the proposed width is between those values, the flexible frame
                //  becomes as wide as proposed, ignoring its content view's size. And if we define only a
                //  minimum or only a maximum width, the content's width is used as the missing constraint.

                // process in points/dp rather than pixels:
                var proposed = ProposedSize(
                    if (proposedWidth == Constraints.Infinity) Float.POSITIVE_INFINITY else proposedWidth.toDp().value,
                    if (proposedHeight == Constraints.Infinity) Float.POSITIVE_INFINITY else proposedHeight.toDp().value
                )

                frame.minWidth?.let { minWidth ->
                    if (minWidth > proposed.width || proposedWidth == Constraints.Infinity) {
                        proposed = ProposedSize(minWidth, proposed.height)
                    }
                }
                frame.maxWidth?.floatValue?.let { maxWidth ->
                    if (maxWidth < proposed.width) {
                        proposed = ProposedSize(maxWidth, proposed.height)
                    }
                }

                frame.minHeight?.let { minHeight ->
                    if (minHeight > proposed.height || proposedHeight == Constraints.Infinity) {
                        proposed = ProposedSize(proposed.width, minHeight)
                    }
                }

                frame.maxHeight?.floatValue?.let { maxHeight ->
                    if (maxHeight < proposed.height) {
                        proposed = ProposedSize(proposed.width, maxHeight)
                    }
                }

                val childSize = measurable.judoMeasure(
                    Size(
                        proposed.width.dp.roundToPx(),
                        proposed.height.dp.roundToPx()
                    )
                )

                var result = SizeF(childSize.width.toDp().value, childSize.height.toDp().value)
                frame.minWidth?.let { minWidth ->
                    result = SizeF(
                        max(minWidth, min(result.width, proposed.width)),
                        result.height
                    )
                }
                frame.maxWidth?.floatValue?.let { maxWidth ->
                    result = SizeF(
                        min(maxWidth, max(result.width, proposed.width.ifInfinity { 0f })),
                        result.height
                    )
                }

                frame.minHeight?.let { minHeight ->
                    result = SizeF(
                        result.width,
                        max(minHeight, min(result.height, proposed.height))
                    )
                }
                frame.maxHeight?.floatValue?.let { maxHeight ->
                    result = SizeF(
                        result.width,
                        min(maxHeight, max(result.height, proposed.height.ifInfinity { 0f }))
                    )
                }

                SizingResult(childSize, Size(result.width.dp.roundToPx(), result.height.dp.roundToPx()))
            }

            // if we ended up with an infinity, then fall back to the placeable's measured size since
            // that appears to be the observed SwiftUI behaviour.
            val width = size.width.ifInfinity {
                childSize.width
            }
            val height = size.height.ifInfinity {
                childSize.height
            }

            Size(width, height)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        Trace.beginSection("FrameModifier::intrinsicMeasure::horizontalFlex")
        return try {
            mapMinIntrinsicAsFlex {
                val (upper: Int, lower: Int) = if (frame.isFixed) {
                    val childFlex = if (frame.width != null) {
                        measurable.judoHorizontalFlex()
                    } else { null }
                    Pair(
                        frame.width?.dp?.roundToPx() ?: childFlex?.first ?: 0,
                        frame.width?.dp?.roundToPx() ?: childFlex?.last ?: 0
                    )
                } else {
                    // so for flex frame we are granted min or max values for each dim.
                    // so I guess just return the min max values. if a min or max isn't provided,
                    // return the range of the child.

                    val childFlex = if (frame.minWidth != null || frame.maxWidth != null) {
                        measurable.judoHorizontalFlex()
                    } else { null }

                    Pair(
                        frame.minWidth?.dp?.roundToPx() ?: childFlex?.first ?: 0,
                        frame.maxWidth?.floatValue?.dp?.roundToPx() ?: childFlex?.last ?: 0
                    )
                }

                IntRange(upper, lower)
            }
        } finally {
            Trace.endSection()
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        Trace.beginSection("FrameModifier::intrinsicMeasure::verticalFlex")
        return try {
            mapMinIntrinsicAsFlex {
                val (upper: Int, lower: Int) = if (frame.isFixed) {
                    val childFlex = if (frame.height != null) {
                        measurable.judoHorizontalFlex()
                    } else { null }
                    Pair(
                        frame.height?.dp?.roundToPx() ?: childFlex?.first ?: 0,
                        frame.height?.dp?.roundToPx() ?: childFlex?.last ?: 0
                    )
                } else {
                    // so for flex frame we are granted min or max values for each dim.
                    // so I guess just return the min max values. if a min or max isn't provided,
                    // return the range of the child.

                    val childFlex = if (frame.minHeight != null || frame.maxHeight != null) {
                        measurable.judoVerticalFlex()
                    } else { null }

                    Pair(
                        frame.minHeight?.dp?.roundToPx() ?: childFlex?.first ?: 0,
                        frame.maxHeight?.floatValue?.dp?.roundToPx() ?: childFlex?.last ?: 0
                    )
                }

                IntRange(upper, lower)
            }
        } finally {
            Trace.endSection()
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        throw IllegalStateException("Only call maxIntrinsicWidth, with packed parameter, on Judo measurables.")
    }
}

@Composable
private fun TestBox(
    modifier: Modifier = Modifier,
    size: Dp = 25.dp
) {
    Box(
        modifier = modifier
            .background(Color.Red)
            .requiredSize(size)
    )
}

@Composable
private fun LabelledPreview(
    name: String,
    contents: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.padding(20.dp)
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(name, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.zIndex(10f))
            Box(
                modifier = Modifier.border(2.dp, color = Color.Magenta)
            ) {
                contents()
            }
        }
    }
}

@Preview
@Composable
private fun NoOpFrame() {
    LabelledPreview("No-op Frame") {
        TestBox(
            modifier = Modifier.judoFrame(Frame(alignment = Alignment.CENTER))
        )
    }
}

@Preview
@Composable
private fun FixedHeightFrame() {
    LabelledPreview("Fixed Height Frame") {
        TestBox(
            modifier = androidx.compose.ui.Modifier
                .judoFrame(
                    app.judo.sdk.compose.model.values.Frame(
                        height = 100f,
                        alignment = app.judo.sdk.compose.model.values.Alignment.BOTTOM
                    )
                )
        )
    }
}

@Preview
@Composable
private fun FixedFrameExpandingContent() {
    LabelledPreview("Fixed Frame Expanding Content") {
        RectangleLayer(
            fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
            modifier = Modifier.judoFrame(
                app.judo.sdk.compose.model.values.Frame(
                    width = 100f,
                    height = 100f,
                    alignment = app.judo.sdk.compose.model.values.Alignment.CENTER
                )
            )
        )
    }
}

@Preview
@Composable
private fun FixedSizeBottomTrailing() {
    LabelledPreview(name = "Fixed Size Bottom Trailing") {
        TestBox(
            modifier = Modifier
                .judoFrame(Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_TRAILING))
        )
    }
}

@Preview
@Composable
private fun FlexibleMaxHeightFinite() {
    LabelledPreview("Flexible Max Height - Finite") {
        TestBox(
            modifier = Modifier.judoFrame(Frame(maxHeight = MaxHeight.Finite(48f), alignment = Alignment.CENTER))
        )
    }
}

@Preview
@Composable
private fun FlexibleMinHeight() {
    LabelledPreview(name = "Flexible Min Height") {
        TestBox(
            modifier = Modifier.judoFrame(Frame(minHeight = 48f, alignment = Alignment.TRAILING))
        )
    }
}

@Preview
@Composable
private fun FlexibleMinMaxFiniteHeight() {
    LabelledPreview("Flexible Frame with Min and Finite Max Height") {
        TestBox(
            modifier = Modifier.judoFrame(
                Frame(
                    minHeight = 48f,
                    maxHeight = MaxHeight.Finite(100f),
                    alignment = Alignment.TRAILING
                )
            )
        )
    }
}

@Preview
@Composable
private fun FlexibleMaxInfiniteHeight() {
    LabelledPreview("Flexible Frame with Infinite Max Height") {
        TestBox(
            modifier = Modifier.judoFrame(
                Frame(
                    maxHeight = MaxHeight.Infinite(),
                    alignment = Alignment.TRAILING
                )
            )
        )
    }
}

@Preview
@Composable
private fun FlexibleMaxInfiniteWidth() {
    LabelledPreview("Flexible Frame with Infinite Max Width") {
        TestBox(
            modifier = Modifier.judoFrame(
                Frame(
                    maxWidth = MaxWidth.Infinite(),
                    alignment = Alignment.TRAILING
                )
            )
        )
    }
}

@Preview
@Composable
private fun FlexibleMaxHeightWithLargerChild() {
    LabelledPreview(name = "Flexible Finite Max Height with Larger Child") {
        TestBox(
            size = 100.dp,
            modifier = Modifier.judoFrame(Frame(maxHeight = MaxHeight.Finite(48f), alignment = Alignment.CENTER))
        )
    }
}

@Preview
@Composable
private fun NestingFrames() {
    LabelledPreview(name = "Nested Frames") {
        Box(
            modifier = Modifier
                .judoFrame(Frame(width = 100f, height = 200f, alignment = Alignment.BOTTOM_TRAILING))
                .clip(CircleShape)
                .background(Color.Red)
                .judoFrame(Frame(width = 70f, height = 70f, alignment = Alignment.CENTER))
        )
    }
}

@Preview
@Composable
private fun FixedWithLargerChild() {
    // TODO: this one is a known broken case.
    LabelledPreview(name = "Fixed Frame with Larger Child Aligned Trailing (Known Broken - Alignment Wrong)") {
        Box(
            modifier = Modifier
                .judoFrame(Frame(width = 150f, height = 200f, alignment = Alignment.TRAILING))
                .clip(CircleShape)
                .background(Color.Blue)
                .requiredSize(200.dp)
        )
    }
}

@Preview
@Composable
private fun IntegrationTestFlexFrameAndHStack() {
    LabelledPreview(name = "Integration Test: Frame inside HStack") {
        HStackLayer(alignment = Alignment.CENTER) {
            TestBox()
            TestBox(modifier = Modifier.judoFrame(Frame(maxWidth = MaxWidth.Finite(100f), alignment = Alignment.TRAILING)))
        }
    }
}

@Preview
@Composable
private fun IntegrationTestInfiniteFlexFrameAndHStack() {
    LabelledPreview(name = "Integration Test: Infinite Frame + HStack (Known Broken - Presumed bug in Stack with Infinite children") {
        HStackLayer(alignment = Alignment.CENTER) {
            TestBox()
            TestBox(modifier = Modifier.judoFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.TRAILING)))
        }
    }
}

/**
 * Created in lieu of [SizeF] to allow infinity values.
 */
internal data class ProposedSize(
    val width: Float,
    val height: Float
)
