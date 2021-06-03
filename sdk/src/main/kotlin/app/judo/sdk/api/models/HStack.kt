package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HStack(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val spacing: Float,
    val alignment: VerticalAlignment,
    val padding: Padding? = null,
    override val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    override val overlay: Overlay? = null,
    val mask: Node? = null,
    val action: Action? = null,
    override val background: Background? = null,
    val border: Border? = null,
    val childIDs: List<String> = emptyList(),
) : NodeContainer, Layer, Backgroundable, Overlayable {

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.HSTACK.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    override fun getChildNodeIDs() = childIDs

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)
}

