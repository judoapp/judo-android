package app.judo.sdk.api.models

import android.content.Context
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageControl(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val hidesForSinglePage: Boolean,
    val carouselID: String,
    val style: PageControlStyle,
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

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.PAGE_CONTROL.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)

    fun getPageControlHeight(context: Context) = 20.dp.toPx(context)
}
