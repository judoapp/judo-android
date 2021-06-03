package app.judo.sdk.ui.extensions

import android.content.Context
import android.graphics.Typeface
import app.judo.sdk.api.models.Font
import app.judo.sdk.api.models.FontWeight
import app.judo.sdk.core.utils.DynamicFontStyleMapper

internal fun Font.getSystemFontAttributes(context: Context): Font.Fixed {
    return when (this) {
        is Font.Fixed -> this
        is Font.Dynamic -> DynamicFontStyleMapper().resolveFontStyle(context.resources, this.textStyle)
        is Font.Custom -> Font.Fixed(size = this.size, weight = FontWeight.Regular, isDynamic = isDynamic)
    }
}

internal fun Font.getEmphasisStyle(): Int? {
    return when (this) {
        is Font.Dynamic -> {
            when {
                this.emphases.bold && this.emphases.italic -> Typeface.BOLD_ITALIC
                this.emphases.bold -> Typeface.BOLD
                this.emphases.italic -> Typeface.ITALIC
                else -> null
            }
        }
        else -> null
    }
}

internal data class FrameworkFont(val name: String, val style: Int)

internal fun FontWeight.mapToFont(): FrameworkFont {
    return when (this) {
        FontWeight.UltraLight -> FrameworkFont("sans-serif-thin", Typeface.NORMAL)
        FontWeight.Thin -> FrameworkFont("sans-serif-thin", Typeface.NORMAL)
        FontWeight.Light -> FrameworkFont("sans-serif-light", Typeface.NORMAL)
        FontWeight.Regular -> FrameworkFont("sans-serif", Typeface.NORMAL)
        FontWeight.Medium -> FrameworkFont("sans-serif-medium", Typeface.NORMAL)
        FontWeight.SemiBold -> FrameworkFont("sans-serif-medium", Typeface.NORMAL)
        FontWeight.Bold -> FrameworkFont("sans-serif", Typeface.BOLD)
        FontWeight.Heavy -> FrameworkFont("sans-serif", Typeface.BOLD)
        FontWeight.Black -> FrameworkFont("sans-serif-black", Typeface.NORMAL)
    }
}