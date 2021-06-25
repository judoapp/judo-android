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

package app.judo.sdk.ui.extensions

import android.content.Context
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Layer
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computeSingleNodeCoordinates
import app.judo.sdk.ui.layout.composition.computeSingleNodeRelativePosition
import app.judo.sdk.ui.layout.composition.computeSingleNodeSize
import app.judo.sdk.ui.layout.composition.getX
import app.judo.sdk.ui.layout.composition.getY
import app.judo.sdk.ui.layout.composition.singleNodeClearSizeAndPositioning

// intersect current layer layout area with mask path to find the resulting path that can
// be drawn to
internal fun Layer.calculateDisplayableAreaFromMaskPath(context: Context): MaskPath? {
    if (maskPath != null) {
        val cornerRadius = if (this is Rectangle) this.cornerRadius.dp.toPx(context) else 0f
        val layerPath = Path().apply {
            addRoundRect(
                RectF(
                sizeAndCoordinates.x,
                sizeAndCoordinates.y,
                sizeAndCoordinates.x + sizeAndCoordinates.contentWidth,
                sizeAndCoordinates.y + sizeAndCoordinates.contentHeight
            ), cornerRadius, cornerRadius, Path.Direction.CW)
        }

        layerPath.op(maskPath!!.path, Path.Op.INTERSECT)

        layerPath.transform(Matrix().apply { setTranslate(-sizeAndCoordinates.x, -sizeAndCoordinates.y) })

        return MaskPath(layerPath, maskPath!!.opacity)
    } else {
        return null
    }
}

// set mask path to a layer and it's background and overlay
internal fun Layer.setMaskPath(maskPath: MaskPath? = this.maskPath) {
    this.maskPath = maskPath
    if (this is Backgroundable) (this.background?.node as? Layer)?.maskPath = this.maskPath
    if (this is Overlayable) (this.overlay?.node as? Layer)?.maskPath = this.maskPath
}

// combines previous mask path with mask to set new mask path of intersection on layer, layer background and layer overlay
internal fun Layer.setMaskPathFromMask(context: Context, mask: Node?, appearance: Appearance) {
    val previousPath: MaskPath? = this.maskPath
    val maskPath = if (mask as? Rectangle != null) calculateMaskPathFromMask(context, mask, appearance) else null
    this.maskPath = when {
        maskPath != null && previousPath != null -> {
            maskPath.path.op(previousPath.path, Path.Op.INTERSECT)
            MaskPath(maskPath.path, maskPath.opacity * previousPath.opacity)
        }
        maskPath != null -> maskPath
        previousPath != null -> previousPath
        else -> null
    }
    if (this is Backgroundable) (this.background as? Layer)?.maskPath = this.maskPath
    if (this is Overlayable) (this.overlay as? Layer)?.maskPath = this.maskPath
}

private fun getMaskOpacity(context: Context, mask: Rectangle, appearance: Appearance): Float {
    val opacity = mask.opacity
    val fillOpacity = when (val fill = mask.fill) {
        is Fill.FlatFill -> {
            val colorForEnvironment = when {
                context.isDarkMode(appearance) -> fill.color.darkMode ?: fill.color.darkModeHighContrast ?: fill.color.default
                else -> fill.color.default
            }
            colorForEnvironment.alpha
        }
        else -> 1f
    }
    return (opacity ?: 1f) * fillOpacity
}

// calculate a path for a given mask
private fun Layer.calculateMaskPathFromMask(context: Context, mask: Rectangle, appearance: Appearance): MaskPath {
    mask.computeSingleNodeSize(context, TreeNode(mask), sizeAndCoordinates.width, sizeAndCoordinates.height)
    mask.computeSingleNodeRelativePosition(sizeAndCoordinates.width, sizeAndCoordinates.height, alignment = Alignment.CENTER)
    mask.computeSingleNodeCoordinates(context, FloatPoint(getX(), getY()))
    val opacity = getMaskOpacity(context, mask, appearance)
    val maskPath = Path().apply {
        addRoundRect(
            RectF(
            mask.sizeAndCoordinates.x,
            mask.sizeAndCoordinates.y,
            mask.sizeAndCoordinates.x + mask.sizeAndCoordinates.contentWidth,
            mask.sizeAndCoordinates.y + mask.sizeAndCoordinates.contentHeight
        ), mask.cornerRadius.dp.toPx(context), mask.cornerRadius.dp.toPx(context), Path.Direction.CW)
    }
    mask.singleNodeClearSizeAndPositioning()
    return MaskPath(maskPath, opacity)
}