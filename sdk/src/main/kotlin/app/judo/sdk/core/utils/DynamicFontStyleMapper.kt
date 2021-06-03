package app.judo.sdk.core.utils

import android.content.res.Resources
import app.judo.sdk.api.models.Font
import app.judo.sdk.api.models.FontScale
import app.judo.sdk.api.models.FontStyle
import app.judo.sdk.api.models.FontWeight

internal class DynamicFontStyleMapper {
    fun resolveFontStyle(resources: Resources, textStyle: String): Font.Fixed {
        val fontScale = FontScale.retrieveFontScale(resources.configuration.fontScale)

        val fixedFont = when (FontStyle.getStyleFromCode(textStyle)) {
            FontStyle.LARGE_TITLE -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 31f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 34f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 36f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 40f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.TITLE_1 -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 25f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 28f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 30f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 34f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.TITLE_2 -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 19f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 22f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 24f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 28f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.TITLE_3 -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 17f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 20f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 22f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 26f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.HEADLINE -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 14f, weight = FontWeight.SemiBold, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 17f, weight = FontWeight.SemiBold, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 19f, weight = FontWeight.SemiBold, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 23f, weight = FontWeight.SemiBold, isDynamic = false)
            }
            FontStyle.BODY -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 14f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 17f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 19f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 23f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.CALLOUT -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 13f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 16f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 18f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 22f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.SUBHEADLINE -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 12f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 15f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 17f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 21f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.FOOTNOTE -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 12f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 13f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 15f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 19f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.CAPTION_1 -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 11f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 12f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 14f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 18f, weight = FontWeight.Regular, isDynamic = false)
            }
            FontStyle.CAPTION_2 -> when (fontScale) {
                FontScale.SMALL -> Font.Fixed(size = 11f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.DEFAULT -> Font.Fixed(size = 11f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGE -> Font.Fixed(size = 13f, weight = FontWeight.Regular, isDynamic = false)
                FontScale.LARGEST -> Font.Fixed(size = 17f, weight = FontWeight.Regular, isDynamic = false)
            }
        }

        return fixedFont
    }
}

