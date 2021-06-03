package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.SegueStyle
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class SegueStyleAdapter {
    @ToJson
    fun toJson(segueStyle: SegueStyle) = segueStyle.code

    @FromJson
    fun fromJson(segueStyle: String) =
        SegueStyle.values().find { it.code == segueStyle } ?: throw RuntimeException("Incorrect code")
}