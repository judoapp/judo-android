package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.ResizingMode
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class PatternTypeJsonAdapter {
    @ToJson
    fun toJson(resizingMode: ResizingMode) = resizingMode.code

    @FromJson
    fun fromJson(patternType: String) =
        ResizingMode.values().find { it.code == patternType } ?: throw RuntimeException("Incorrect code")
}