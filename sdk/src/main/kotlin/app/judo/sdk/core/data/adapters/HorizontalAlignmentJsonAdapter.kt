package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.HorizontalAlignment
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class HorizontalAlignmentJsonAdapter {
    @ToJson
    fun toJson(alignment: HorizontalAlignment) = alignment.code

    @FromJson
    fun fromJson(alignment: String) =
        HorizontalAlignment.values().find { it.code == alignment } ?: throw RuntimeException("Incorrect code")
}