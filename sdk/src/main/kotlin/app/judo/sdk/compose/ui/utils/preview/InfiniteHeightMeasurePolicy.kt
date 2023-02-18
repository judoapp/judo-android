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

package app.judo.sdk.compose.ui.utils.preview

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

/**
 * This measure policy can be used to test a composable to see its behaviour when it is
 * offered an infinity constraint.
 *
 * This is meant for preview/testing use only.
 */
internal object InfiniteHeightMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val childConstraints = constraints.copy(
            maxHeight = Constraints.Infinity
        )
        val placeables = measurables.map { it.measure(childConstraints) }

        return layout(placeables.maxOf { it.width }, placeables.maxOf { it.height }) {
            placeables.forEach {
                it.placeRelative(0, 0)
            }
        }
    }
}

/**
 * This measure policy can be used to test a composable to see its behaviour when it is
 * offered an infinity constraint.
 *
 * This is meant for preview/testing use only.
 */
internal object InfiniteWidthMeasurePolicy : MeasurePolicy {
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
