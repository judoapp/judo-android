package app.judo.sdk.api.models

import android.graphics.drawable.Drawable
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.ui.layout.composition.PXFramer
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val imageURL: String,
    val darkModeImageURL: String? = null,
    val resolution: Float,
    val resizingMode: ResizingMode,
    val blurHash: String? = null,
    val darkModeBlurHash: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val darkModeImageWidth: Int? = null,
    val darkModeImageHeight: Int? = null,
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
) : Layer, Backgroundable, Overlayable {

    constructor(
        id: String,
        name: String?,
        drawable: Drawable?,
        darkModeDrawable: Drawable? = null,
        resolution: Float,
        resizingMode: ResizingMode,
        imageWidth: Int? = null,
        imageHeight: Int? = null,
        darkModeImageWidth: Int?,
        darkModeImageHeight: Int?,
        padding: Padding? = null,
        frame: Frame? = null,
        layoutPriority: Float? = null,
        offset: Point? = null,
        shadow: Shadow? = null,
        opacity: Float? = null,
        background: Background? = null,
        overlay: Overlay? = null,
        mask: Node? = null,
        action: Action? = null,
        accessibility: Accessibility? = null,
        metadata: Metadata? = null
    ) : this(
        id,
        name,
        metadata,
        "",
        null,
        resolution,
        resizingMode,
        null,
        null,
        imageWidth,
        imageHeight,
        darkModeImageWidth,
        darkModeImageHeight,
        padding,
        frame,
        layoutPriority,
        offset,
        shadow,
        opacity,
        background,
        overlay,
        mask,
        action,
        accessibility
    ) {
        this.drawable = drawable
        this.darkModeDrawable = darkModeDrawable
    }

    @Transient
    var drawable: Drawable? = null

    @Transient
    var darkModeDrawable: Drawable? = null

    override fun determineLayoutPriority() = layoutPriority ?: 0f

    override val typeName = NodeType.IMAGE.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

    internal fun hasImageDimensions(): Boolean {
       return ((imageHeight != null && imageWidth != null) || (darkModeImageHeight != null) && (darkModeImageWidth != null))
    }

    override var sizeAndCoordinates: SizeAndCoordinates = SizeAndCoordinates()

    @Transient
    override var maskPath: MaskPath? = null
    val pxFramer = PXFramer(frame)

    @Transient
    internal var interpolator: Interpolator? = null

    internal val interpolatedImageURL: String
        get() {
            return interpolator?.interpolate(imageURL) ?: imageURL
        }

    internal val interpolatedDarkModeImageURL: String?
        get() {
            return darkModeImageURL?.let { interpolator?.interpolate(it) }
        }
}