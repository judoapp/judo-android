package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Spacer(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    override val frame: Frame? = null,
    val layoutPriority: Float? = null,
) : Layer {
    override val typeName = NodeType.SPACER.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    override fun determineLayoutPriority() = layoutPriority ?: -1f

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)
}