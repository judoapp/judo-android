package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.Axis
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class ScrollAxisJsonAdapter {
    @ToJson
    fun toJson(axis: Axis) = axis.code

    @FromJson
    fun fromJson(scrollAxis: String) =
        Axis.values().find { it.code == scrollAxis } ?: throw RuntimeException("Incorrect code")
}