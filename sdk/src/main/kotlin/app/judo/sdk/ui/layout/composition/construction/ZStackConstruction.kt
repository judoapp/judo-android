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

package app.judo.sdk.ui.layout.composition.construction

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.Screen
import app.judo.sdk.api.models.ZStack
import app.judo.sdk.ui.extensions.createRipple
import app.judo.sdk.ui.extensions.setMaskPath
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.findNearestAncestor
import app.judo.sdk.ui.layout.composition.toLayout
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceView
import kotlin.math.roundToInt

internal fun ZStack.construct(
    context: Context,
    treeNode: TreeNode,
    resolvers: Resolvers
): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)

    val judoStackFrame = ExperienceView(
        context, resolvers = resolvers
    ).apply {
        setWillNotDraw(true)
        id = View.generateViewId()
        shadow = this@construct.shadow
        isClickable = false
        alpha = opacity ?: 1f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        layoutParams = FrameLayout.LayoutParams(sizeAndCoordinates.contentWidth.roundToInt(), sizeAndCoordinates.contentHeight.roundToInt()).apply {
            setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
        }
    }

    this.action?.let { action ->
        judoStackFrame.foreground = createRipple(context, resolvers.statusBarColorResolver.color)
        judoStackFrame.setOnClickListener {
            treeNode.findNearestAncestor<Screen>()?.let { screen -> resolvers.actionResolver(action, screen, treeNode.value) }
        }
    }

    treeNode.children.forEach { (it.value as Layer).setMaskPath(maskPath) }
    val childViews = treeNode.children.reversed().flatMap { it.toLayout(context, resolvers)  }.toMutableList()

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)

    val constructedViews = listOfNotNull(background, judoStackFrame) + childViews + listOfNotNull(overlay)
    childViews.forEach { it.alpha = it.alpha * (opacity ?: 1f) }
    judoStackFrame.alpha = judoStackFrame.alpha * (opacity ?: 1f)

    return constructedViews
}
