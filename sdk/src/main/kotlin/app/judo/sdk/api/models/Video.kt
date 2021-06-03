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
    val action: Action? = null,
    val accessibility: Accessibility? = null,
) : Layer, Backgroundable, Overlayable {
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
    internal var interpolator: Interpolator? = null

    internal val interpolatedSourceURL: String
        get() {
            return interpolator?.interpolate(sourceURL) ?: sourceURL
        }

    internal val interpolatedPosterImageURL: String?
        get() {
            return posterImageURL?.let { interpolator?.interpolate(it) }
        }

}
