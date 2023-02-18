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

package app.judo.sdk.compose.ui.layers.stacks

import android.os.Trace
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.nodes.VStack
import app.judo.sdk.compose.model.values.Alignment
import app.judo.sdk.compose.model.values.Axis
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.model.values.Fill
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.layers.*
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.utils.*
import app.judo.sdk.compose.ui.utils.groupByPriority
import app.judo.sdk.compose.ui.utils.ifInfinity
import app.judo.sdk.compose.ui.utils.preview.InfiniteHeightMeasurePolicy
import app.judo.sdk.compose.ui.utils.sortByVerticalFlexibility

@Composable
internal fun VStackLayer(node: VStack, modifier: Modifier = Modifier) {
    VStackLayer(modifier, node.spacing, node.alignment, JudoModifiers(node)) {
        Children(children = node.children)
    }
}

@Composable
internal fun VStackLayer(
    modifier: Modifier = Modifier,
    spacing: Float = 10f,
    alignment: Alignment = Alignment.CENTER,
    judoModifiers: JudoModifiers = JudoModifiers(),
    content: @Composable () -> Unit
) {
    val localDensityContext = LocalDensity.current

    val spacingAsPx = with(localDensityContext) {
        spacing.dp.roundToPx()
    }

    LayerBox(judoModifiers = judoModifiers, modifier = modifier) {
        CompositionLocalProvider(Environment.LocalStackAxis provides Axis.VERTICAL) {
            Layout(content, measurePolicy = vStackMeasurePolicy(spacingAsPx, alignment))
        }
    }
}

/**
 * Measure policy for the VStackLayer.
 * It overrides the intrinsic height AND width functions to remove the default behavior and better handle
 * infinity in children widths.
 *
 * For the height intrinsics, we always need to account for the spacing between children.
 * This is especially true in the layout function as otherwise the children will be bigger than the
 * VStack layout and go over its borders.
 *
 * The flexibility sorting is quite simple, ranking children by the range of heights they can be while
 * still being correctly drawn. The bigger this range, the more flexible a child is. For rectangles
 * without frames, for example, the range is between 0 and [Constraints.Infinity], always being one
 * of the most flexible layers.
 */
internal fun vStackMeasurePolicy(spacingAsPx: Int, alignment: Alignment): MeasurePolicy {
    val tag = "VStackMeasurePolicy"
    return object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            if (measurables.isEmpty()) {
                return layout(0, 0) { }
            }

            Trace.beginSection("VStackLayer::measure")

            var remainingHeight = if (constraints.maxHeight == Constraints.Infinity) Constraints.Infinity else constraints.maxHeight - maxOf((spacingAsPx * (measurables.count() - 1)), 0)
            val proposedHeights = hashMapOf<Measurable, Int>()

            val measurablesGroupedByPriority = measurables.groupByPriority()
            val highestPriority = measurablesGroupedByPriority.keys.firstOrNull()
            var largestObservedNonInfinityWidth: Int? = null

            measurablesGroupedByPriority.forEach { (priority, groupedMeasurables) ->
                val sortedByFlexibility = groupedMeasurables.sortByVerticalFlexibility(
                    constraints.maxWidth
                )

                sortedByFlexibility.forEachIndexed { index, measurable ->
                    measurable.annotateIntrinsicsCrash {
                        if (remainingHeight == Constraints.Infinity) {
                            proposedHeights[measurable] = Constraints.Infinity
                        } else {
                            val remainingChildren = sortedByFlexibility.size - index
                            val proposedSize = maxOf(
                                remainingHeight / remainingChildren,
                                0
                            )

                            val childSize = measurable.judoMeasure(
                                Size(
                                    constraints.maxWidth,
                                    proposedSize
                                )
                            )

                            if (priority == highestPriority && childSize.width != Constraints.Infinity) {
                                largestObservedNonInfinityWidth = maxOf(largestObservedNonInfinityWidth ?: Int.MIN_VALUE, childSize.width)
                            }

                            remainingHeight -= childSize.height
                            proposedHeights[measurable] = childSize.height
                        }
                    }
                }
            }

            // fallback behaviour on the cross dimension
            val maxWidthConstraint = constraints.maxWidth.ifInfinity {
                largestObservedNonInfinityWidth ?: Constraints.Infinity
            }

            val placeables = measurables.map { measurable ->
                measurable.measure(
                    constraints.copy(
                        maxWidth = maxWidthConstraint,
                        maxHeight = proposedHeights[measurable] ?: 0
                    )
                )
            }

            placeables.assertNoInfiniteSizes("VStack")

            val maxWidth = placeables.maxOf { it.width }
            val height = placeables.sumOfWithLayoutSpacing(spacingAsPx) { it.height }
            val l = layout(maxWidth, height) {
                var yPosition = 0

                placeables.forEach { placeable ->
                    val xPosition = when (alignment) {
                        Alignment.LEADING -> 0
                        Alignment.TRAILING -> maxWidth - placeable.width
                        // The default Alignment for VStack is CENTER.
                        else -> maxOf(maxWidth / 2 - placeable.width / 2, 0)
                    }

                    placeable.placeRelative(x = xPosition, y = yPosition)
                    yPosition += placeable.height + spacingAsPx
                }
            }
            Trace.endSection()
            return l
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            Trace.beginSection("VStackLayer::instrinsicMeasure")

            try {
                return mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
                    if (measurables.isEmpty()) {
                        return@mapMaxIntrinsicWidthAsMeasure Size(0, 0)
                    }

                    var remainingHeight = if (proposedHeight == Constraints.Infinity) Constraints.Infinity else proposedHeight - maxOf((spacingAsPx * (measurables.count() - 1)), 0)
                    val proposedHeights = hashMapOf<IntrinsicMeasurable, Int>()

                    val measurablesGroupedByPriority = measurables.groupByPriority()
                    val highestPriority = measurablesGroupedByPriority.keys.firstOrNull()
                    var largestObservedNonInfinityWidth: Int? = null

                    measurablesGroupedByPriority.forEach { (priority, groupedMeasurables) ->
                        val sortedByFlexibility = groupedMeasurables.sortByVerticalFlexibility(
                            proposedWidth
                        )

                        sortedByFlexibility.forEachIndexed { index, measurable ->
                            measurable.annotateIntrinsicsCrash {
                                if (remainingHeight == Constraints.Infinity) {
                                    proposedHeights[measurable] = Constraints.Infinity
                                } else {
                                    val remainingChildren = sortedByFlexibility.size - index
                                    val proposedSize = maxOf(
                                        remainingHeight / remainingChildren,
                                        0
                                    )

                                    val childSize = measurable.judoMeasure(
                                        Size(
                                            proposedWidth,
                                            proposedSize
                                        )
                                    )

                                    if (priority == highestPriority && childSize.width != Constraints.Infinity) {
                                        largestObservedNonInfinityWidth = maxOf(largestObservedNonInfinityWidth ?: Int.MIN_VALUE, childSize.width)
                                    }

                                    remainingHeight -= childSize.height
                                    proposedHeights[measurable] = childSize.height
                                }
                            }
                        }
                    }

                    // fallback behaviour on the cross dimension
                    val maxWidthConstraint = proposedWidth.ifInfinity {
                        largestObservedNonInfinityWidth ?: Constraints.Infinity
                    }

                    val sizes = measurables.map { measurable ->
                        measurable.judoMeasure(
                            Size(
                                maxWidthConstraint,
                                proposedHeights[measurable] ?: 0
                            )
                        )
                    }

                    Size(
                        sizes.maxOf { it.width },
                        sizes.sumOfWithLayoutSpacing(spacingAsPx) { it.height }
                    )
                }
            } finally {
                Trace.endSection()
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            Trace.beginSection("VStackLayer::intrinsicMeasure::horizontalFlex")
            return try {
                mapMinIntrinsicAsFlex {
                    val childRanges = measurables.map { it.judoHorizontalFlex() }

                    val lower = childRanges.maxOfOrNull { it.first } ?: 0
                    val upper = childRanges.maxOfOrNull { it.last } ?: 0

                    IntRange(lower, upper)
                }
            } finally {
                Trace.endSection()
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            Trace.beginSection("VStackLayer::intrinsicMeasure::verticalFlex")
            return try {
                mapMinIntrinsicAsFlex {
                    val childRanges = measurables.map { it.judoVerticalFlex() }

                    // we'll add spacing since spacing is inflexible.
                    val spacing = maxOf(
                        (spacingAsPx * (measurables.count() - 1)),
                        0
                    )

                    val lower = childRanges.sumOf { it.first } + spacing
                    val higher = childRanges.maxOfOrNull { it.last }?.let { max -> max.unlessInfinity { it + spacing } } ?: 0
                    IntRange(lower, higher)
                }
            } finally {
                Trace.endSection()
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

@Preview
@Composable
private fun StackCenterAligned() {
    VStackLayer(alignment = Alignment.CENTER) {
        TestBox()
        Text(text = "Judo rules")
    }
}

@Preview
@Composable
private fun StackInfiniteContent() {
    VStackLayer(alignment = Alignment.CENTER) {
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")), cornerRadius = 8f)
        Text("Judo rules")
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("red")), cornerRadius = 8f)
    }
}

@Preview
@Composable
private fun OfferedInfinityHeight() {
    // the scroll container will offer the stack infinity height.  We want to test this use case
    // explicitly without depending on ScrollContainerLayer (which itself has an implicit VStack in
    // it, making for an awkward test situation)

    Layout({
        VStackLayer() {
            // these rectangles should be offered infinity on height, and ExpandMeasurePolicy
            // should fall back to 10 dp.
            RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")))
            RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("red")))
        }
    }, measurePolicy = InfiniteHeightMeasurePolicy)
}

@Preview
@Composable
private fun IntegrationSpacer() {
    VStackLayer() {
        TextLayer("Judo")
        SpacerLayer()
        TextLayer("Rocks")
    }
}

@Preview
@Composable
private fun LongContent() {
    VStackLayer {
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("red")), modifier = Modifier.requiredHeight(100.dp))
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("green")), modifier = Modifier.requiredHeight(100.dp))
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")), modifier = Modifier.requiredHeight(100.dp))

        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("red")), modifier = Modifier.requiredHeight(100.dp))
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("green")), modifier = Modifier.requiredHeight(100.dp))
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")), modifier = Modifier.requiredHeight(100.dp))

        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("red")), modifier = Modifier.requiredHeight(100.dp))
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("green")), modifier = Modifier.requiredHeight(100.dp))
        RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")), modifier = Modifier.requiredHeight(100.dp))
    }
}
