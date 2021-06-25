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

internal fun Icon.computeSize(context: Context, parentConstraints: Dimensions) {

    // calculate height and width from frame and parent dimensions
    val pxFrame = pxFramer.getPixelFrame(context)

    val horizontalPadding = (padding?.leading?.dp?.toPx(context) ?: 0f) + (padding?.trailing?.dp?.toPx(context) ?: 0f)
    val verticalPadding = (padding?.top?.dp?.toPx(context) ?: 0f) + (padding?.bottom?.dp?.toPx(context) ?: 0f)

    val iconWidth = pointSize.dp.toPx(context)
    val iconHeight = pointSize.dp.toPx(context)

    val heightConstraint = when {
        pxFrame?.maxHeight is MaxHeight.Finite && parentConstraints.height is Dimension.Inf -> minOf(pxFrame.maxHeight.value, iconHeight + verticalPadding)
        pxFrame?.maxHeight is MaxHeight.Finite && parentConstraints.height is Dimension.Value -> minOf(pxFrame.maxHeight.value, parentConstraints.height.value)
        pxFrame?.maxHeight is MaxHeight.Infinite -> maxOf(iconHeight + verticalPadding, (parentConstraints.height as? Dimension.Value)?.value ?: 0f)
        pxFrame?.height != null -> pxFrame.height
        pxFrame?.minHeight != null -> maxOf(iconHeight + verticalPadding, pxFrame.minHeight)
        else -> iconHeight + verticalPadding
    }
    val widthConstraint = when {
        pxFrame?.maxWidth is MaxWidth.Finite && parentConstraints.width is Dimension.Inf -> minOf(pxFrame.maxWidth.value, iconWidth + horizontalPadding)
        pxFrame?.maxWidth is MaxWidth.Finite && parentConstraints.width is Dimension.Value -> minOf(pxFrame.maxWidth.value, parentConstraints.width.value)
        pxFrame?.maxWidth is MaxWidth.Infinite -> maxOf(iconWidth + horizontalPadding, (parentConstraints.width as? Dimension.Value)?.value ?: 0f)
        frame?.width != null -> pxFrame?.width!!
        frame?.minWidth != null -> maxOf(iconWidth + horizontalPadding, pxFrame?.minWidth!!)
        else -> iconWidth + horizontalPadding
    }

    this.sizeAndCoordinates = this.sizeAndCoordinates.copy(
        width = widthConstraint,
        height = heightConstraint,
        contentHeight = iconHeight,
        contentWidth = iconWidth
    )
}