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
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.Screen
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computePosition
import app.judo.sdk.ui.layout.composition.getHeight
import app.judo.sdk.ui.layout.composition.getWidth

internal fun Screen.computePosition(context: Context, treeNode: TreeNode) {
    // set positions
    treeNode.children.forEach {
        if (it.getHeight() > sizeAndCoordinates.height) {
            val y = 0f
            val x = (treeNode.getWidth() - it.getWidth()) / 2f

            (it.value as Layer).computePosition(context, it, FloatPoint(x, y))
        } else {
            val y =  ((treeNode.getHeight() - it.getHeight()) / 2f)
            val x = (treeNode.getWidth() - it.getWidth()) / 2f

            (it.value as Layer).computePosition(context, it, FloatPoint(x, y))
        }
    }
}