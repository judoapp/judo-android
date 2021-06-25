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
import app.judo.sdk.ui.extensions.isDarkMode
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computeSingleNodeSize

internal fun Image.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {

    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)
    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)

    data class ContentDimensions(val width: Float, val height: Float)

    var imageWidth = if (darkModeImageWidth != null && context.isDarkMode(treeNode.appearance)) {
        darkModeImageWidth.dp.toPx(context)
    }  else {
        imageWidth?.dp?.toPx(context) ?: 0f
    }

    var imageHeight = if (darkModeImageHeight != null && context.isDarkMode(treeNode.appearance)) {
        darkModeImageHeight.dp.toPx(context)
    }  else {
        imageHeight?.dp?.toPx(context) ?: 0f
    }

    when (this.resizingMode) {
        ResizingMode.SCALE_TO_FIT -> {
            var parentWidthConstraint = when(val dimension = parentConstraints.width) {
                is Dimension.Inf -> 10000f
                is Dimension.Value -> dimension.value
            }

            var parentHeightConstraint = when(val dimension = parentConstraints.height) {
                is Dimension.Inf -> 10000f
                is Dimension.Value -> dimension.value
            }

            if (parentConstraints.width is Dimension.Inf && parentConstraints.height is Dimension.Inf) {
                parentHeightConstraint = imageHeight
                parentWidthConstraint = imageWidth
            }

            val heightConstraint = when {
                pxFrame?.maxHeight is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, parentHeightConstraint)
                pxFrame?.maxHeight is MaxHeight.Infinite -> parentHeightConstraint
                pxFrame?.height != null -> pxFrame.height
                pxFrame?.minHeight != null -> maxOf(parentHeightConstraint, pxFrame.minHeight)
                else -> parentHeightConstraint
            }

            val widthConstraint = when {
                pxFrame?.maxWidth is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, parentWidthConstraint)
                pxFrame?.maxWidth is MaxWidth.Infinite -> parentWidthConstraint
                pxFrame?.width != null -> pxFrame.width
                pxFrame?.minWidth != null -> maxOf(parentWidthConstraint, pxFrame.minWidth)
                else -> parentWidthConstraint
            }

            val horizontalImage = imageWidth / (widthConstraint - horizontalPadding) >= imageHeight / (heightConstraint - verticalPadding)

            val contentDimensions = if (horizontalImage) {
                ContentDimensions(widthConstraint - horizontalPadding, (imageHeight / imageWidth) * (widthConstraint - horizontalPadding))
            } else {
                ContentDimensions((heightConstraint - verticalPadding) / (imageHeight / imageWidth), heightConstraint - verticalPadding)
            }

            val nodeWidth = when {
                pxFrame?.minWidth != null -> maxOf(contentDimensions.width + horizontalPadding, pxFrame.minWidth)
                frame.unboundedWidth() -> contentDimensions.width + horizontalPadding
                pxFrame?.maxWidth is MaxWidth.Finite && parentConstraints.width is Dimension.Value -> minOf(pxFrame.maxWidth.value, parentConstraints.width.value)
                pxFrame?.maxWidth is MaxWidth.Finite -> minOf(contentDimensions.width + horizontalPadding, pxFrame.maxWidth.value)
                pxFrame?.maxWidth is MaxWidth.Infinite && parentConstraints.width is Dimension.Value -> maxOf(contentDimensions.width + horizontalPadding, widthConstraint)
                pxFrame?.maxWidth is MaxWidth.Infinite -> minOf(contentDimensions.width + horizontalPadding, widthConstraint)
                else -> widthConstraint
            }

            val nodeHeight = when {
                pxFrame?.minHeight != null -> maxOf(contentDimensions.height + verticalPadding, pxFrame.minHeight)
                frame.unboundedHeight() -> contentDimensions.height + verticalPadding
                pxFrame?.maxHeight is MaxHeight.Finite && parentConstraints.height is Dimension.Value -> minOf(pxFrame.maxHeight.value, parentConstraints.height.value)
                pxFrame?.maxHeight is MaxHeight.Finite -> minOf(contentDimensions.height + verticalPadding, pxFrame.maxHeight.value)
                pxFrame?.maxHeight is MaxHeight.Infinite && parentConstraints.height is Dimension.Value -> maxOf(contentDimensions.height + verticalPadding, heightConstraint)
                pxFrame?.maxHeight is MaxHeight.Infinite -> minOf(contentDimensions.height + verticalPadding, heightConstraint)
                else -> heightConstraint
            }

            this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                width = nodeWidth,
                height = nodeHeight,
                contentHeight = contentDimensions.height,
                contentWidth = contentDimensions.width
            )
        }
        ResizingMode.SCALE_TO_FILL -> {
            val parentWidthConstraint = when(val dimension = parentConstraints.width) {
                is Dimension.Inf -> 1f
                is Dimension.Value -> dimension.value
            }

            val parentHeightConstraint = when(val dimension = parentConstraints.height) {
                is Dimension.Inf -> 1f
                is Dimension.Value -> dimension.value
            }

            val heightConstraint = when {
                pxFrame?.maxHeight is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, parentHeightConstraint)
                pxFrame?.maxHeight is MaxHeight.Infinite -> parentHeightConstraint
                pxFrame?.height != null -> pxFrame.height
                pxFrame?.minHeight != null -> maxOf(parentHeightConstraint, pxFrame.minHeight)
                else -> parentHeightConstraint
            }

            val widthConstraint = when {
                pxFrame?.maxWidth is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, parentWidthConstraint)
                pxFrame?.maxWidth is MaxWidth.Infinite -> parentWidthConstraint
                frame?.width != null -> pxFrame?.width!!
                frame?.minWidth != null -> maxOf(parentWidthConstraint, pxFrame?.minWidth!!)
                else -> parentWidthConstraint
            }

            val horizontalImage = imageWidth / (widthConstraint - horizontalPadding) >= imageHeight / (heightConstraint - verticalPadding)

            val contentDimensions = if (horizontalImage) {
                ContentDimensions(((heightConstraint - verticalPadding) / (imageHeight / imageWidth)), heightConstraint - verticalPadding)
            } else {
                ContentDimensions(widthConstraint - horizontalPadding, ((imageHeight / imageWidth) * (widthConstraint - horizontalPadding)))
            }

            this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                width = widthConstraint,
                height = heightConstraint,
                contentHeight = minOf(heightConstraint - verticalPadding, contentDimensions.height),
                contentWidth = minOf(widthConstraint - horizontalPadding, contentDimensions.width)
            )
        }
        ResizingMode.TILE -> {
            val parentWidthConstraint = when(val dimension = parentConstraints.width) {
                is Dimension.Inf -> 1f
                is Dimension.Value -> dimension.value
            }

            val parentHeightConstraint = when(val dimension = parentConstraints.height) {
                is Dimension.Inf -> 1f
                is Dimension.Value -> dimension.value
            }

            val heightConstraint = when {
                pxFrame?.maxHeight is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, parentHeightConstraint)
                pxFrame?.maxHeight is MaxHeight.Infinite -> parentHeightConstraint
                pxFrame?.height != null -> pxFrame.height
                pxFrame?.minHeight != null -> maxOf(parentHeightConstraint, pxFrame.minHeight)
                else -> parentHeightConstraint
            }
            val widthConstraint = when {
                pxFrame?.maxWidth is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, parentWidthConstraint)
                pxFrame?.maxWidth is MaxWidth.Infinite -> parentWidthConstraint
                frame?.width != null -> pxFrame?.width!!
                frame?.minWidth != null -> maxOf(parentWidthConstraint, pxFrame?.minWidth!!)
                else -> parentWidthConstraint
            }

            this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                width = widthConstraint,
                height = heightConstraint,
                contentHeight = heightConstraint - verticalPadding,
                contentWidth = widthConstraint - horizontalPadding
            )
        }
        ResizingMode.STRETCH -> {
            val parentWidthConstraint = when(val dimension = parentConstraints.width) {
                is Dimension.Inf -> imageWidth
                is Dimension.Value -> dimension.value
            }

            val parentHeightConstraint = when(val dimension = parentConstraints.height) {
                is Dimension.Inf -> imageHeight
                is Dimension.Value -> dimension.value
            }

            val heightConstraint = when {
                pxFrame?.maxHeight is MaxHeight.Finite -> minOf(pxFrame.maxHeight.value, parentHeightConstraint)
                pxFrame?.maxHeight is MaxHeight.Infinite -> parentHeightConstraint
                pxFrame?.height != null -> pxFrame.height
                pxFrame?.minHeight != null -> maxOf(parentHeightConstraint, pxFrame.minHeight)
                else -> parentHeightConstraint
            }
            val widthConstraint = when {
                pxFrame?.maxWidth is MaxWidth.Finite -> minOf(pxFrame.maxWidth.value, parentWidthConstraint)
                pxFrame?.maxWidth is MaxWidth.Infinite -> parentWidthConstraint
                frame?.width != null -> pxFrame?.width!!
                frame?.minWidth != null -> maxOf(parentWidthConstraint, pxFrame?.minWidth!!)
                else -> parentWidthConstraint
            }

            this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                width = widthConstraint,
                height = heightConstraint,
                contentHeight = heightConstraint - verticalPadding,
                contentWidth = widthConstraint - horizontalPadding
            )
        }
        ResizingMode.ORIGINAL -> {
            imageHeight /= resolution
            imageWidth /= resolution

            val heightConstraint = when {
                pxFrame?.maxHeight is MaxHeight.Finite && parentConstraints.height is Dimension.Inf -> minOf(pxFrame.maxHeight.value, imageHeight + verticalPadding)
                pxFrame?.maxHeight is MaxHeight.Finite && parentConstraints.height is Dimension.Value -> minOf(pxFrame.maxHeight.value, parentConstraints.height.value)
                pxFrame?.maxHeight is MaxHeight.Infinite -> maxOf(imageHeight + verticalPadding, (parentConstraints.height as? Dimension.Value)?.value ?: 0f)
                pxFrame?.height != null -> pxFrame.height
                pxFrame?.minHeight != null -> maxOf(imageHeight + verticalPadding, pxFrame.minHeight)
                else -> imageHeight + verticalPadding
            }
            val widthConstraint = when {
                pxFrame?.maxWidth is MaxWidth.Finite && parentConstraints.width is Dimension.Inf -> minOf(pxFrame.maxWidth.value, imageWidth + horizontalPadding)
                pxFrame?.maxWidth is MaxWidth.Finite && parentConstraints.width is Dimension.Value -> minOf(pxFrame.maxWidth.value, parentConstraints.width.value)
                pxFrame?.maxWidth is MaxWidth.Infinite -> maxOf(imageWidth + horizontalPadding, (parentConstraints.width as? Dimension.Value)?.value ?: 0f)
                frame?.width != null -> pxFrame?.width!!
                frame?.minWidth != null -> maxOf(imageWidth + horizontalPadding, pxFrame?.minWidth!!)
                else -> imageWidth + horizontalPadding
            }

            this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
                width = if (frame.unboundedWidth()) imageWidth + horizontalPadding else widthConstraint,
                height = if (frame.unboundedHeight()) imageHeight + verticalPadding else heightConstraint,
                contentHeight = imageHeight,
                contentWidth = imageWidth
            )
        }
    }

    background?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, this.sizeAndCoordinates.width, this.sizeAndCoordinates.height)
}
