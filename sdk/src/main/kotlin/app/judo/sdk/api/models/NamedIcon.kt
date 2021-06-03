package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NamedIcon(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val icon: Icon,
    val color: ColorVariants,
    val pointSize: Int,
    val padding: Padding? = null,
    override val frame: Frame? = null,
    val layoutPriority: Float? = null,
    val offset: Point? = null,
    val shadow: Shadow? = null,
    val opacity: Float? = null,
    val mask: Node? = null,
    val action: Action? = null,
    val accessibility: Accessibility? = null,
    val border: Border? = null,
) : Layer {

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.NAMED_ICON.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    @Transient
    override var maskPath: MaskPath? = null
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()
    val pxFramer = PXFramer(frame)
}