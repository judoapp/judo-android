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
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Layer
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.setMaskPath
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.extensions.toIntPx
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.getAllLeafNodes
import app.judo.sdk.ui.layout.composition.toLayout
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.ExperienceMediaPlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.roundToInt

internal fun Carousel.construct(
    context: Context,
    treeNode: TreeNode,
    resolvers: Resolvers
): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    treeNode.children.forEach { (it.value as Layer).setMaskPath(maskPath) }

    val viewPager = ViewPager2(context).apply {
        id = View.generateViewId()
        tag = UUID.fromString(this@construct.id)
        alpha = opacity ?: 1f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 2
        adapter = CarouselPagerAdapter(context, resolvers, treeNode.children, isLoopEnabled, padding)
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.width.roundToInt(), sizeAndCoordinates.height.roundToInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
        }
    }

    val positionToMediaChildIDs = treeNode.children.mapIndexed { index, treeNodeChild ->
        index to treeNodeChild.getAllLeafNodes().filter { it is PlaysMedia }.map { it.id }
    }

    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            positionToMediaChildIDs.forEach { (nodePosition, mediaViewIDs) ->
                val selectedChild = position == nodePosition
                mediaViewIDs.forEach { mediaViewID ->
                    val mediaView = viewPager.findViewWithTag<ExperienceMediaPlayerView>(mediaViewID)
                    mediaView?.setupIfVisible(selectedChild)
                }
            }
        }
    })

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)

    return listOfNotNull(background, viewPager, overlay)
}

internal class CarouselPagerAdapter(private val context: Context, private val resolvers: Resolvers, val nodes: List<TreeNode>, private val loop: Boolean, private val padding: Padding?) : RecyclerView.Adapter<CarouselPagerAdapter.CarouselViewHolder>() {
    class CarouselViewHolder(container: FrameLayout) : RecyclerView.ViewHolder(container) {
        val container: FrameLayout
            get() = itemView as FrameLayout

        companion object {
            fun create(parent: ViewGroup, padding: Padding?): CarouselViewHolder {
                val container = FrameLayout(parent.context).apply {
                    id = ViewCompat.generateViewId()
                    clipChildren = true
                    clipToPadding = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        padding?.let {
                            setPadding(
                                it.leading.dp.toIntPx(context),
                                it.top.dp.toIntPx(context),
                                it.trailing.dp.toIntPx(context),
                                it.bottom.dp.toIntPx(context)
                            )
                        }
                    }
                }
                parent.clipChildren = true
                parent.clipToPadding = true
                parent.clipToOutline = true
                return CarouselViewHolder(container)
            }
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = CarouselViewHolder.create(viewGroup, padding)
    override fun onBindViewHolder(viewHolder: CarouselViewHolder, position: Int) {
        runBlocking(Dispatchers.Main.immediate) {
            val node = if (loop) nodes[position.rem(nodes.size)] else nodes[position]
            val nodes = node.toLayout(context, resolvers)
            viewHolder.container.removeAllViews()
            nodes.forEach { viewHolder.container.addView(it) }
        }
    }
    override fun getItemCount() = if (loop) Int.MAX_VALUE else nodes.size

    fun getUniqueItemCount() = nodes.size
}