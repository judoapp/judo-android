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

import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import app.judo.sdk.compose.ui.modifiers.judoChildModifierData

/**
 * No measurables should measure themselves at an infinity size.
 */
fun Collection<Placeable>.assertNoInfiniteSizes(contextName: String) {
    // unfortunately even when assertions (-ea) is off, there's some calculation that goes on here.
    forEach { placeable ->
        assert(placeable.height != Constraints.Infinity && placeable.width  != Constraints.Infinity) {
            val naughtyLayer = placeable.judoChildModifierData?.debugNode
            "Child illegally claimed an infinity dimension in a $contextName (measured size (${placeable.width}, ${placeable.height})): ${naughtyLayer?.prettyPrint() ?: "unknown layer"}"
        }
    }
}

