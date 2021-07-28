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
import android.graphics.drawable.VectorDrawable
import android.util.Log
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.medianOf
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun HStack.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
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

    val spacing = spacing.dp.toPx(context)
    val totalSpacing = spacing * (treeNode.children.count() - 1)

    // measure all fixed width children
    treeNode.children.filter { it.horizontalBehavior() == ViewBehavior.WRAP }.forEach {
        val sizeAndCoordinates = (it.value as Layer).sizeAndCoordinates
        val widthForMeasure = if (sizeAndCoordinates.width == 0f) widthForChildMeasure else Dimension.Value(sizeAndCoordinates.width)
        it.computeSize(context, Dimensions(height = heightForChildMeasure, width = widthForMeasure))
    }

    val totalFixedWidth = treeNode.children.filter { it.horizontalBehavior() == ViewBehavior.WRAP }.sumOf { it.getWidth().toDouble() }.toFloat()

    val horizontalExpandChildren = treeNode.children.filter { it.horizontalBehavior() == ViewBehavior.EXPAND_FILL }
    horizontalExpandChildren.forEach { it.clearSizeAndPositioning() }
    
    fun calculateLayoutPriorityGroupSizing(totalHorizontalSpace: Float, priorityGroup: List<TreeNode>, lowerPriorityGroupFixedWidth: Float): Float {
        if (widthConstraint is Dimension.Inf) {
            priorityGroup.forEach {
                it.computeSize(context, Dimensions(height = heightForChildMeasure, width = Dimension.Inf))
            }
        }

        if (widthConstraint is Dimension.Value) {

            // 1 - Initial step which calculates the size of expand behavior children (which fill to expand available space)
            // splitting the available width equally, some children have a fixed width component (minWidth, frame width)
            // if the fixed width component of a child is greater than the space equally split then this child is not allocated any space
            var maxWidthPerChild = Dimension.Value((totalHorizontalSpace) / (priorityGroup.count().toFloat()))

            var groupCount = priorityGroup.count().toFloat()
            var horizontalSpace = totalHorizontalSpace
            val fixedWidthTooLargeGroup  = mutableListOf<TreeNode>()
            var lastHorizontalSpace = 0f

            while (lastHorizontalSpace != horizontalSpace) {
                lastHorizontalSpace = horizontalSpace
                priorityGroup.forEach {
                    if (it.getFixedNodeWidth(context) > maxWidthPerChild.value && it !in fixedWidthTooLargeGroup) {
                        groupCount -= 1f
                        horizontalSpace -= it.getFixedNodeWidth(context)
                        fixedWidthTooLargeGroup.add(it)
                    }
                }
                maxWidthPerChild = Dimension.Value(horizontalSpace / groupCount)
            }

            maxWidthPerChild = Dimension.Value(horizontalSpace / groupCount)

            fixedWidthTooLargeGroup.forEach {
                it.computeSize(context, Dimensions(height = heightForChildMeasure, width = Dimension.Value(it.getFixedNodeWidth(context))))
            }

            priorityGroup.filter { it !in fixedWidthTooLargeGroup }.forEach {
                it.computeSize(context, Dimensions(height = heightForChildMeasure, width = maxWidthPerChild))
            }

            setWidth(widthConstraint.value)

            // end of step 1

            // 2 - This step calculates if we have any width available to split between children that require extra width
            // the available width comes from children in the previous step that don't fill the width allocated to them
            // examples of children with this behaviour are children with max widths or one line text that has enough space to fully display

            fun List<TreeNode>.getWidthSum() = this.sumOf { (it.getWidth().toDouble()) }.toFloat()
            fun calculateExtraHorizontalSpaceAvailable(): Float {
                return treeNode.getWidth() - totalSpacing - lowerPriorityGroupFixedWidth - (treeNode.children.getWidthSum()) - horizontalPadding
            }

            // calculate which children need any extra width
            var childrenNeedExtraWidth = priorityGroup.filter {
                (it.getWidth() + 0.01f >= maxWidthPerChild.value)
                        || it.containsTextThatRequiresHorizontalSpace()
                        || (fixedWidthTooLargeGroup.size == priorityGroup.size && calculateExtraHorizontalSpaceAvailable() > 0f)
            }

            fun calculatePerChildWidth(): Float {
                return (childrenNeedExtraWidth.getWidthSum() + calculateExtraHorizontalSpaceAvailable()) / (childrenNeedExtraWidth.count().toFloat())
            }
            fun getFixedChildrenExceedingWidthAllocation() = childrenNeedExtraWidth.filter { it.getFixedNodeWidth(context) >= calculatePerChildWidth() }


            // if the fixed width component of a child that needs extra width is larger than the horizontal space to allocate then dont
            // allocate any space to that child and remove that child's current width from the extra space available


            // calculate the horizontal space available
            var perChildHorizontalSpaceToAllocate = 0f

            childrenNeedExtraWidth = childrenNeedExtraWidth.filter { it !in getFixedChildrenExceedingWidthAllocation() }

            perChildHorizontalSpaceToAllocate = calculatePerChildWidth()

            // end of step 2, we have now calculated which children need extra width and the width to allocate to these children

            // 3 - This step measures the children that require extra width and keeps repeating this process until all
            // width has been allocated or children with this priority no longer require any extra width

            var childrenReceivedExtraWidthAllocation = listOf<TreeNode>()

            while (calculateExtraHorizontalSpaceAvailable() > 0f && childrenNeedExtraWidth.count() > 0 && childrenNeedExtraWidth != childrenReceivedExtraWidthAllocation) {
                childrenNeedExtraWidth.forEach {
                    it.clearSizeAndPositioning()
                    it.computeSize(context, Dimensions(height = heightForChildMeasure, width = Dimension.Value(perChildHorizontalSpaceToAllocate)))
                }

                childrenReceivedExtraWidthAllocation = childrenNeedExtraWidth

                childrenNeedExtraWidth = childrenNeedExtraWidth.filter {
                    it.getWidth() >= perChildHorizontalSpaceToAllocate
                            || it.containsTextThatRequiresHorizontalSpace()
                            || it in getFixedChildrenExceedingWidthAllocation() }.toMutableList()

                if (childrenNeedExtraWidth == childrenReceivedExtraWidthAllocation) {
                    childrenNeedExtraWidth = childrenNeedExtraWidth.filter { it.getWidth() >= perChildHorizontalSpaceToAllocate }
                }

                perChildHorizontalSpaceToAllocate = if (childrenNeedExtraWidth.count() > 0) {
                    childrenNeedExtraWidth = childrenNeedExtraWidth.filter { it !in getFixedChildrenExceedingWidthAllocation() }
                    calculatePerChildWidth()
                } else {
                    0f
                }
            }

            return calculateExtraHorizontalSpaceAvailable()
        } else {
            return  0f
        }
    }

    // get size for expand behavior children
    if (horizontalExpandChildren.count() > 0) {
        val expandChildrenGroupedByPriority = horizontalExpandChildren.groupBy { (it.value as Layer).determineLayoutPriority() }
        var availableWidth = if (widthConstraint is Dimension.Value) {
            (widthConstraint.value - totalSpacing - totalFixedWidth - horizontalPadding)
        } else {
            1f
        }
        val keys = expandChildrenGroupedByPriority.keys.sortedDescending()

        for (key in keys) {
            if (availableWidth > 0 || widthConstraint is Dimension.Inf) {
                var lowerPriorityFixedWidth = 0f

                expandChildrenGroupedByPriority.forEach {
                    if (it.key < key) {
                        it.value.forEach { treeNode ->
                            lowerPriorityFixedWidth += treeNode.getFixedNodeWidth(context)
                        }
                    }
                }
                availableWidth = calculateLayoutPriorityGroupSizing(if (keys.first() == key) availableWidth - lowerPriorityFixedWidth else availableWidth, (expandChildrenGroupedByPriority[key] ?: error("missing layout priority group")), lowerPriorityFixedWidth)
            } else {
                expandChildrenGroupedByPriority[key]?.forEach {
                    // still need to measure even if available width is 0
                    it.computeSize(context, Dimensions(height = heightForChildMeasure, width = Dimension.Value(it.getFixedNodeWidth(context))))
                }
            }
        }
    }

    // height calculation is different for baseline alignment compared to other alignments because it involves more than just the
    // height of the max child if childCount > 1.
    // if height above text baseline in child containing text > max height of other children height is the child with text
    // else height is height below text baseline plus max height of other children
    // if multiple children with text, child with text with highest baseline in y should be used for measurement
    val heightForBaselineAlignment = when {
        alignment == VerticalAlignment.FIRST_TEXT_BASELINE && treeNode.getFirstText() != null && treeNode.children.count() == 1 -> treeNode.children.first().getHeight()
        alignment == VerticalAlignment.FIRST_TEXT_BASELINE && treeNode.getFirstText() != null -> {

            val childrenContainingText = treeNode.children.filter { it.getFirstText() != null }
            childrenContainingText.forEach { (it.value as Layer).computePosition(context, it, FloatPoint(0f, 0f)) }
            val childWithHighestBaseline = childrenContainingText.maxByOrNull {
                val firstTextNode = it.getFirstText()!!
                val heightAboveTextBaseline  = firstTextNode.getY() + (firstTextNode.value as Text).firstBaselineToTopDistance
                val heightBelowTextBaseline = it.getHeight() - heightAboveTextBaseline
                heightBelowTextBaseline
            }

            val childWithTextHeight = childWithHighestBaseline!!.getHeight()
            val firstTextNode = childWithHighestBaseline.getFirstText()!!
            val heightAboveTextBaseline = firstTextNode.getY() + (firstTextNode.value as Text).firstBaselineToTopDistance
            val heightBelowTextBaseline = childWithTextHeight - heightAboveTextBaseline

            val maxChildHeightExcludingFirstTextNode = treeNode.children.filter { it.getFirstText() != firstTextNode }.maxOfOrNull {
                // height of node or if node containing text height above text baseline
                val childFirstText = it.getFirstText()
                if (childFirstText != null) {
                    childFirstText.getY() + (childFirstText.value as Text).firstBaselineToTopDistance
                } else {
                    it.getHeight()
                }
            } ?: 0f

            childrenContainingText.forEach { it.clearPositioning() }

            if (heightAboveTextBaseline >= maxChildHeightExcludingFirstTextNode) {
                childWithTextHeight
            } else {
                heightBelowTextBaseline + maxChildHeightExcludingFirstTextNode
            }
        }
        else -> 0f
    }

    // calculate height
    val maxChildHeight = if (heightForBaselineAlignment == 0f) treeNode.children.maxOfOrNull { it.getHeight() } ?: 0f else heightForBaselineAlignment
    treeNode.setHeight(maxChildHeight + verticalPadding)

    // calculate width
    var totalWidth = 0f
    treeNode.children.forEachIndexed { index, child ->
        totalWidth += child.getWidth()
        if (index != treeNode.children.lastIndex) totalWidth += spacing
    }

    val nodeWidth = when {
        pxFrame?.maxWidth != null && pxFrame.minWidth != null -> {
            when (pxFrame.maxWidth) {
                is MaxWidth.Finite -> medianOf(pxFrame.minWidth, totalWidth + horizontalPadding, pxFrame.maxWidth.value)
                else -> maxOf(pxFrame.minWidth, totalWidth + horizontalPadding)
            }
        }
        pxFrame?.minWidth != null -> maxOf(pxFrame.minWidth, totalWidth + horizontalPadding)
        pxFrame?.maxWidth != null -> {
            when (widthConstraint) {
                is Dimension.Value -> {
                    when (pxFrame.maxWidth) {
                        is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, widthConstraint.value)
                        is MaxWidth.Infinite -> maxOf(totalWidth + horizontalPadding, widthConstraint.value)
                    }
                }
                is Dimension.Inf -> {
                    when (pxFrame.maxWidth) {
                        is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, totalWidth + horizontalPadding)
                        is MaxWidth.Infinite -> totalWidth + horizontalPadding
                    }
                }
            }
        }
        frame.unboundedWidth() -> totalWidth + horizontalPadding
        else -> (widthConstraint as Dimension.Value).value
    }

    val nodeHeight = when {
        pxFrame?.maxHeight != null && pxFrame.minHeight != null -> {
            when (pxFrame.maxHeight) {
                is MaxHeight.Finite -> medianOf(pxFrame.minHeight, maxChildHeight + verticalPadding, pxFrame.maxHeight.value)
                else -> maxOf(pxFrame.minHeight, maxChildHeight + verticalPadding)
            }
        }
        pxFrame?.minHeight != null -> maxOf(pxFrame.minHeight, maxChildHeight + verticalPadding)
        pxFrame?.maxHeight != null -> {
            when (heightConstraint) {
                is Dimension.Value -> {
                    when (pxFrame.maxHeight) {
                        is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, heightConstraint.value)
                        is MaxHeight.Infinite -> maxOf(maxChildHeight + verticalPadding, heightConstraint.value)
                    }
                }
                is Dimension.Inf -> {
                    when (pxFrame.maxHeight) {
                        is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, maxChildHeight + verticalPadding)
                        is MaxHeight.Infinite -> maxChildHeight + verticalPadding
                    }
                }
            }
        }
        frame.unboundedHeight() -> maxChildHeight + verticalPadding
        else -> (heightConstraint as Dimension.Value).value
    }

    // set HStack size, content height/width is size of VStack. height and width are height and width including frame
    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
        width = nodeWidth,
        height = nodeHeight,
        contentHeight = maxChildHeight,
        contentWidth = totalWidth
    )

    background?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
}