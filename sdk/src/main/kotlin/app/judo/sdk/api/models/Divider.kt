package app.judo.sdk.api.models

import android.content.Context
import android.graphics.Path
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Divider(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val backgroundColor: ColorVariants,
    val offset: Point? = null,
    val padding: Padding? = null,
    override val frame: Frame? = null,
    val layoutPriority: Float? = null
) : Layer {

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.DIVIDER.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()
    val pxFramer = PXFramer(frame)

    @Transient
    override var maskPath: MaskPath? = null

    fun getDividerHeight(context: Context) = 1.dp.toPx(context)
    fun getVerticalDividerWidth(context: Context) = 1.dp.toPx(context)
}
