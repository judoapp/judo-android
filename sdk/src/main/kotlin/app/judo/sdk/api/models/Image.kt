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

package app.judo.sdk.api.models

import android.graphics.drawable.Drawable
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val imageURL: String,
    val darkModeImageURL: String? = null,
    val resolution: Float,
    val resizingMode: ResizingMode,
    val blurHash: String? = null,
    val darkModeBlurHash: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val darkModeImageWidth: Int? = null,
    val darkModeImageHeight: Int? = null,
    val padding: Padding? = null,
    override val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    override val background: Background? = null,
    override val overlay: Overlay? = null,
    val mask: Node? = null,
    override var action: Action? = null,
    val accessibility: Accessibility? = null,
) : Layer, Backgroundable, Overlayable, Actionable, SupportsInterpolation {

    constructor(
        id: String,
        name: String?,
        drawable: Drawable?,
        darkModeDrawable: Drawable? = null,
        resolution: Float,
        resizingMode: ResizingMode,
        imageWidth: Int? = null,
        imageHeight: Int? = null,
        darkModeImageWidth: Int?,
        darkModeImageHeight: Int?,
        padding: Padding? = null,
        frame: Frame? = null,
        layoutPriority: Float? = null,
        offset: Point? = null,
        shadow: Shadow? = null,
        opacity: Float? = null,
        background: Background? = null,
        overlay: Overlay? = null,
        mask: Node? = null,
        action: Action? = null,
        accessibility: Accessibility? = null,
        metadata: Metadata? = null
    ) : this(
        id,
        name,
        metadata,
        "",
        null,
        resolution,
        resizingMode,
        null,
        null,
        imageWidth,
        imageHeight,
        darkModeImageWidth,
        darkModeImageHeight,
        padding,
        frame,
        layoutPriority,
        offset,
        shadow,
        opacity,
        background,
        overlay,
        mask,
        action,
        accessibility
    ) {
        this.drawable = drawable
        this.darkModeDrawable = darkModeDrawable
    }

    @Transient
    var drawable: Drawable? = null

    @Transient
    var darkModeDrawable: Drawable? = null

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.IMAGE.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    internal fun hasImageDimensions(): Boolean {
       return ((imageHeight != null && imageWidth != null) || (darkModeImageHeight != null) && (darkModeImageWidth != null))
    }

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)

    @Transient
    override var interpolator: Interpolator? = null

    internal val interpolatedImageURL: String
        get() {
            return interpolator?.interpolate(imageURL) ?: imageURL
        }

    internal val interpolatedDarkModeImageURL: String?
        get() {
            return darkModeImageURL?.let { interpolator?.interpolate(it) }
        }
}