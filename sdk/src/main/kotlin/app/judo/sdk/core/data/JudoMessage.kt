package app.judo.sdk.core.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JudoMessage(
    val action: String
)
