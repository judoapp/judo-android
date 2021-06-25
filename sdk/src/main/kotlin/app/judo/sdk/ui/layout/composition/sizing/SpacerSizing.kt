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

package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun Spacer.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val parentWidthConstraint = when {
        parentConstraints.width is Dimension.Value && treeNode.parent?.value is HStack -> parentConstraints.width.value
        parentConstraints.width is Dimension.Inf && treeNode.parent?.value is HStack -> 10f.dp.toPx(context)
        else -> 0f
    }

    val parentHeightConstraint = when {
        parentConstraints.height is Dimension.Value && treeNode.parent?.value is VStack -> parentConstraints.height.value
        parentConstraints.height is Dimension.Inf && treeNode.parent?.value is VStack -> 10f.dp.toPx(context)
        else -> 0f
    }

    val height: Float = when {
        pxFrame?.maxHeight is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, parentHeightConstraint)
        pxFrame?.maxHeight is MaxHeight.Infinite -> parentHeightConstraint
        pxFrame?.height != null -> pxFrame.height
        pxFrame?.minHeight != null -> maxOf(parentHeightConstraint, pxFrame.minHeight)
        else -> parentHeightConstraint
    }
    val width: Float = when {
        pxFrame?.maxWidth is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, parentWidthConstraint)
        pxFrame?.maxWidth is MaxWidth.Infinite -> parentWidthConstraint
        frame?.width != null -> pxFrame?.width!!
        frame?.minWidth != null -> maxOf(parentWidthConstraint, pxFrame?.minWidth!!)
        else -> parentWidthConstraint
    }

    this.sizeAndCoordinates = sizeAndCoordinates.copy(width = width, height = height, contentWidth = width, contentHeight = height)
}

internal fun Spacer.computePosition() {
    setFrameAlignment()
}