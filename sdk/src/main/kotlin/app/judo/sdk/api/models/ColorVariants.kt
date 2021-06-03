package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ColorVariants(
    val darkMode: Color? = null,
    val darkModeHighContrast: Color? = null,
    val default: Color,
    val highContrast: Color? = null
)