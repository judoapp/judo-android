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

package app.judo.sdk.compose.ui.utils

import android.util.Size
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.values.Axis
import app.judo.sdk.compose.ui.layout.component1
import app.judo.sdk.compose.ui.layout.component2
import app.judo.sdk.compose.ui.layout.mapMaxIntrinsicWidthAsMeasure
import app.judo.sdk.compose.ui.layout.mapMinIntrinsicAsFlex

/**
 * This measurement policy expands to proposed space, participating in the Judo packed
 * intrinsics system.
 *
 * Note that it may be used on native Jetpack Compose measurables as well as Judo ones,
 * as it does not need to use child intrinsics.
 */
internal class ExpandLayoutModifier(
    /**
     * When true, gives fixed constraints to the child(ren) to force them to expand to the size
     * full size consumed by the ExpandMeasurePolicy.
     *
     * Meant for use on non-Judo measurables.
     */
    private val expandChildren: Boolean,

    /**
     * The axis to expand along, or both if null.
     */
    private val axis: Axis? = null,

    /**
     * If an infinity is proposed along a dimension, instead clamp to this size. Default 10 dp.
     */
    private val infinityDefault: Dp = 10.dp,

    /**
     * The size to consume along any not expanded axis. Default 0 dp.
     */
    private val otherAxisSize: Dp = 0.dp
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val maxWidth = when (axis) {
            Axis.VERTICAL -> otherAxisSize.roundToPx()
            Axis.HORIZONTAL, null -> constraints.maxWidth
        }
        val maxHeight = when (axis) {
            Axis.VERTICAL, null -> constraints.maxHeight
            Axis.HORIZONTAL -> otherAxisSize.roundToPx()
        }

        val maxConstraints = if (expandChildren) {
            Constraints.fixed(
                if (maxWidth == Constraints.Infinity) infinityDefault.roundToPx() else maxWidth,
                if (maxHeight == Constraints.Infinity) infinityDefault.roundToPx() else maxHeight
            )
        } else {
            Constraints(
                maxWidth = if (maxWidth == Constraints.Infinity) infinityDefault.roundToPx() else maxWidth,
                maxHeight = if (maxHeight == Constraints.Infinity) infinityDefault.roundToPx() else maxHeight
            )
        }
        return layout(maxConstraints.maxWidth, maxConstraints.maxHeight) {
            measurable.measure(
                maxConstraints
            ).place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return this.mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
            Size(
                when (axis) {
                    Axis.HORIZONTAL, null -> proposedWidth.ifInfinity { infinityDefault.roundToPx() }
                    Axis.VERTICAL -> otherAxisSize.roundToPx()
                },
                when (axis) {
                    Axis.VERTICAL, null -> proposedHeight.ifInfinity {
                        infinityDefault.roundToPx()
                    }
                    Axis.HORIZONTAL -> otherAxisSize.roundToPx()
                }
            )
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // flexible or inflexible depending on axis
            when (axis) {
                Axis.HORIZONTAL, null -> IntRange(0, Constraints.Infinity)
                Axis.VERTICAL -> IntRange(otherAxisSize.roundToPx(), otherAxisSize.roundToPx())
            }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // flexible or inflexible depending on axis
            when (axis) {
                Axis.VERTICAL, null -> IntRange(0, Constraints.Infinity)
                Axis.HORIZONTAL -> IntRange(otherAxisSize.roundToPx(), otherAxisSize.roundToPx())
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

@Deprecated("Consider using ExpandLayoutModifier instead, in order to reduce unnecessary composables")
internal fun ExpandMeasurePolicy(
    /**
     * When true, gives fixed constraints to the child(ren) to force them to expand to the size
     * full size consumed by the ExpandMeasurePolicy.
     *
     * Meant for use on non-Judo measurables.
     */
    expandChildren: Boolean,

    /**
     * The axis to expand along, or both if null.
     */
    axis: Axis? = null,

    /**
     * If an infinity is proposed along a dimension, instead clamp to this size. Default 10 dp.
     */
    infinityDefault: Dp = 10.dp
): MeasurePolicy {
    return object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            val maxWidth = when (axis) {
                Axis.VERTICAL -> 0
                Axis.HORIZONTAL, null -> constraints.maxWidth
            }
            val maxHeight = when (axis) {
                Axis.VERTICAL, null -> constraints.maxHeight
                Axis.HORIZONTAL -> 0
            }

            val maxConstraints = if (expandChildren) {
                Constraints.fixed(
                    if (maxWidth == Constraints.Infinity) infinityDefault.roundToPx() else maxWidth,
                    if (maxHeight == Constraints.Infinity) infinityDefault.roundToPx() else maxHeight
                )
            } else {
                Constraints(
                    maxWidth = if (maxWidth == Constraints.Infinity) infinityDefault.roundToPx() else maxWidth,
                    maxHeight = if (maxHeight == Constraints.Infinity) infinityDefault.roundToPx() else maxHeight
                )
            }

            return layout(maxConstraints.maxWidth, maxConstraints.maxHeight) {
                measurables.forEach { measurable ->
                    val placeable = measurable.measure(
                        maxConstraints
                    )
                    placeable.place(0, 0)
                }
            }
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            return this.mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
                Size(
                    when (axis) {
                        Axis.HORIZONTAL, null -> proposedWidth.ifInfinity { infinityDefault.roundToPx() }
                        Axis.VERTICAL -> 0
                    },
                    when (axis) {
                        Axis.VERTICAL, null -> proposedHeight.ifInfinity {
                            infinityDefault.roundToPx()
                        }
                        Axis.HORIZONTAL -> 0
                    }
                )
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            return mapMinIntrinsicAsFlex {
                // flexible or inflexible depending on axis
                when (axis) {
                    Axis.HORIZONTAL, null -> IntRange(0, Constraints.Infinity)
                    Axis.VERTICAL -> IntRange(0, 0)
                }
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            return mapMinIntrinsicAsFlex {
                // flexible or inflexible depending on axis
                when (axis) {
                    Axis.HORIZONTAL, null -> IntRange(0, Constraints.Infinity)
                    Axis.VERTICAL -> IntRange(0, 0)
                }
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
