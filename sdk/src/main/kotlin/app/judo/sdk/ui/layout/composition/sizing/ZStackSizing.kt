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
import kotlinx.coroutines.CoroutineScope

internal fun ZStack.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate max possible child size so this can be passed to children
    val pxFrame = pxFramer.getPixelFrame(context)

    val heightConstraint: Dimension = when(val intrinsicHeight = parentConstraints.height) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Inf
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Inf
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(intrinsicHeight.value, pxFrame.minHeight))
                else -> Dimension.Value(intrinsicHeight.value)
            }
        }
    }

    val widthConstraint: Dimension = when(val intrinsicWidth = parentConstraints.width) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Inf
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Inf
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                else -> Dimension.Inf
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, intrinsicWidth.value))
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(intrinsicWidth.value)
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                pxFrame?.minWidth != null -> Dimension.Value(maxOf(intrinsicWidth.value, pxFrame.minWidth))
                else -> Dimension.Value(intrinsicWidth.value)
            }
        }
    }

    val widthForChildMeasure = when(widthConstraint) {
        is Dimension.Inf -> Dimension.Inf
        is Dimension.Value -> Dimension.Value(widthConstraint.value - (padding?.leading?.dp?.toPx(context) ?: 0f) - (padding?.trailing?.dp?.toPx(context) ?: 0f))
    }

    val heightForChildMeasure = when(heightConstraint) {
        is Dimension.Inf -> Dimension.Inf
        is Dimension.Value -> Dimension.Value(heightConstraint.value - (padding?.top?.dp?.toPx(context) ?: 0f) - (padding?.bottom?.dp?.toPx(context) ?: 0f))
    }

    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)
    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)


    // ask children to compute sizes given bounds respecting layout priority
    val priorityGroupedChildren = treeNode.children.groupBy { (it.value as Layer).determineLayoutPriority() }

    val maxPriorityKey = priorityGroupedChildren.keys.maxOrNull() ?: 0
    val maxPriorityGroupedChildren = priorityGroupedChildren.filter { it.key == maxPriorityKey }.flatMap { it.value }
    val otherPriorityGroupChildren = priorityGroupedChildren.filter { it.key != maxPriorityKey }.flatMap { it.value }

    maxPriorityGroupedChildren.forEach {
        it.computeSize(context, Dimensions(widthForChildMeasure, heightForChildMeasure))
    }

    val measuredMaxChildHeight = maxPriorityGroupedChildren.maxOfOrNull { (it.value as Layer).sizeAndCoordinates.height } ?: 0f
    val measuredMaxChildWidth = maxPriorityGroupedChildren.maxOfOrNull { (it.value as Layer).sizeAndCoordinates.width } ?: 0f

    val maxChildHeightForMeasure = when {
        heightConstraint is Dimension.Inf && pxFrame?.maxHeight is MaxHeight.Finite -> minOf(measuredMaxChildHeight, pxFrame.maxHeight.value)
        heightConstraint is Dimension.Inf && pxFrame?.minHeight != null -> maxOf(measuredMaxChildHeight, pxFrame.minHeight)
        else -> measuredMaxChildHeight
    }

    val maxChildWidthForMeasure = when {
        widthConstraint is Dimension.Inf && pxFrame?.maxWidth is MaxWidth.Finite -> minOf(measuredMaxChildWidth, pxFrame.maxWidth.value)
        widthConstraint is Dimension.Inf && pxFrame?.minWidth != null -> maxOf(measuredMaxChildWidth, pxFrame.minWidth)
        else -> measuredMaxChildWidth
    }

    // certain children might change size (e.g. inf constrained rectangles) if measured with fixed dimension constraints
    maxPriorityGroupedChildren.forEach {
        it.clearSizeAndPositioning()
        it.computeSize(context, Dimensions(Dimension.Value(maxChildWidthForMeasure), Dimension.Value(maxChildHeightForMeasure)))
    }

    // ask children to compute sizes given bounds
    otherPriorityGroupChildren.forEach {
        it.computeSize(context, Dimensions(Dimension.Value(maxChildWidthForMeasure), Dimension.Value(maxChildHeightForMeasure)))
    }

    val nodeWidth = when {
        pxFrame?.minWidth != null ->  maxOf(pxFrame.minWidth, maxChildWidthForMeasure + horizontalPadding)
        (frame.unboundedWidth() || widthConstraint is Dimension.Inf) -> maxChildWidthForMeasure + horizontalPadding
        else -> (widthConstraint as Dimension.Value).value
    }

    val nodeHeight = when {
        pxFrame?.minHeight != null -> maxOf(pxFrame.minHeight, maxChildHeightForMeasure + verticalPadding)
        (frame.unboundedHeight() || heightConstraint is Dimension.Inf) -> maxChildHeightForMeasure + verticalPadding
        else -> (heightConstraint as Dimension.Value).value
    }

    // set ZStack size, content height/width is size of ZStack. height and width are height and width including frame
    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
        width = nodeWidth,
        height = nodeHeight,
        contentHeight = maxChildHeightForMeasure,
        contentWidth = maxChildWidthForMeasure
    )
    
    background?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
}