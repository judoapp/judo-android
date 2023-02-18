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
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.layout.mapMaxIntrinsicWidthAsMeasure

/**
 * This measurement policy is the smallest possible measurement policy, that assumes the size
 * of the child and passes the child's intrinsic sizes through.
 *
 * In part, it is meant to defeat the default Compose measurement policy's attempt to automatically
 * determine intrinsic sizes rather than just delegating it to the child measurable directly.
 *
 * @param stripPackedJudoIntrinsics If this value is on, then any Judo-specific Packed Intrinsics
 *                                  values are stripped. It is important to set this when this
 *                                  measure policy is being used on native (external to Judo)
 *                                  composables.
 */
internal fun SimpleMeasurePolicy(
    traceName: String? = null
): MeasurePolicy {
    return object : MeasurePolicy {

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            require(measurables.size == 1) {
                "SimpleMeasurePolicy expects a single measurable being passed in. Received: $measurables"
            }
            if (traceName != null) {
                Trace.beginSection(traceName)
            }

            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }

            val l = layout(placeables.maxOf { it.width }, placeables.maxOf { it.height }) {
                placeables.forEach { placeable ->
                    placeable.place(0, 0)
                }
            }

            if (traceName != null) {
                Trace.endSection()
            }

            return l
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            val measurable = measurables.singleOrNull()
            assert(measurable != null) {
                "SimpleMeasurePolicy expects a single measurable being passed in. Received: $measurables"
            }
            if (measurable == null) {
                return PackedWidth(0, 0).packedValue
            }

            // just pass through.
            return measurable.annotateIntrinsicsCrash(traceName) {
                measurable.maxIntrinsicWidth(height)
            }
        }

        // [sumOf] is not strictly necessary, as these overrides will always have a single
        // measurable. It's used as a safety precaution.
        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurables: List<IntrinsicMeasurable>,
            height: Int
        ): Int {
            val measurable = measurables.singleOrNull()
            assert(measurable != null) {
                "SimpleMeasurePolicy expects a single measurable being passed in. Received: $measurables"
            }
            if (measurable == null) {
                return IntRange(0, 0).packedValue
            }
            return measurable.minIntrinsicWidth(height)
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            val measurable = measurables.singleOrNull()
            assert(measurable != null) {
                "SimpleMeasurePolicy expects a single measurable being passed in. Received: $measurables"
            }
            if (measurable == null) {
                return IntRange(0, 0).packedValue
            }
            return measurable.minIntrinsicHeight(width)
        }

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            throw IllegalStateException("Only call maxIntrinsicWidth, with packed parameter, on Judo measurables.")
        }
    }
}
