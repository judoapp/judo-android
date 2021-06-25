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
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import app.judo.sdk.api.models.Color
import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.api.models.PageControl
import app.judo.sdk.api.models.PageControlStyle
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.calculateDisplayableAreaFromMaskPath
import app.judo.sdk.ui.extensions.setMaskPathFromMask
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.toSingleLayerLayout
import app.judo.sdk.ui.views.PageControlView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.roundToInt

internal fun PageControl.construct(
    context: Context,
    treeNode: TreeNode,
    resolvers: Resolvers
): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    val pageIndicatorColor = when (style) {
        is PageControlStyle.CustomPageControlStyle -> style.normalColor
        is PageControlStyle.DefaultPageControlStyle -> {
            ColorVariants(
                default = Color(0.3f, 0f, 0f, 0f),
                darkMode = Color(0.3f, 1f, 1f, 1f)
            )
        }
        is PageControlStyle.LightPageControlStyle -> ColorVariants(default = Color(0.3f, 1f, 1f, 1f))
        is PageControlStyle.DarkPageControlStyle -> ColorVariants(default = Color(0.3f, 0f, 0f, 0f))
        is PageControlStyle.InvertedPageControlStyle -> {
            ColorVariants(
                default = Color(0.3f, 1f, 1f, 1f),
                darkMode = Color(0.3f, 0f, 0f, 0f)
            )
        }
        is PageControlStyle.ImagePageControlStyle -> style.normalColor
    }

    val currentPageIndicatorColor = when (style) {
        is PageControlStyle.CustomPageControlStyle -> style.currentColor
        is PageControlStyle.DefaultPageControlStyle -> {
            ColorVariants(
                default = Color(1f, 0f, 0f, 0f),
                darkMode = Color(1f, 1f, 1f, 1f)
            )
        }
        is PageControlStyle.LightPageControlStyle -> ColorVariants(default = Color(1f, 1f, 1f, 1f), )
        is PageControlStyle.DarkPageControlStyle -> ColorVariants(default = Color(1f, 0f, 0f, 0f))
        is PageControlStyle.InvertedPageControlStyle -> {
            ColorVariants(
                default = Color(1f, 1f, 1f, 1f),
                darkMode = Color(1f, 0f, 0f, 0f)
            )
        }
        is PageControlStyle.ImagePageControlStyle -> style.currentColor
    }

    val pageControl = PageControlView(context, maskPath, imageIndicators = style is PageControlStyle.ImagePageControlStyle).apply {
        id = View.generateViewId()
        alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            forceHasOverlappingRendering(false)
        }
        layoutParams = FrameLayout.LayoutParams(
            sizeAndCoordinates.contentWidth.roundToInt(),
            sizeAndCoordinates.contentHeight.roundToInt()
        ).apply {
            setMargins(sizeAndCoordinates.x.toInt(), sizeAndCoordinates.y.toInt(), 0, 0)
        }
        this.shadow = this@construct.shadow
        this.resolvers = resolvers
        isClickable = false
        this.pageIndicatorColor = pageIndicatorColor
        this.currentPageIndicatorColor = currentPageIndicatorColor
        hidesForSinglePage = this@construct.hidesForSinglePage
        carouselID?.let { id ->
            doOnGlobalLayout {
                val viewPager = rootView.findViewWithTag<ViewPager2>(UUID.fromString(id))
                this.indicatorCount = viewPager?.getUniqueItemCount() ?: 0
                viewPager?.registerForChanges(this)
            }
        }
    }

    fun getImage(url: String, onComplete: (Drawable) -> Unit) {
        (context as LifecycleOwner).lifecycleScope.launch(Dispatchers.IO) {
            Environment.current.imageService
                .getImageAsync(ImageService.Request(url))
                .await().drawable?.let { drawable ->
                    withContext(Dispatchers.Main) {
                        onComplete(drawable)
                    }
                }
        }
    }

    if (style is PageControlStyle.ImagePageControlStyle) {
        getImage(style.currentImage.interpolatedImageURL) { drawable ->
            val bitmap = drawable.toBitmap()
            pageControl.currentItemBitmap = bitmap.scale(
                width = ((style.currentImage.imageWidth ?: 0).dp.toIntPx(context) / style.currentImage.resolution).toInt(),
                height = ((style.currentImage.imageHeight ?: 0).dp.toIntPx(context) / style.currentImage.resolution).toInt()
            )
        }
        getImage(style.normalImage.interpolatedImageURL) { drawable ->
            val bitmap = drawable.toBitmap()
            pageControl.normalItemBitmap = bitmap.scale(
                width = ((style.normalImage.imageWidth ?: 0).dp.toIntPx(context) / style.normalImage.resolution).toInt(),
                height = ((style.normalImage.imageHeight ?: 0).dp.toIntPx(context) / style.normalImage.resolution).toInt()
            )
        }
    }

    val background = this.background?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)
    val overlay = this.overlay?.node?.toSingleLayerLayout(context, treeNode, resolvers, this.maskPath)

    return listOfNotNull(background, pageControl, overlay)
}

private fun ViewPager2.registerForChanges(pageControlView: PageControlView) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pageControlView.currentlySelectedItem = position % getUniqueItemCount()
        }
    })
}

private fun ViewPager2.getUniqueItemCount() = (adapter as CarouselPagerAdapter).getUniqueItemCount()
