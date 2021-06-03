package app.judo.sdk.api.models

import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebView(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val url: String,
    val isScrollEnabled: Boolean,
    val aspectRatio: Float? = null,
    val padding: Padding? = null,
    override val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    override val background: Background? = null,
    override val overlay: Overlay? = null,
    val mask: Node? = null,
    val accessibility: Accessibility? = null
) : Layer, Backgroundable, Overlayable {

    override val typeName = NodeType.WEB.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)

    @Transient
    internal var interpolator: Interpolator? = null

    internal val interpolatedURL: String
        get() {
            return interpolator?.interpolate(url) ?: url
        }

}