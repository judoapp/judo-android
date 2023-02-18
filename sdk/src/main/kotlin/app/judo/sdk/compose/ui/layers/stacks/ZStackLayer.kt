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

import android.graphics.Point
import android.os.Trace
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import app.judo.sdk.compose.model.nodes.ZStack
import app.judo.sdk.compose.model.values.Alignment
import app.judo.sdk.compose.ui.layers.Children
import app.judo.sdk.compose.ui.layers.LayerBox
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.modifiers.judoChildModifierData
import app.judo.sdk.compose.ui.utils.ifInfinity

@Composable
internal fun ZStackLayer(node: ZStack) {
    ZStackLayer(node.alignment, judoModifiers = JudoModifiers(node)) {
        Children(children = node.children)
    }
}

@Composable
internal fun ZStackLayer(
    alignment: Alignment = Alignment.CENTER,
    judoModifiers: JudoModifiers = JudoModifiers(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    LayerBox(judoModifiers, modifier = modifier) {
        Layout(content, measurePolicy = zStackMeasurePolicy(alignment))
    }
}

internal fun zStackMeasurePolicy(alignment: Alignment): MeasurePolicy {
    return object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            if (measurables.isEmpty()) {
                return layout(0, 0) { }
            }

            Trace.beginSection("ZStack::measure")

            data class ZStackChild(
                val measurable: Measurable,
                val zIndex: Int,
                val priority: Int
            )

            val children = measurables.mapIndexed { index, measurable ->
                ZStackChild(
                    measurable = measurable,
                    zIndex = index,
                    priority = measurable.judoChildModifierData?.layoutPriority ?: 0
                )
            }

            val maxPriority = children.maxOf { it.priority }

            val maxPriorityChildren = children
                .filter { child -> child.priority == maxPriority }

            val lowPriorityMeasurables = children
                .filter { child -> child.priority != maxPriority }

            data class PlaceableChild(
                val placeable: Placeable,
                val zIndex: Int
            )

            Trace.beginSection("ZStack::measure::fallback")
            // ZStack needs to know what to propose to all the high-priority children
            // in the event of the ZStack itself being proposed an Infinity.
            val measureSize = if (constraints.maxWidth == Constraints.Infinity || constraints.maxHeight == Constraints.Infinity) {
                // fallback size. dimensions for which a fallback could not be determined is left
                // at Infinity.  need maximum non-infinity value.

                val childSizes = maxPriorityChildren.map {
                    it.measurable.judoMeasure(
                        Size(
                            constraints.maxWidth,
                            constraints.maxHeight
                        )
                    )
                }

                val fallbackWidth = childSizes.map { it.width }.filter { it != Constraints.Infinity }.maxOrNull() ?: constraints.maxWidth
                val fallbackHeight = childSizes.map { it.height }.filter { it != Constraints.Infinity }.maxOrNull() ?: constraints.maxHeight

                Size(
                    constraints.maxWidth.ifInfinity { fallbackWidth },
                    constraints.maxHeight.ifInfinity { fallbackHeight }
                )
            } else {
                Size(
                    constraints.maxWidth,
                    constraints.maxHeight
                )
            }

            Trace.endSection()

            val maxPriorityPlaceables = maxPriorityChildren.map { child ->
                val childConstraints = constraints.copy(
                    minWidth = 0,
                    maxWidth = measureSize.width,
                    minHeight = 0,
                    maxHeight = measureSize.height
                )
                PlaceableChild(
                    child.measurable.measure(
                        childConstraints
                    ),
                    child.zIndex
                )
            }

            val width = maxPriorityPlaceables.maxOf { it.placeable.width }
            val height = maxPriorityPlaceables.maxOf { it.placeable.height }

            val lowPriorityPlaceables = lowPriorityMeasurables.map { child ->
                val childConstraints = constraints.copy(
                    minWidth = 0,
                    maxWidth = width,
                    minHeight = 0,
                    maxHeight = height
                )
                PlaceableChild(
                    child.measurable.measure(
                        childConstraints
                    ),
                    child.zIndex
                )
            }

            val l = layout(width, height) {
                val placeableChildren = maxPriorityPlaceables + lowPriorityPlaceables
                placeableChildren.forEach { child ->
                    val placeable = child.placeable
                    val centerWidth = { maxOf(width / 2 - placeable.width / 2, 0) }
                    val centerHeight = { maxOf(height / 2 - placeable.height / 2, 0) }
                    val right = { width - placeable.width }
                    val bottom = { height - placeable.height }

                    val position: Point = when (alignment) {
                        Alignment.TOP -> Point(centerWidth(), 0)
                        Alignment.BOTTOM -> Point(centerWidth(), bottom())
                        Alignment.LEADING -> Point(0, centerHeight())
                        Alignment.TRAILING -> Point(right(), centerHeight())
                        Alignment.TOP_LEADING -> Point(0, 0)
                        Alignment.TOP_TRAILING -> Point(right(), 0)
                        Alignment.BOTTOM_LEADING -> Point(0, bottom())
                        Alignment.BOTTOM_TRAILING -> Point(right(), bottom())
                        Alignment.FIRST_TEXT_BASELINE -> Point(
                            0,
                            0
                        ) // TODO: This alignment type has its own issue: https://github.com/judoapp/judo-android-develop/issues/636
                        else -> Point(
                            centerWidth(),
                            centerHeight()
                        )
                    }
                    placeable.place(position.x, position.y, zIndex = child.zIndex * -1f)
                }
            }
            Trace.endSection()
            return l
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            Trace.beginSection("ZStack::intrinsicMeasure")
            return try {
                this.mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
                    if (measurables.isEmpty()) {
                        return@mapMaxIntrinsicWidthAsMeasure Size(0, 0)
                    }

                    data class ZStackChild(
                        val measurable: IntrinsicMeasurable,
                        val zIndex: Int,
                        val priority: Int
                    )

                    val children = measurables.mapIndexed { index, measurable ->
                        ZStackChild(
                            measurable = measurable,
                            zIndex = index,
                            priority = measurable.judoChildModifierData?.layoutPriority ?: 0
                        )
                    }

                    val maxPriority = children.maxOf { it.priority }

                    val maxPriorityChildren = children
                        .filter { child -> child.priority == maxPriority }

                    val lowPriorityMeasurables = children
                        .filter { child -> child.priority != maxPriority }

                    data class PlaceableChild(
                        val size: Size,
                        val zIndex: Int
                    )

                    Trace.beginSection("ZStack::intrinsicMeasure::fallback")
                    // ZStack needs to know what to propose to all the high-priority children
                    // in the event of the ZStack itself being proposed an Infinity.
                    val measureSize = if (proposedWidth == Constraints.Infinity || proposedHeight == Constraints.Infinity) {
                        // fallback size. dimensions for which a fallback could not be determined is left
                        // at Infinity.  need maximum non-infinity value.

                        val childSizes = maxPriorityChildren.map {
                            it.measurable.judoMeasure(
                                Size(
                                    proposedWidth,
                                    proposedHeight
                                )
                            )
                        }

                        val fallbackWidth = childSizes.map { it.width }.filter { it != Constraints.Infinity }.maxOrNull() ?: proposedWidth
                        val fallbackHeight = childSizes.map { it.height }.filter { it != Constraints.Infinity }.maxOrNull() ?: proposedHeight

                        Size(
                            proposedWidth.ifInfinity { fallbackWidth },
                            proposedHeight.ifInfinity { fallbackHeight }
                        )
                    } else {
                        Size(
                            proposedWidth,
                            proposedHeight
                        )
                    }
                    Trace.endSection()

                    val maxPriorityPlaceables = maxPriorityChildren.map { child ->
                        PlaceableChild(
                            child.measurable.judoMeasure(
                                Size(
                                    measureSize.width,
                                    measureSize.height
                                )
                            ),
                            child.zIndex
                        )
                    }

                    Size(
                        maxPriorityPlaceables.maxOf { it.size.width },
                        maxPriorityPlaceables.maxOf { it.size.height }
                    )
                }
            } finally {
                Trace.endSection()
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            return try {
                Trace.beginSection("ZStackLayer::intrinsicMeasure::verticalFlex")
                mapMinIntrinsicAsFlex {
                    // zstack flex would be constrained, for both dimensions, the most
                    // inflexible child. oh snap layout priority changes the behavior, only top?
                    // (For hstack/hvstack, I believe layout priority not material to flex, but
                    // here, because of Zstack's fallback behaviour, it is.)

                    data class ZStackChild(
                        val measurable: IntrinsicMeasurable,
                        val zIndex: Int,
                        val priority: Int
                    )

                    // TODO: all this may produce a lot of garbage for the hotpath. Optimize,
                    //  potentially.
                    val children = measurables.mapIndexed { index, measurable ->
                        ZStackChild(
                            measurable = measurable,
                            zIndex = index,
                            priority = measurable.judoChildModifierData?.layoutPriority ?: 0
                        )
                    }

                    val maxPriority = children.maxOfOrNull { it.priority } ?: 0

                    val maxPriorityChildren = children
                        .filter { child -> child.priority == maxPriority }


                    val childRanges = maxPriorityChildren.map { it.measurable.judoVerticalFlex() }

                    val lower = childRanges.maxOfOrNull { it.first } ?: 0
                    val upper = childRanges.maxOfOrNull { it.last } ?: 0


                    IntRange(lower, upper)
                }
            } finally {
                Trace.endSection()
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            return try {
                Trace.beginSection("ZStackLayer::intrinsicMeasure::horizontalFlex")
                mapMinIntrinsicAsFlex {
                    // zstack flex would be constrained, for both dimensions, the most
                    // inflexible child. oh snap layout priority changes the behavior, only top?
                    // (For hstack/hvstack, I believe layout priority not material to flex, but
                    // here, because of Zstack's fallback behaviour, it is.)

                    data class ZStackChild(
                        val measurable: IntrinsicMeasurable,
                        val zIndex: Int,
                        val priority: Int
                    )

                    // TODO: all this may produce a lot of garbage for the hotpath. Optimize,
                    //  potentially.
                    val children = measurables.mapIndexed { index, measurable ->
                        ZStackChild(
                            measurable = measurable,
                            zIndex = index,
                            priority = measurable.judoChildModifierData?.layoutPriority ?: 0
                        )
                    }

                    val maxPriority = children.maxOfOrNull { it.priority } ?: 0

                    val maxPriorityChildren = children
                        .filter { child -> child.priority == maxPriority }


                    val childRanges = maxPriorityChildren.map { it.measurable.judoHorizontalFlex() }

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
