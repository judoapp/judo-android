package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Padding(
    val top: Float = 0f,
    val leading: Float = 0f,
    val bottom: Float = 0f,
    val trailing: Float = 0f,
)

