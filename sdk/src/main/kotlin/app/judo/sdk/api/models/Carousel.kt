package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Carousel(
    val childIDs: List<String>,
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val isLoopEnabled: Boolean,
    override val background: Background? = null,
    override val overlay: Overlay? = null,
    val opacity: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val padding: Padding? = null,
    override val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val aspectRatio: Float? = null,
    val mask: Node? = null,
    val accessibility: Accessibility? = null,
) : NodeContainer, Layer, Backgroundable, Overlayable {

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()
    val pxFramer = PXFramer(frame)
    override fun getChildNodeIDs() = childIDs

    @Transient
    override var maskPath: MaskPath? = null
    override val typeName = NodeType.CAROUSEL.code
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}
