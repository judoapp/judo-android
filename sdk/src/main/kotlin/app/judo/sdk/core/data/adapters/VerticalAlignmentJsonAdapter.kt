package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.VerticalAlignment
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class VerticalAlignmentJsonAdapter {
    @ToJson
    fun toJson(alignment: VerticalAlignment) = alignment.code

    @FromJson
    fun fromJson(alignment: String) =
        VerticalAlignment.values().find { it.code == alignment } ?: throw RuntimeException("Incorrect code $alignment")
}