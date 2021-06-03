package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradientVariants(
    val default: Gradient,
    val darkMode: Gradient? = null,
    val darkModeHighContrast: Gradient? = null,
    val highContrast: Gradient? = null,
)