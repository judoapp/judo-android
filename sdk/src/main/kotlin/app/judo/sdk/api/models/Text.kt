package app.judo.sdk.api.models

import android.graphics.Typeface
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.utils.Translator
import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Text(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val text: String,
    val font: Font,
    val textColor: ColorVariants,
    val textAlignment: TextAlignment,
    val lineLimit: Int? = null,
    val transform: TextTransform? = null,
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
    val skeleton: Boolean? = null,
    val visible: Boolean? = null,
    val accessibility: Accessibility? = null,
) : Layer, Backgroundable, Overlayable {

    @Transient
    internal var typeface: Typeface? = null

    @Transient
    internal var translator: Translator = Translator { it }

    @Transient
    internal var interpolator: Interpolator? = null

    internal val translation: String
        get() = translator.translate(text)

    internal val interpolatedText : String
        get() {
            val translatedText = translation

            return interpolator?.interpolate(translatedText) ?: translatedText
        }

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.TEXT.code
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
    var desiresWidth: Boolean = false
    var firstBaselineToTopDistance = 0f
    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)

}