package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.medianOf
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun VStack.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
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

    // measure all fixed height children
    treeNode.children.filter { it.verticalBehavior() == ViewBehavior.WRAP }.forEach {
        val sizeAndCoordinates = (it.value as Layer).sizeAndCoordinates
        val heightForMeasure = if (sizeAndCoordinates.height == 0f) heightForChildMeasure else Dimension.Value(sizeAndCoordinates.height)
        it.computeSize(context, Dimensions(height = heightForMeasure, width = widthForChildMeasure))
    }

    val totalFixedHeight = treeNode.children.filter { it.verticalBehavior() == ViewBehavior.WRAP }.sumOf { it.getHeight().toDouble() }.toFloat()

    val verticalExpandChildren = treeNode.children.filter { it.verticalBehavior() == ViewBehavior.EXPAND_FILL }
    verticalExpandChildren.forEach { it.clearSizeAndPositioning() }


    fun calculateLayoutPriorityGroupSizing(totalVerticalSpace: Float, priorityGroup: List<TreeNode>, lowerPriorityGroupFixedHeight: Float): Float {
        if (heightConstraint is Dimension.Inf) {
            priorityGroup.forEach {
                it.computeSize(context, Dimensions(height = Dimension.Inf, width = widthForChildMeasure))
            }
        }

        if (heightConstraint is Dimension.Value) {
            // 1 - Initial step which calculates the size of expand behavior children (which fill to expand available space)
            // splitting the available height equally, some children have a fixed height component (minHeight, frame height)
            // if the fixed height component of a child is greater than the space equally split then this child is not allocated any space
            var maxHeightPerChild = Dimension.Value((totalVerticalSpace) / (priorityGroup.count().toFloat()))

            var groupCount = priorityGroup.count().toFloat()
            var verticalSpace = totalVerticalSpace
            val fixedHeightTooLargeGroup  = mutableListOf<TreeNode>()
            var lastVerticalSpace = 0f

            while (lastVerticalSpace != verticalSpace) {
                lastVerticalSpace = verticalSpace
                priorityGroup.forEach {
                    if (it.getFixedNodeHeight(context) > maxHeightPerChild.value && it !in fixedHeightTooLargeGroup) {
                        groupCount -= 1f
                        verticalSpace -= it.getFixedNodeHeight(context)
                        fixedHeightTooLargeGroup.add(it)
                    }
                }
                maxHeightPerChild = Dimension.Value(verticalSpace / groupCount)
            }

            maxHeightPerChild = Dimension.Value(verticalSpace / groupCount)

            fixedHeightTooLargeGroup.forEach {
                it.computeSize(context, Dimensions(height = Dimension.Value(it.getFixedNodeHeight(context)), width = widthForChildMeasure))
            }

            priorityGroup.filter { it !in fixedHeightTooLargeGroup }.forEach {
                it.computeSize(context, Dimensions(height = maxHeightPerChild, width = widthForChildMeasure))
            }

            setHeight(heightConstraint.value)

            // end of step 1

            // 2 - This step calculates if we have any height available to split between children that require extra height
            // the available height comes from children in the previous step that don't fill the height allocated to them
            // examples of children with this behaviour are children with max heights

            fun List<TreeNode>.getHeightSum() = this.sumOf { (it.getHeight().toDouble()) }.toFloat()
            fun calculateExtraVerticalSpaceAvailable(): Float {
                return treeNode.getHeight() - totalSpacing - lowerPriorityGroupFixedHeight - (treeNode.children.getHeightSum()) - verticalPadding
            }

            // calculate which children need any extra height
            var childrenNeedExtraHeight = priorityGroup.filter {
                (it.getHeight() + 0.01f >= maxHeightPerChild.value)
                        || (fixedHeightTooLargeGroup.size == priorityGroup.size && calculateExtraVerticalSpaceAvailable() > 0f)
            }

            fun calculatePerChildHeight(): Float {
                return (childrenNeedExtraHeight.getHeightSum() + calculateExtraVerticalSpaceAvailable()) / (childrenNeedExtraHeight.count().toFloat())
            }
            fun getFixedChildrenExceedingHeightAllocation() = childrenNeedExtraHeight.filter { it.getFixedNodeHeight(context) >= calculatePerChildHeight() }

            // if the fixed height component of a child that needs extra height is larger than the vertical space to allocate then dont
            // allocate any space to that child and remove that child's current height from the extra space available


            // calculate the vertical space available
            var perChildVerticalSpaceToAllocate = 0f

            childrenNeedExtraHeight = childrenNeedExtraHeight.filter { it !in getFixedChildrenExceedingHeightAllocation() }

            perChildVerticalSpaceToAllocate = calculatePerChildHeight()

            // end of step 2, we have now calculated which children need extra height and the height to allocate to these children

            // 3 - This step measures the children that require extra height and keeps repeating this process until all
            // height has been allocated or children with this priority no longer require any extra height

            var childrenReceivedExtraHeightAllocation = listOf<TreeNode>()

            while (calculateExtraVerticalSpaceAvailable() > 0f && childrenNeedExtraHeight.count() > 0 && childrenNeedExtraHeight != childrenReceivedExtraHeightAllocation) {
                childrenNeedExtraHeight.forEach {
                    it.clearSizeAndPositioning()
                    it.computeSize(context, Dimensions(height = Dimension.Value(perChildVerticalSpaceToAllocate), width = widthForChildMeasure))
                }

                childrenReceivedExtraHeightAllocation = childrenNeedExtraHeight

                childrenNeedExtraHeight = childrenNeedExtraHeight.filter {
                    it.getHeight() >= perChildVerticalSpaceToAllocate
                            || it in getFixedChildrenExceedingHeightAllocation() }.toMutableList()

                perChildVerticalSpaceToAllocate = if (childrenNeedExtraHeight.count() > 0) {
                    childrenNeedExtraHeight = childrenNeedExtraHeight.filter { it !in getFixedChildrenExceedingHeightAllocation() }
                    calculatePerChildHeight()
                } else {
                    0f
                }
            }

            return calculateExtraVerticalSpaceAvailable()
        } else {
            return  0f
        }
    }

    // get size for expand behavior children
    if (verticalExpandChildren.count() > 0) {
        val expandChildrenGroupedByPriority = verticalExpandChildren.groupBy { (it.value as Layer).determineLayoutPriority() }
        var availableHeight = if (heightConstraint is Dimension.Value) {
            (heightConstraint.value - totalSpacing - totalFixedHeight - verticalPadding)
        } else {
            1f
        }
        val keys = expandChildrenGroupedByPriority.keys.sortedDescending()

        for (key in keys) {
            if (availableHeight > 0 || heightConstraint is Dimension.Inf) {
                var lowerPriorityFixedHeight = 0f

                expandChildrenGroupedByPriority.forEach {
                    if (it.key < key) {
                        it.value.forEach { treeNode ->
                            lowerPriorityFixedHeight += treeNode.getFixedNodeHeight(context)
                        }
                    }
                }
                availableHeight = calculateLayoutPriorityGroupSizing(if (keys.first() == key) availableHeight - lowerPriorityFixedHeight else availableHeight, (expandChildrenGroupedByPriority[key] ?: error("missing layout priority group")), lowerPriorityFixedHeight)
            } else {
                expandChildrenGroupedByPriority[key]?.forEach {
                    // still need to measure even if available width is 0
                    it.computeSize(context, Dimensions(height = Dimension.Value(it.getFixedNodeHeight(context)), width = widthForChildMeasure))
                }
            }
        }
    }

    // calculate width
    val maxChildWidth = treeNode.children.maxOfOrNull { it.getWidth() } ?: 0f
    treeNode.setWidth(maxChildWidth + horizontalPadding)

    // calculate height
    var totalHeight = 0f
    treeNode.children.forEachIndexed { index, child ->
        totalHeight += child.getHeight()
        if (index != treeNode.children.lastIndex) totalHeight += spacing
    }

    val nodeWidth = when {
        pxFrame?.maxWidth != null && pxFrame.minWidth != null -> {
            when (pxFrame.maxWidth) {
                is MaxWidth.Finite -> medianOf(pxFrame.minWidth, maxChildWidth + horizontalPadding, pxFrame.maxWidth.value)
                else -> maxOf(pxFrame.minWidth, maxChildWidth + horizontalPadding)
            }
        }
        pxFrame?.minWidth != null -> maxOf(pxFrame.minWidth, maxChildWidth + horizontalPadding)
        pxFrame?.maxWidth != null -> {
            when (widthConstraint) {
                is Dimension.Value -> {
                    when (pxFrame.maxWidth) {
                        is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, widthConstraint.value)
                        is MaxWidth.Infinite -> maxOf(maxChildWidth + horizontalPadding, widthConstraint.value)
                    }
                }
                is Dimension.Inf -> {
                    when (pxFrame.maxWidth) {
                        is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, maxChildWidth + horizontalPadding)
                        is MaxWidth.Infinite -> maxChildWidth + horizontalPadding
                    }
                }
            }
        }
        frame.unboundedWidth() -> maxChildWidth + horizontalPadding
        else -> (widthConstraint as Dimension.Value).value
    }

    val nodeHeight = when {
        pxFrame?.maxHeight != null && pxFrame.minHeight != null -> {
            when (pxFrame.maxHeight) {
                is MaxHeight.Finite -> medianOf(pxFrame.minHeight, totalHeight + verticalPadding, pxFrame.maxHeight.value)
                else -> maxOf(pxFrame.minHeight, totalHeight + verticalPadding)
            }
        }
        pxFrame?.minHeight != null -> maxOf(pxFrame.minHeight, totalHeight + verticalPadding)
        pxFrame?.maxHeight != null -> {
            when (heightConstraint) {
                is Dimension.Value -> {
                    when (pxFrame.maxHeight) {
                        is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, heightConstraint.value)
                        is MaxHeight.Infinite -> maxOf(totalHeight + verticalPadding, heightConstraint.value)
                    }
                }
                is Dimension.Inf -> {
                    when (pxFrame.maxHeight) {
                        is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, totalHeight + verticalPadding)
                        is MaxHeight.Infinite -> totalHeight + verticalPadding
                    }
                }
            }
        }
        frame.unboundedHeight() -> totalHeight + verticalPadding
        else -> (heightConstraint as Dimension.Value).value
    }

    // judo stacks have a behaviour where with infinite parent constraints they initially measure then remeasure with
    // fixed constraints, the result of this is that nodes that usually would have a size of 0 (rectangles with inf constraints) receive any
    // extra size not allocated to nodes initially sized with infinite constraints
    //TODO: 2021-02-12 - Only do this when the vstack contains views that require this it's really bad for perf
   if (parentConstraints.height !is Dimension.Value || parentConstraints.width !is Dimension.Value) {
       val widthForMeasure = if (parentConstraints.width is Dimension.Inf) {
           Dimension.Value(nodeWidth)
       } else {
           parentConstraints.width as Dimension.Value
       }

       val heightForMeasure = if (parentConstraints.height is Dimension.Inf) {
           Dimension.Value(nodeHeight)
       } else {
           parentConstraints.height as Dimension.Value
       }

       computeSize(context, treeNode, Dimensions(widthForMeasure, heightForMeasure))
       return
   }

    // set VStack size, content height/width is size of VStack. height and width are height and width including frame
    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
        width = nodeWidth,
        height = nodeHeight,
        contentHeight = totalHeight,
        contentWidth = maxChildWidth
    )

    background?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
}