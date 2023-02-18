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

import androidx.compose.ui.unit.Constraints

/**
 * Basic copy of the [sumOf] implementation but adding layout spacing to the return if needed.
 * Used in all StackLayers.
 */
inline fun <T> Iterable<T>.sumOfWithLayoutSpacing(spacingAsPx: Int, selector: (T) -> Int): Int {
    var sum: Int = 0
    for (element in this) {
        if (element == Constraints.Infinity) {
            return Constraints.Infinity
        }
        sum += selector(element)
    }
    return sum + maxOf(spacingAsPx * (this.count() - 1), 0)
}

fun Iterable<Int>.sumOfWithLayoutSpacing(spacingAsPx: Int): Int {
    var sum: Int = 0
    for (element in this) {
        if (element == Constraints.Infinity) {
            return Constraints.Infinity
        }
        sum += element
    }
    return sum + maxOf(spacingAsPx * (this.count() - 1), 0)
}
