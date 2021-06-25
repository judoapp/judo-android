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
import app.judo.sdk.api.models.Carousel
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.Layer
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun Carousel.computePosition(context: Context, treeNode: TreeNode, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

    val contentWidth = sizeAndCoordinates.contentWidth
    val contentHeight = sizeAndCoordinates.contentHeight

    treeNode.children.forEach {
        it.setY(((contentHeight - (it.getHeight())) / 2f) + it.getY())
        it.setX(((contentWidth - it.getWidth()) / 2f) + it.getX())
    }

    background?.node?.computeSingleNodeRelativePosition(
        this.sizeAndCoordinates.width,
        this.sizeAndCoordinates.height,
        background.alignment
    )
    overlay?.node?.computeSingleNodeRelativePosition(
        this.sizeAndCoordinates.width,
        this.sizeAndCoordinates.height,
        overlay.alignment
    )

    background?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))
    overlay?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))

    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    // set child positions to 0,0 because children are added to carousel container frame, not screen frame so should
    // be positioned relative to carousel frame
    treeNode.children.forEach {
        (it.value as Layer).computePosition(context, it, FloatPoint(0f, 0f))
    }
}
