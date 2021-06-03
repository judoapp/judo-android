package app.judo.sdk.api.models

import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.utils.Translator
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppBar(
    /**
     * Show or hide the up arrow button.
     */
    val showUpArrow: Boolean,
    /**
     * Overrides the default arrow icon.
     */
    val upArrowIconURL: String? = null,
    /**
     * Color for all the icons.
     */
    val iconColor: ColorVariants,
    /**
     * The title should be a signpost for the AppBar's current position in the navigation hierarchy
     * and the content contained there.
     */
    val title: String? = null,
    val titleFont: Font,
    val titleColor: ColorVariants,
    /**
     * Defaults to [Screen.backgroundColor]
     */
    val backgroundColor: ColorVariants,
    val menuItems: List<MenuItem>? = null,
) : Visitable {

    // Add support for colorizing all the icons at once
    // IconColor
    // Button Color
    // StatusBarColor can not be null
    // Talk to Andrew about accessibility

    @Transient
    internal var translator: Translator = Translator { it }

    @Transient
    internal var interpolator: Interpolator? = null

    internal val translatedTitle: String?
        get() = title?.let(translator::translate) ?: title

    internal val interpolatedTitle: String?
        get() {
            val translatedText = translatedTitle

            return translatedText?.let {
                interpolator?.interpolate(it)
            } ?: translatedText
        }

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}