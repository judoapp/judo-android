package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Metadata(
    val tags: Set<String> = emptySet(),
    val properties: Map<String, String> = emptyMap()
)
