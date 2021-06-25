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
import app.judo.sdk.api.models.Audio
import app.judo.sdk.api.models.MaxHeight
import app.judo.sdk.api.models.MaxWidth
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computeSingleNodeSize

internal fun Audio.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {
    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val audioHeight = 80.dp.toPx(context)
    val defaultAudioWidth = 300.dp.toPx(context)

    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)
    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)

    val heightConstraint: Dimension.Value = when(val intrinsicHeight = parentConstraints.height) {
        is Dimension.Inf -> {
            when {
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(audioHeight + verticalPadding, pxFrame.minHeight))
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, audioHeight + verticalPadding))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(audioHeight + verticalPadding)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                else -> Dimension.Value(audioHeight + verticalPadding)
            }
        }
        is Dimension.Value -> {
            when {
                pxFrame?.minHeight != null -> Dimension.Value(maxOf(audioHeight + verticalPadding, pxFrame.minHeight))
                pxFrame?.maxHeight is MaxHeight.Finite -> Dimension.Value(minOf(pxFrame.maxHeight.value, intrinsicHeight.value))
                pxFrame?.maxHeight is MaxHeight.Infinite -> Dimension.Value(intrinsicHeight.value)
                pxFrame?.height != null -> Dimension.Value(pxFrame.height)
                else -> Dimension.Value(audioHeight + verticalPadding)
            }
        }
    }

    val widthConstraint: Dimension.Value = when(val intrinsicWidth = parentConstraints.width) {
        is Dimension.Inf -> {
            when {
                pxFrame?.maxWidth is MaxWidth.Finite -> Dimension.Value(minOf(pxFrame.maxWidth.value, defaultAudioWidth + horizontalPadding))
                pxFrame?.maxWidth is MaxWidth.Infinite -> Dimension.Value(defaultAudioWidth + horizontalPadding)
                pxFrame?.width != null -> Dimension.Value(pxFrame.width)
                pxFrame?.minWidth != null -> Dimension.Value(maxOf(pxFrame.minWidth, defaultAudioWidth + horizontalPadding))
                else -> Dimension.Value(defaultAudioWidth + horizontalPadding)
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

    val width = widthConstraint.value
    val height = heightConstraint.value

    val contentWidth = width - (padding?.leading?.dp?.toPx(context) ?: 0f) - (padding?.trailing?.dp?.toPx(context) ?: 0f)

    this.sizeAndCoordinates = sizeAndCoordinates.copy(
        width = width,
        height = height,
        contentWidth = contentWidth,
        contentHeight = audioHeight
    )

    background?.node?.computeSingleNodeSize(context, treeNode, sizeAndCoordinates.width, sizeAndCoordinates.height)
    overlay?.node?.computeSingleNodeSize(context, treeNode, sizeAndCoordinates.width, sizeAndCoordinates.height)
}