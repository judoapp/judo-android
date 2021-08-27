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
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import app.judo.sdk.api.models.Icon
import app.judo.sdk.api.models.ResizingMode
import app.judo.sdk.api.models.Screen
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.layout.Resolvers
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.findNearestAncestor
import app.judo.sdk.ui.views.ExperienceImageView
import kotlin.math.roundToInt

internal fun Icon.construct(context: Context, treeNode: TreeNode, resolvers: Resolvers): List<View> {
    setMaskPathFromMask(context, mask, treeNode.appearance)
    val maskPath = calculateDisplayableAreaFromMaskPath(context)

    fun createExperienceImageView(): ExperienceImageView {
        return ExperienceImageView(context, resolvers = resolvers, shadow = shadow, resizingMode = ResizingMode.ORIGINAL, maskPath = maskPath).apply {
            id = View.generateViewId()
            scaleType = ImageView.ScaleType.FIT_XY
            alpha = (opacity ?: 1f) * (maskPath?.opacity ?: 1f)
            isClickable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { forceHasOverlappingRendering(false) }
            layoutParams = FrameLayout.LayoutParams(
                sizeAndCoordinates.contentWidth.roundToInt(),
                sizeAndCoordinates.contentHeight.roundToInt()
            ).apply {
                setMargins(sizeAndCoordinates.x.roundToInt(), sizeAndCoordinates.y.roundToInt(), 0, 0)
            }
        }
    }

    val imageView = createExperienceImageView()

    val resourceId: Int = context.getMaterialIconID(icon.materialName)
    try {
        imageView.setImageDrawable(ResourcesCompat.getDrawable(context.resources, resourceId, null)
            ?.apply { setTint(resolvers.colorResolver.resolveForColorInt(color)) })

    } catch (e : Resources.NotFoundException) {
        Log.d("Named Icon $id", "icon ${icon.materialName} not found")
    }

    action?.let { action ->
        imageView.foreground = createRipple(context, resolvers.statusBarColorResolver.color)
        imageView.setOnClickListener {
            resolvers.actionResolver(action, treeNode.value)
        }
    }

    return listOfNotNull(imageView)
}