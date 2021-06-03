package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Gradient(
    val from: List<Float>,
    val to: List<Float>,
    val stops: List<GradientStop>
)

