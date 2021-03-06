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

package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.Alignment
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.ZStack
import app.judo.sdk.ui.extensions.adjustPositionForPadding
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun ZStack.computePosition(context: Context, treeNode: TreeNode, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

    val contentWidth = sizeAndCoordinates.contentWidth
    val contentHeight = sizeAndCoordinates.contentHeight

    // set child positions within stack
    treeNode.children.forEach {
        // set alignment
        when (alignment) {
            Alignment.TOP -> {
                val x = (contentWidth - it.getWidth()) / 2f
                // adding current child x and y in case child has a frame
                it.setY(0f + it.getY())
                it.setX(x + it.getX())
            }
            Alignment.TOP_LEADING -> {
                it.setX(0f + it.getX())
                it.setY(0f + it.getY())
            }
            Alignment.TOP_TRAILING -> {
                val x = contentWidth - it.getWidth()
                it.setX(x + it.getX())
                it.setY(0f + it.getY())
            }
            Alignment.BOTTOM -> {
                val x = ((contentWidth - it.getWidth()) / 2f)
                val y = contentHeight - it.getHeight()
                it.setX(x + it.getX())
                it.setY(y + it.getY())
            }
            Alignment.BOTTOM_LEADING -> {
                val y = contentHeight - it.getHeight()
                it.setX(0f + it.getX())
                it.setY(y + it.getY())
            }
            Alignment.BOTTOM_TRAILING -> {
                val x = contentWidth - it.getWidth()
                val y = contentHeight - it.getHeight()
                it.setX(x + it.getX())
                it.setY(y + it.getY())
            }
            Alignment.LEADING -> {
                val y = ((contentHeight - it.getHeight()) / 2f)
                it.setY(y + it.getY())
                it.setX(0f + it.getX())
            }
            Alignment.TRAILING -> {
                val y = ((contentHeight - it.getHeight()) / 2f)
                val x = contentWidth - it.getWidth()
                it.setY(y + it.getY())
                it.setX(x + it.getX())
            }
            Alignment.CENTER -> {
                it.setY(((contentHeight - it.getHeight()) / 2f) + it.getY())
                it.setX(((contentWidth - it.getWidth()) / 2f) + it.getX())
            }
        }
    }

    background?.node?.computeSingleNodeRelativePosition(this.sizeAndCoordinates.width, this.sizeAndCoordinates.height, background.alignment)
    overlay?.node?.computeSingleNodeRelativePosition(this.sizeAndCoordinates.width, this.sizeAndCoordinates.height, overlay.alignment)
    background?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))
    overlay?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))

    // set frame alignment
    setFrameAlignment()

    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    adjustPositionForPadding(context, padding)

    treeNode.children.forEach {
        (it.value as Layer).computePosition(context, it, FloatPoint(getX(), getY()))
    }
}
