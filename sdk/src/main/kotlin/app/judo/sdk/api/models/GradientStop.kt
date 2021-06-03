package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradientStop(
    val color: Color,
    val position: Double
)