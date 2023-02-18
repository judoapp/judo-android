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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import app.judo.sdk.compose.model.nodes.Rectangle
import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.model.values.Border
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.model.values.Fill
import app.judo.sdk.compose.model.values.GradientReference
import app.judo.sdk.compose.model.values.GradientValue
import app.judo.sdk.compose.ui.layout.StripPackedJudoIntrinsics
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.utils.ExpandMeasurePolicy
import app.judo.sdk.compose.ui.utils.preview.InfiniteHeightMeasurePolicy
import app.judo.sdk.compose.ui.values.getComposeBrush
import app.judo.sdk.compose.ui.values.getComposeColor

@Composable
internal fun RectangleLayer(node: Rectangle, modifier: Modifier = Modifier) {
    RectangleLayer(modifier, fill = node.fill, border = node.border, cornerRadius = node.cornerRadius, judoModifiers = JudoModifiers(node))
}

@Composable
internal fun RectangleLayer(modifier: Modifier = Modifier, fill: Fill, border: Border? = null, cornerRadius: Float = 0f, judoModifiers: JudoModifiers = JudoModifiers()) {
    LayerBox(judoModifiers = judoModifiers, modifier = modifier) {
        var size by remember { mutableStateOf(Size.Zero) }

        val borderModifier = if (border != null) {
            Modifier.border(width = border.width.dp, color = border.color.getComposeColor(), RoundedCornerShape(cornerRadius.dp))
        } else {
            Modifier
        }

        val rectangleModifier = when (fill) {
            is Fill.FlatFill -> {
                modifier
                    .then(borderModifier)
                    .clip(RoundedCornerShape(cornerRadius.dp))
                    .background(fill.color.getComposeColor())
            }
            is Fill.GradientFill -> {
                modifier
                    .then(borderModifier)
                    .clip(RoundedCornerShape(cornerRadius.dp))
                    .background(fill.gradient.getComposeBrush(size = size))
            }
        }

        Layout(
            measurePolicy = ExpandMeasurePolicy(true),
            content = {
                Box(
                    modifier = rectangleModifier
                        .then(StripPackedJudoIntrinsics())
                        .then(Modifier.onGloballyPositioned { coordinates -> size = coordinates.size.toSize() })
                )
            }
        )
    }
}

@Composable
private fun RectangleLayout(modifier: Modifier = Modifier) {
    Layout({
    }, measurePolicy = ExpandMeasurePolicy(expandChildren = false), modifier = modifier)
}

@Preview
@Composable
private fun RectanglePreview() {
    RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")), cornerRadius = 20f)
}

@Preview
@Composable
private fun RectangleBorderPreview() {
    RectangleLayer(
        fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
        border = Border(ColorReference.SystemColor("green"), 2f),
        cornerRadius = 20f
    )
}

@Preview
@Composable
private fun RectangleLayerInInfinity() {
    Layout(
        measurePolicy = InfiniteHeightMeasurePolicy,
        content = {
            RectangleLayer(fill = Fill.FlatFill(ColorReference.SystemColor("blue")), cornerRadius = 20f)
        }
    )
}

@Preview
@Composable
private fun RectangleGradientPreview() {
    // Note: gradients require runtime behaviour so this preview
    // will only work when you press the little green play button
    // to run it on emulator or real device.
    RectangleLayer(
        fill = Fill.GradientFill(
            gradient = GradientReference.CustomGradient(
                GradientValue(
                    to = listOf(0.0f, 1.0f),
                    from = listOf(1.0f, 0.0f),
                    stops = listOf(
                        GradientStop(0.0f, ColorValue(1.0f, 1.0f, 0.0f, 0.0f)),
                        GradientStop(1.0f, ColorValue(1.0f, 0.0f, 0.0f, 1.0f)),
                    )
                )
            )
        ),
        cornerRadius = 20f
    )
}