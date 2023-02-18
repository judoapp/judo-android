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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.nodes.Rectangle
import app.judo.sdk.compose.model.values.Alignment
import app.judo.sdk.compose.model.values.Shadow
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.utils.ExpandMeasurePolicy
import app.judo.sdk.compose.ui.values.getComposeColor

@Composable
internal fun ShadowJudoModifier(
    shadow: Shadow?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (shadow != null) {
        // instead of using .judoShadow() *directly* on our content, instead we'll use
        // OverlayBackgroundMeasurePolicy to wrap it. This is to work around an issue where
        // drawBehind() somehow is interfering with layout. This approach means that .drawBehind { }
        // is safely nestled within a measurement policy that sets its size from that of content()
        // where it cannot interfere with layout.
        Layout({
            content(Modifier)
            Layout(
                { /* empty expanding layout */ },
                measurePolicy = ExpandMeasurePolicy(expandChildren = false),
                modifier = Modifier
                    .judoShadow(Environment.LocalNode.current, shadow, Environment.LocalIsDarkTheme.current)
                    .layoutId("backgroundOrOverlay")
            )
        }, measurePolicy = OverlayBackgroundMeasurePolicy(overlay = false, alignment = Alignment.CENTER), modifier = modifier)
    } else {
        content(modifier)
    }
}

internal fun Modifier.judoShadow(node: Node?, shadow: Shadow, isDarkMode: Boolean) = this
    .then(
        when (node) {
            // `node` is only null within certain previews or in contexts where bare layers
            // (such as a stack) are being used without a node (upon which no shadows are placed.)
            is Rectangle, null -> drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint()
                    val frameworkPaint = paint.asFrameworkPaint()

                    //Adjust shadow size to better match Judo iOS
                    val sizeAdjustment = 1.2f

                    frameworkPaint.color = Color.Transparent.toArgb()
                    frameworkPaint.setShadowLayer(
                        (shadow.blur * sizeAdjustment).dp.toPx(),
                        (shadow.x * sizeAdjustment).dp.toPx(),
                        (shadow.y * sizeAdjustment).dp.toPx(),
                        shadow.color.getComposeColor(isDarkMode).toArgb()
                    )

                    var cornerRadius: Float = 0f
                    if (node is Rectangle) {
                        cornerRadius = (node.cornerRadius * sizeAdjustment).dp.toPx()
                    }

                    canvas.drawRoundRect(
                        0f,
                        0f,
                        this.size.width,
                        this.size.height,
                        cornerRadius,
                        cornerRadius,
                        paint
                    )
                }
            }
            else -> Modifier
        }
    )
