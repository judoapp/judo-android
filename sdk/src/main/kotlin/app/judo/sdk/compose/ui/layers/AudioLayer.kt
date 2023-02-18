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

package app.judo.sdk.compose.ui.layers

import android.util.Size
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.nodes.Audio
import app.judo.sdk.compose.ui.layout.mapMaxIntrinsicWidthAsMeasure
import app.judo.sdk.compose.ui.layout.mapMinIntrinsicAsFlex
import app.judo.sdk.compose.ui.modifiers.JudoModifiers

@Composable
internal fun AudioLayer(node: Audio) {
    var playerHeight = 88.dp

    MediaPlayer(
        source = node.source,
        looping = node.looping,
        autoPlay = node.autoPlay,
        showControls = true,
        timeoutControls = false,
        modifier = Modifier,
        judoModifiers = JudoModifiers(node),
        measurePolicy = AudioMeasurePolicy(playerHeight)
    )
}

class AudioMeasurePolicy(
    private val playerHeight: Dp
) : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val childConstraints = Constraints.fixed(
            constraints.maxWidth,
            playerHeight.roundToPx()
        )

        val placeables = measurables.map { measurable ->
            measurable.measure(childConstraints)
        }

        return layout(constraints.maxWidth, playerHeight.roundToPx()) {
            placeables.forEach { placeable ->
                placeable.place(0, 0)
            }
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ): Int {
        return mapMaxIntrinsicWidthAsMeasure(height) { proposedSize ->
            Size(
                proposedSize.width,
                playerHeight.roundToPx()
            )
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // completely inflexible on height.
            IntRange(playerHeight.roundToPx(), playerHeight.roundToPx())
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // completely flexible on width.
            IntRange(0, Constraints.Infinity)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int {
        throw IllegalStateException("Only call maxIntrinsicWidth, with packed parameter, on Rover Experiences measurables.")
    }
}
