package app.judo.sdk.core.utils

import app.judo.sdk.api.models.FontWeight

internal sealed class FontAttributes {

    data class Fixed(
        val size: Float,
        val weight: FontWeight,
        val lineHeight: Float? = null
    ) : FontAttributes()

    data class Dynamic(
        val fontStyle: String
    ) : FontAttributes()

}