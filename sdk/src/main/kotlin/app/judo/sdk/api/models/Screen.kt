package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Screen(
    override val id: String,
    override val metadata: Metadata? = null,
    val childIDs: List<String>,
    override val name: String? = null,
    val backgroundColor: ColorVariants,
    val statusBarStyle: StatusBarStyle,
    val appBar: AppBar? = null,
    override val frame: Frame? = null,
) : NodeContainer, Layer {

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    override val typeName = NodeType.SCREEN.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    @Transient
    override var maskPath: MaskPath? = null

    override fun determineLayoutPriority() = 0f

    override fun getChildNodeIDs() = childIDs
}
