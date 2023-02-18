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

import android.os.Trace
import android.util.Size
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.layout.FeatureFlags.fastFlexibilitySort
import app.judo.sdk.compose.ui.layout.GREATEST_FINITE
import app.judo.sdk.compose.ui.layout.judoHorizontalFlex
import app.judo.sdk.compose.ui.layout.judoMeasure
import app.judo.sdk.compose.ui.modifiers.judoChildModifierData
import java.util.*

/**
 * Perform some operation on an Int value unless it is a [Constraints.Infinity] value, in which case
 * pass the infinity through.
 *
 * This is often useful when processing handling maxWidth/maxHeight values arriving via
 * [Constraints] in measurement policies.
 */
internal inline fun Int.unlessInfinity(map: (Int) -> Int): Int =
    if (this == Constraints.Infinity) this else map(this)

/**
 * If an Int value is an infinity, replace it with the value yielded by [fallback].
 */
internal inline fun Int.ifInfinity(fallback: (Int) -> Int): Int =
    if (this == Constraints.Infinity) fallback(this) else this

/**
 * Perform some operation on a Float value unless it is a [Float.isInfinite] value, in which case
 * pass the infinity through.
 */
internal inline fun Float.unlessInfinity(map: (Float) -> Float): Float =
    if (this.isInfinite()) this else map(this)

/**
 * If a Float value is an infinity, replace it with the value yielded by [fallback].
 */
internal inline fun Float.ifInfinity(fallback: (Float) -> Float): Float =
    if (this.isInfinite()) fallback(this) else this

// The following are several routines for grouping and processing measurables.

// Because [IntrinsicMeasurable] and [Measurable] are incompatible types with the same surface
// form, duplications of each method were needed. Ditto for [Placeable] in certain instances.

/**
 * Of all the provided intrinsic measurables, filters them to just the ones that have the highest
 * layout priority value.
 */
internal fun Collection<IntrinsicMeasurable>.filterByMaxPriority(): Collection<IntrinsicMeasurable> {
    val measurablesByPriority = associateBy({ it }) { measurable ->
        val priority = measurable.judoChildModifierData?.layoutPriority
        // if any lack a priority, then just have byMaxPriority return the entire list.
        priority ?: 0
    }

    val maxPriority = measurablesByPriority.maxOfOrNull { it.value }

    return measurablesByPriority.filterValues { it == maxPriority }.keys
}

/**
 * Group all the measurables by their Judo layout priority, with the groups ordered by priority.
 *
 * Useful in stack implementations.
 */
internal fun Collection<Measurable>.groupByPriority(): SortedMap<Int, List<Measurable>> {
    return groupBy { measurable ->
        val priority = measurable.judoChildModifierData?.layoutPriority
        priority ?: 0
    }.toSortedMap(reverseOrder())
}

/**
 * Group all the measurables by their Judo layout priority, with the groups ordered by priority.
 *
 * Useful in stack implementations.
 */
@JvmName("groupByPriorityIntrinsicMeasurable")
internal fun Collection<IntrinsicMeasurable>.groupByPriority(): SortedMap<Int, List<IntrinsicMeasurable>> {
    return groupBy { measurable ->
        val priority = measurable.judoChildModifierData?.layoutPriority
        priority ?: 0
    }.toSortedMap(reverseOrder())
}

@JvmName("filterByMaxPriorityPlaceable")
internal fun Collection<Placeable>.filterByMaxPriority(): Collection<Placeable> {
    val measurablesByPriority = associateBy({ it }) { placeable ->
        val priority = placeable.judoChildModifierData?.layoutPriority
        // if any lack a priority, then just have byMaxPriority return the entire list.
        priority ?: 0
    }

    val maxPriority = measurablesByPriority.maxOfOrNull { it.value }

    return measurablesByPriority.filterValues { it == maxPriority }.keys
}

internal fun Collection<Measurable>.sortByHorizontalFlexibility(maxHeight: Int): List<Measurable> {
    Trace.beginSection("sortByHorizontalFlexibility")
    val byFlexibility = associateBy({ it }) { measurable ->
        measurable.annotateIntrinsicsCrash {
            if (fastFlexibilitySort) {
                val range = measurable.judoHorizontalFlex()

                range.last - range.first
            } else {
                val lower = measurable.judoMeasure(
                    Size(0, maxHeight)
                )

                val upper = measurable.judoMeasure(
                    Size(Int.GREATEST_FINITE, maxHeight)
                )

                upper.width - lower.width
            }
        }
    }
    val r = byFlexibility.entries.sortedBy { it.value }.map { it.key }
    Trace.endSection()
    return r
}

internal fun Collection<Measurable>.sortByVerticalFlexibility(maxWidth: Int): List<Measurable> {
    Trace.beginSection("sortByVerticalFlexibility")
    val byFlexibility = associateBy({ it }) { measurable ->
        measurable.annotateIntrinsicsCrash {
            if (fastFlexibilitySort) {
                val range = measurable.judoVerticalFlex()

                range.last - range.first
            } else {
                val lower = measurable.judoMeasure(
                    Size(maxWidth, 0)
                )

                val upper = measurable.judoMeasure(
                    Size(maxWidth, Int.GREATEST_FINITE)
                )

                upper.height - lower.height
            }
        }
    }
    val r = byFlexibility.entries.sortedBy { it.value }.map { it.key }
    Trace.endSection()
    return r
}

@JvmName("sortByHorizontalFlexibilityIntrinsicMeasurable")
internal fun Collection<IntrinsicMeasurable>.sortByHorizontalFlexibility(maxHeight: Int): List<IntrinsicMeasurable> {
    // Disabled flexibility sort in intrinsics, since doing so has massive performance implications,
    // with minimum degradation in layout correctness.
    if (FeatureFlags.flexibilitySortInStackIntrinsics) return this.toList()
    Trace.beginSection("sortByHorizontalFlexibilityIntrinsicMeasurable")
    val byFlexibility = associateBy({ it }) { measurable ->
        measurable.annotateIntrinsicsCrash {
            val range = measurable.judoHorizontalFlex()

            range.last - range.first
        }
    }
    val r = byFlexibility.entries.sortedBy { it.value }.map { it.key }
    Trace.endSection()
    return r
}

@JvmName("sortByVerticalFlexibilityIntrinsicMeasurable")
internal fun Collection<IntrinsicMeasurable>.sortByVerticalFlexibility(maxWidth: Int): List<IntrinsicMeasurable> {
    // Disabled flexibility sort in intrinsics, since doing so has massive performance implications,
    // with minimum degradation in layout correctness.
    if (!FeatureFlags.flexibilitySortInStackIntrinsics) return this.toList()
    Trace.beginSection("sortByVerticalFlexibilityIntrinsicMeasurable")
    val byFlexibility = associateBy({ it }) { measurable ->
        measurable.annotateIntrinsicsCrash {
            val range = measurable.judoVerticalFlex()

            range.last - range.first
        }
    }
    val r = byFlexibility.entries.sortedBy { it.value }.map { it.key }
    Trace.endSection()
    return r
}
