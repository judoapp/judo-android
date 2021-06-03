package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScrollContainer(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val axis: Axis,
    val disableScrollBar: Boolean,
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
    val childIDs: List<String>,
) : Layer, NodeContainer, Backgroundable, Overlayable {

    override val typeName = NodeType.SCROLL_CONTAINER.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override fun getChildNodeIDs() = childIDs

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)
}