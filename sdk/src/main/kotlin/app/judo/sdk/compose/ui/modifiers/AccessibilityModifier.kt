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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import app.judo.sdk.compose.model.values.Accessibility

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AccessibilityModifier(
    accessibility: Accessibility?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (accessibility != null) {
        content(modifier.semantics {
            if (accessibility.isHidden) {
                invisibleToUser()
            }

            accessibility.label?.let {
                contentDescription = accessibility.label
            }

            if (accessibility.isHeader) {
                heading()
            }

            //TODO: Support the rest of the accessibility node, as API is updated
        })
    } else {
        content(modifier)
    }
}
