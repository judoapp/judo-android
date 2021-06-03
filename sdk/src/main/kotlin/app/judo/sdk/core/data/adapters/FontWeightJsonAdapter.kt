package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.FontWeight
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class FontWeightJsonAdapter {
    @ToJson
    fun toJson(fontWeight: FontWeight) = fontWeight.code

    @FromJson
    fun fromJson(fontWeight: String) =
        FontWeight.values().find { it.code.equals(fontWeight, true) } ?: throw RuntimeException("Incorrect code")
}