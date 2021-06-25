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

import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Video(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val sourceURL: String,
    val resizingMode: VideoResizingMode,
    val posterImageURL: String?,
    val autoPlay: Boolean,
    val showControls: Boolean,
    val looping: Boolean,
    val removeAudio: Boolean,
    val padding: Padding? = null,
    val aspectRatio: Float? = null,
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
) : Layer, Backgroundable, Overlayable, Actionable, SupportsInterpolation, PlaysMedia {
    override val typeName = NodeType.VIDEO.code
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    @Transient
    override var maskPath: MaskPath? = null
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()
    val pxFramer = PXFramer(frame)


    @Transient
    override var interpolator: Interpolator? = null

    internal val interpolatedSourceURL: String
        get() {
            return interpolator?.interpolate(sourceURL) ?: sourceURL
        }

    internal val interpolatedPosterImageURL: String?
        get() {
            return posterImageURL?.let { interpolator?.interpolate(it) }
        }

}
