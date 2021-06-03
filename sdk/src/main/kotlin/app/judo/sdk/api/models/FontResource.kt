package app.judo.sdk.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class FontResource {

    @JsonClass(generateAdapter = true)
    data class Single(
        val url: String,
        @Json(name = "fontName")
        val name: String
    ): FontResource()

    @JsonClass(generateAdapter = true)
    data class Collection(
        val url: String,
        @Json(name = "fontNames")
        val names: List<String>,
    ): FontResource()

}
