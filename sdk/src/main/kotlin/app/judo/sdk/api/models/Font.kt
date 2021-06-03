package app.judo.sdk.api.models

import com.squareup.moshi.*

sealed class Font {

    @JsonClass(generateAdapter = true)
    data class Fixed(
        val weight: FontWeight,
        val size: Float,
        val isDynamic: Boolean,
    ) : Font()

    @JsonClass(generateAdapter = true)
    data class Dynamic(
        val textStyle: String,
        val isDynamic: Boolean,
        val emphases: Emphases,
    ) : Font()

    @JsonClass(generateAdapter = true)
    data class Custom(
        val size: Float,
        val fontName: String,
        val isDynamic: Boolean,
    ) : Font()

}
