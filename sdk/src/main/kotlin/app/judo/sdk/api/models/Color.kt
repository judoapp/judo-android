package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Color(
    val alpha: Float,
    val blue: Float,
    val green: Float,
    val red: Float
)