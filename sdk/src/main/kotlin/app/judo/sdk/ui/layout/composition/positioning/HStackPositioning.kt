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
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Layer
import app.judo.sdk.ui.extensions.adjustPositionForPadding
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun HStack.computePosition(context: Context, treeNode: TreeNode, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

    // set relative child positions
    val maxChildHeight = treeNode.children.maxOfOrNull { it.getHeight() } ?: 0f
    var totalWidth = 0f
    val spacing = spacing.dp.toPx(context)

    if (alignment == VerticalAlignment.FIRST_TEXT_BASELINE) {
        // calculate all child heights below text baseline in order to align other children to the text with the highest baseline
        val childrenContainingText = treeNode.children.filter { it.getFirstText() != null }
        childrenContainingText.forEach { (it.value as Layer).computePosition(context, it, FloatPoint(0f, 0f)) }
        val heightBelowTextBaselines = childrenContainingText.map {
            val firstTextNode = it.getFirstText()!!
            val heightAboveTextBaseline  = firstTextNode.getY() + (firstTextNode.value as Text).firstBaselineToTopDistance
            val heightBelowTextBaseline = it.getHeight() - heightAboveTextBaseline
            it.value.id to heightBelowTextBaseline
        }
        val childIdWithHighestBaseline = heightBelowTextBaselines.maxByOrNull { it.second }?.first
        val childWithHighestBaseline = treeNode.children.find { it.value.id == childIdWithHighestBaseline }

        childrenContainingText.forEach { it.clearPositioning() }

        treeNode.children.forEachIndexed { index, child ->
            child.setX(totalWidth + child.getX())
            // set alignment
            when {
                childWithHighestBaseline == null -> {
                    // align bottom
                    val y = maxChildHeight - child.getHeight()
                    child.setY(y + child.getY())
                }
                child == childWithHighestBaseline -> {
                    // align bottom
                    val y = this.sizeAndCoordinates.contentHeight - child.getHeight()
                    child.setY(y + child.getY())
                }
                child in childrenContainingText -> {
                    // align baseline to baseline
                    val heightBelowBaseline = heightBelowTextBaselines.find { it.first == child.value.id }!!.second
                    val heightBelowHighestChildBaseline = heightBelowTextBaselines.find { it.first == childWithHighestBaseline.value.id }!!.second
                    val y = this.sizeAndCoordinates.contentHeight - child.getHeight() - heightBelowHighestChildBaseline + heightBelowBaseline
                    child.setY(y + child.getY())
                }
                else -> {
                    // align bottom to baseline
                    val heightBelowHighestChildBaseline = heightBelowTextBaselines.find { it.first == childWithHighestBaseline.value.id }!!.second
                    val y = this.sizeAndCoordinates.contentHeight - child.getHeight() - heightBelowHighestChildBaseline
                    child.setY(y + child.getY())
                }
            }

            totalWidth += child.getWidth()
            if (index != treeNode.children.lastIndex) totalWidth += spacing
        }
    } else {
        treeNode.children.forEachIndexed { index, child ->
            child.setX(totalWidth + child.getX())

            // set alignment
            when (alignment) {
                // adding current child x and y in case child has a frame
                VerticalAlignment.TOP -> child.setY(0f + child.getY())
                VerticalAlignment.CENTER -> {
                    val y = (maxChildHeight - child.getHeight()) / 2f
                    child.setY(y + child.getY())
                }
                else -> {
                    val y = maxChildHeight - child.getHeight()
                    child.setY(y + child.getY())
                }
            }
            totalWidth += child.getWidth()
            if (index != treeNode.children.lastIndex) totalWidth += spacing
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
