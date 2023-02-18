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

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints

/**
 * A version of [Modifier.layout] that, rather than falling back on the intrinsic node
 * coordinator's default implementation of intrinsics that uses measure with a shim around
 * child intrinsic measurables, just passes through to the child's intrinsic size.
 *
 * Naturally, this version is only appropriate for use for custom layouts that do not
 * differ from the intrinsic size of the child.
 */
fun Modifier.layoutWithIntrinsicsPassthrough(
    measure: MeasureScope.(Measurable, Constraints) -> MeasureResult
) = this.then(
    PackedIntrinsicsSafeLayoutImpl(
        measureBlock = measure,
        inspectorInfo = debugInspectorInfo {
            name = "layout"
            properties["measure"] = measure
        }
    )
)

private class PackedIntrinsicsSafeLayoutImpl(
    val measureBlock: MeasureScope.(Measurable, Constraints) -> MeasureResult,
    inspectorInfo: InspectorInfo.() -> Unit,
) : LayoutModifier, InspectorValueInfo(inspectorInfo) {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ) = measureBlock(measurable, constraints)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? PackedIntrinsicsSafeLayoutImpl ?: return false
        return measureBlock == otherModifier.measureBlock
    }

    override fun hashCode(): Int {
        return measureBlock.hashCode()
    }

    override fun toString(): String {
        return "LayoutModifierImpl(measureBlock=$measureBlock)"
    }

    // This differs from the stock Modifier.layout by implementing pass-through intrinsics.
    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return measurable.maxIntrinsicHeight(width)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return measurable.maxIntrinsicWidth(height)
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return measurable.minIntrinsicHeight(width)
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return measurable.minIntrinsicWidth(height)
    }
}