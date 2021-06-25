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
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.TreeNode

internal fun Divider.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)
    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)

    // determine if divider is horizontal or vertical
    if (treeNode.isNearestParentStackHorizontal()) {
        val defaultDividerWidth = getVerticalDividerWidth(context)

        val height: Dimension.Value = when(val intrinsicHeight = parentConstraints.height) {
            is Dimension.Inf -> {
                when {
                    pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(0f)
                    pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(0f)
                    frame?.height != null -> Dimension.Value(pxFrame?.height!!)
                    frame?.minHeight != null -> Dimension.Value(pxFrame?.minHeight!!)
                    else -> Dimension.Value(0f)
                }
            }
            is Dimension.Value -> {
                when {
                    pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                    pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                    frame?.height != null -> Dimension.Value(pxFrame?.height!!)
                    frame?.minHeight != null -> Dimension.Value(maxOf(intrinsicHeight.value, pxFrame?.minHeight!!))
                    else -> Dimension.Value(intrinsicHeight.value)
                }
            }
        }

        val width: Dimension.Value = when(val intrinsicWidth = parentConstraints.width) {
            is Dimension.Inf -> {
                when {
                    pxFrame?.minWidth != null -> Dimension.Value(maxOf(defaultDividerWidth + horizontalPadding, pxFrame.minWidth))
                    pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, defaultDividerWidth + horizontalPadding))
                    pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(defaultDividerWidth + verticalPadding)
                    pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                    else -> Dimension.Value(defaultDividerWidth + horizontalPadding)
                }
            }
            is Dimension.Value -> {
                when {
                    pxFrame?.minWidth != null -> Dimension.Value(maxOf(defaultDividerWidth + horizontalPadding, pxFrame.minWidth))
                    pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, intrinsicWidth.value))
                    pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(intrinsicWidth.value)
                    pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                    else -> Dimension.Value(defaultDividerWidth + verticalPadding)
                }
            }
        }

        this.sizeAndCoordinates = sizeAndCoordinates.copy(
            width = width.value,
            height = height.value,
            contentHeight = height.value - verticalPadding,
            contentWidth = getVerticalDividerWidth(context)
        )
    } else {
        val defaultDividerHeight = getDividerHeight(context)

        val width: Dimension.Value = when(val intrinsicWidth = parentConstraints.width) {
            is Dimension.Inf -> {
                when {
                    pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(0f)
                    pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(0f)
                    frame?.width != null -> Dimension.Value(pxFrame?.width!!)
                    frame?.minWidth != null -> Dimension.Value(pxFrame?.minWidth!!)
                    else -> Dimension.Value(0f)
                }
            }
            is Dimension.Value -> {
                when {
                    pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, intrinsicWidth.value))
                    pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(intrinsicWidth.value)
                    frame?.width != null -> Dimension.Value(pxFrame?.width!!)
                    frame?.minWidth != null -> Dimension.Value(maxOf(intrinsicWidth.value, pxFrame?.minWidth!!))
                    else -> Dimension.Value(intrinsicWidth.value)
                }
            }
        }

        val height: Dimension.Value = when(val intrinsicHeight = parentConstraints.height) {
            is Dimension.Inf -> {
                when {
                    pxFrame?.minHeight != null -> Dimension.Value(maxOf(defaultDividerHeight + verticalPadding, pxFrame.minHeight))
                    pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, defaultDividerHeight + verticalPadding))
                    pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(defaultDividerHeight + verticalPadding)
                    pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                    else -> Dimension.Value(defaultDividerHeight + verticalPadding)
                }
            }
            is Dimension.Value -> {
                when {
                    pxFrame?.minHeight != null -> Dimension.Value(maxOf(defaultDividerHeight + verticalPadding, pxFrame.minHeight))
                    pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                    pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                    pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                    else -> Dimension.Value(defaultDividerHeight + verticalPadding)
                }
            }
        }

        this.sizeAndCoordinates = sizeAndCoordinates.copy(
            width = width.value,
            height = height.value,
            contentHeight = getDividerHeight(context),
            contentWidth = width.value - horizontalPadding
        )
    }
}

internal fun TreeNode.isNearestParentStackHorizontal(): Boolean {
    var parent = this.parent
    while (parent != null) {
        when (parent.value) {
            is VStack -> return false
            is HStack -> return true
            else -> {
                parent = parent.parent
            }
        }
    }
    return false
}
