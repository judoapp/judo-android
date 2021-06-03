package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.TextAlignment
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class TextAlignmentJsonAdapter {
    @ToJson
    fun toJson(alignment: TextAlignment) = alignment.code

    @FromJson
    fun fromJson(alignment: String) =
        TextAlignment.values().find { it.code == alignment } ?: throw RuntimeException("Incorrect code")
}