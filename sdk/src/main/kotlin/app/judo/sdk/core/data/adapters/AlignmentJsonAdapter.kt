package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.Alignment
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class AlignmentJsonAdapter {
    @ToJson
    fun toJson(alignment: Alignment) = alignment.code

    @FromJson
    fun fromJson(alignment: String) =
        Alignment.values().find { it.code == alignment } ?: throw RuntimeException("Incorrect code")
}