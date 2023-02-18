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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.nodes.HStack
import app.judo.sdk.compose.model.values.Alignment
import app.judo.sdk.compose.model.values.Axis
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.model.values.Fill
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.layers.Children
import app.judo.sdk.compose.ui.layers.LayerBox
import app.judo.sdk.compose.ui.layers.RectangleLayer
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.utils.*
import app.judo.sdk.compose.ui.utils.groupByPriority
import app.judo.sdk.compose.ui.utils.ifInfinity
import app.judo.sdk.compose.ui.utils.sortByHorizontalFlexibility

@Composable
internal fun HStackLayer(node: HStack) {
    HStackLayer(spacing = node.spacing, alignment = node.alignment, judoModifiers = JudoModifiers(node)) {
        Children(children = node.children)
    }
}

@Composable
internal fun HStackLayer(
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

    LayerBox(judoModifiers, modifier = modifier) {
        CompositionLocalProvider(Environment.LocalStackAxis provides Axis.HORIZONTAL) {
            Layout(content, measurePolicy = hStackMeasurePolicy(spacingAsPx, alignment))
        }
    }
}

/**
 * Measure policy for the HStackLayer.
 * It overrides the intrinsic width functions to remove the default behavior and better handle
 * infinity in children widths.
 */
internal fun hStackMeasurePolicy(spacingAsPx: Int, alignment: Alignment): MeasurePolicy {
    val tag = "HStackMeasurePolicy"
    return object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            if (measurables.isEmpty()) {
                return layout(0, 0) { }
            }

            Trace.beginSection("HStackLayer::measure")

            var remainingWidth =
                if (constraints.maxWidth == Constraints.Infinity) Constraints.Infinity else constraints.maxWidth - maxOf(
                    (spacingAsPx * (measurables.count() - 1)),
                    0
                )
            val proposedWidths = hashMapOf<Measurable, Int>()

            val measurablesGroupedByPriority = measurables.groupByPriority()

            val highestPriority = measurablesGroupedByPriority.keys.firstOrNull()
            var largestObservedNonInfinityHeight: Int? = null

            measurablesGroupedByPriority.forEach { (priority, groupedMeasurables) ->
                val sortedByFlexibility = groupedMeasurables.sortByHorizontalFlexibility(
                    constraints.maxHeight
                )

                sortedByFlexibility.forEachIndexed { index, measurable ->
                    measurable.annotateIntrinsicsCrash {
                        if (remainingWidth == Constraints.Infinity) {
                            proposedWidths[measurable] = Constraints.Infinity
                        } else {
                            val remainingChildren = sortedByFlexibility.size - index
                            val proposedSize = maxOf(remainingWidth / remainingChildren, 0)

                            val childSize = measurable.judoMeasure(
                                Size(
                                    proposedSize,
                                    constraints.maxHeight
                                )
                            )

                            if (priority == highestPriority && childSize.height != Constraints.Infinity) {
                                largestObservedNonInfinityHeight = maxOf(
                                    largestObservedNonInfinityHeight ?: Int.MIN_VALUE,
                                    childSize.height
                                )
                            }

                            remainingWidth -= childSize.width
                            proposedWidths[measurable] = childSize.width
                        }
                    }
                }
            }

            // fallback behaviour on the cross dimension
            val maxHeightConstraint = constraints.maxHeight.ifInfinity {
                largestObservedNonInfinityHeight ?: Constraints.Infinity
            }

            val placeables = measurables.map { measurable ->
                measurable.measure(
                    constraints.copy(
                        maxWidth = proposedWidths[measurable] ?: 0,
                        maxHeight = maxHeightConstraint
                    )
                )
            }

            placeables.assertNoInfiniteSizes("HStack")

            val maxHeight = placeables.maxOf { it.height }
            val width = placeables.sumOfWithLayoutSpacing(spacingAsPx) { it.width }
            val l = layout(width, maxHeight) {
                var xPosition = 0

                placeables.forEach { placeable ->
                    val yPosition = when (alignment) {
                        Alignment.TOP -> 0
                        Alignment.BOTTOM -> maxHeight - placeable.height
                        // TODO: This alignment type has its own issue: https://github.com/judoapp/judo-android-develop/issues/636
                        Alignment.FIRST_TEXT_BASELINE -> 0
                        // The default Alignment for HStack is CENTER.
                        else -> maxOf(maxHeight / 2 - placeable.height / 2, 0)
                    }

                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + spacingAsPx
                }
            }
            Trace.endSection()
            return l
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            Trace.beginSection("HStackLayer::intrinsicMeasure")
            try {
                return mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
                    if (measurables.isEmpty()) {
                        return@mapMaxIntrinsicWidthAsMeasure Size(0, 0)
                    }

                    var remainingWidth =
                        if (proposedWidth == Constraints.Infinity) Constraints.Infinity else proposedWidth - maxOf(
                            (spacingAsPx * (measurables.count() - 1)),
                            0
                        )
                    val proposedWidths = hashMapOf<IntrinsicMeasurable, Int>()

                    val measurablesGroupedByPriority = measurables.groupByPriority()

                    val highestPriority = measurablesGroupedByPriority.keys.firstOrNull()
                    var largestObservedNonInfinityHeight: Int? = null

                    measurablesGroupedByPriority.forEach { (priority, groupedMeasurables) ->
                        val sortedByFlexibility = groupedMeasurables.sortByHorizontalFlexibility(
                            proposedHeight
                        )

                        sortedByFlexibility.forEachIndexed { index, measurable ->
                            measurable.annotateIntrinsicsCrash {
                                if (remainingWidth == Constraints.Infinity) {
                                    proposedWidths[measurable] = Constraints.Infinity
                                } else {
                                    val remainingChildren = sortedByFlexibility.size - index
                                    val proposedSize = maxOf(remainingWidth / remainingChildren, 0)

                                    val childSize = measurable.judoMeasure(
                                        android.util.Size(
                                            proposedSize,
                                            proposedHeight
                                        )
                                    )

                                    if (priority == highestPriority && childSize.height != Constraints.Infinity) {
                                        largestObservedNonInfinityHeight = maxOf(
                                            largestObservedNonInfinityHeight ?: Int.MIN_VALUE,
                                            childSize.height
                                        )
                                    }

                                    remainingWidth -= childSize.width
                                    proposedWidths[measurable] = childSize.width
                                }
                            }
                        }
                    }

                    // fallback behaviour on the cross dimension
                    val maxHeightConstraint = proposedHeight.ifInfinity {
                        largestObservedNonInfinityHeight ?: Constraints.Infinity
                    }

                    val placeables = measurables.map { measurable ->
                        measurable.judoMeasure(
                            Size(
                                proposedWidths[measurable] ?: 0,
                                maxHeightConstraint
                            )
                        )
                    }

                    val maxHeight = placeables.maxOf { it.height }
                    val width = placeables.sumOfWithLayoutSpacing(spacingAsPx) { it.width }

                    Size(width, maxHeight)
                }
            } finally {
                Trace.endSection()
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            Trace.beginSection("HStackLayer::intrinsicMeasure::horizontalFlex")
            return try {
                mapMinIntrinsicAsFlex {
                    val childRanges = measurables.map { it.judoHorizontalFlex() }

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

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            Trace.beginSection("HStackLayer::intrinsicMeasure::verticalFlex")
            return try {
                mapMinIntrinsicAsFlex {
                    val childRanges = measurables.map { it.judoVerticalFlex() }

                    val lower = childRanges.maxOfOrNull { it.first } ?: 0
                    val upper = childRanges.maxOfOrNull { it.last } ?: 0

                    IntRange(lower, upper)
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

@Preview
@Composable
private fun OfferedInfinityWidth() {
    // the scroll container will offer the stack infinity width.  We want to test this use case
    // explicitly without depending on ScrollContainerLayer (which itself has an implicit VStack in
    // it, making for an awkward test situation)

    Layout({
        HStackLayer() {
            // these rectangles should be offered infinity on width, and ExpandMeasurePolicy
            // should fall back to 10 dp.
            RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")))
            RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("red")))
        }
    }, measurePolicy = InfiniteWidthMeasurePolicy)
}

private object InfiniteWidthMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val childConstraints = constraints.copy(
            maxWidth = Constraints.Infinity
        )
        val placeables = measurables.map { it.measure(childConstraints) }

        return layout(placeables.maxOf { it.width }, placeables.maxOf { it.height }) {
            placeables.forEach {
                it.placeRelative(0, 0)
            }
        }
    }
}
