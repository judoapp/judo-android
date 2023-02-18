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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.nodes.Rectangle
import app.judo.sdk.compose.model.values.Fill
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.values.getComposeColor
import app.judo.sdk.compose.ui.vendor.clip

@Composable
internal fun MaskModifier(
    mask: Node?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (mask != null) {
        content(
            modifier
                .judoMask(mask, Environment.LocalIsDarkTheme.current)
        )
    } else {
        content(modifier)
    }
}

private fun Modifier.judoMask(mask: Node, isDarkMode: Boolean) = this
    .then(
        if (mask is Rectangle) {
            // Using vendored version of .clip here to enable Packed Intrinsics passthrough.
            var modifier = Modifier.clip(RoundedCornerShape(mask.cornerRadius.dp))
            modifier = when (mask.fill) {
                is Fill.FlatFill -> {
                    modifier.alpha(mask.fill.color.getComposeColor(isDarkMode).alpha)
                }

                is Fill.GradientFill -> {
                    modifier.alpha(1.0f)
                }
            }
            this.then(modifier)
        } else {
            this
        }
    )


