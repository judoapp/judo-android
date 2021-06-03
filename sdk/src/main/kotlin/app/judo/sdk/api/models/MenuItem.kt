package app.judo.sdk.api.models

import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.utils.Translator
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItem(
    /**
     * A required property and should not be empty.
     */
    val title: String,
    // TODO: 2021-04-15 - Move this to the parent class
    val titleFont: Font,
    // TODO: 2021-04-15 - Move this to the parent class
    val titleColor: ColorVariants,
    val action: Action,
    val menuItemVisibility: MenuItemVisibility,
    /**
     * Either and SVG loaded from a URL or an [Icon]
     */
    val icon: MenuItemIcon? = null,
    val contentDescription: String? = null,
    val actionDescription: String? = null,
) : Visitable {

    @Transient
    internal var translator: Translator = Translator { it }

    internal val translatedTitle: String
        get() = translator.translate(title)

    internal val translatedContentDescription: String?
        get() = contentDescription?.let(translator::translate) ?: contentDescription

    internal val translatedActionDescription: String?
        get() = actionDescription?.let(translator::translate) ?: actionDescription

    @Transient
    internal var interpolator: Interpolator? = null

    internal val interpolatedTitle: String
        get() {
            return interpolator?.interpolate(translatedTitle) ?: translatedTitle
        }

    internal val interpolatedContentDescription: String?
        get() {
            return translatedContentDescription?.let {
                interpolator?.interpolate(it)
            } ?: translatedContentDescription
        }

    internal val interpolatedActionDescription: String?
        get() {
            return translatedActionDescription?.let {
                interpolator?.interpolate(it)
            } ?: translatedActionDescription
        }

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

}