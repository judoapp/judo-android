package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rectangle(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val fill: Fill,
    val cornerRadius: Float,
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
    val action: Action? = null,
    val accessibility: Accessibility? = null,
    val border: Border? = null,
) : Layer, Backgroundable, Overlayable {

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.RECTANGLE.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    @Transient
    override var maskPath: MaskPath? = null
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()
    val pxFramer = PXFramer(frame)
}
