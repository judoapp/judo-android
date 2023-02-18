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

import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.values.*

internal data class JudoModifiers(
    val action: Action? = null,
    val frame: Frame? = null,
    val opacity: Float? = null,
    val aspectRatio: Float? = null,
    val padding: Padding? = null,
    val layoutPriority: Int? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val mask: Node? = null,
    val overlay: Overlay? = null,
    val background: Background? = null,
    val accessibility: Accessibility? = null,

    /**
     * Contains a reference to the original node that produced these modifiers, if available.
     *
     * For diagnostic use only.
     */
    val debugNode: Node? = null
) {
    constructor(node: Node) : this(
        action = node.action,
        frame = node.frame,
        opacity = node.opacity,
        aspectRatio = node.aspectRatio,
        padding = node.padding,
        layoutPriority = node.layoutPriority,
        offset = node.offset,
        shadow = node.shadow,
        mask = node.mask,
        overlay = node.overlay,
        background = node.background,
        accessibility = node.accessibility,
        debugNode = node
    )
}

