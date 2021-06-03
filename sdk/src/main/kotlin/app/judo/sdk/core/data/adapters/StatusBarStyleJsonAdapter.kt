package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.StatusBarStyle
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class StatusBarStyleJsonAdapter {
    @ToJson
    fun toJson(statusBarStyle: StatusBarStyle) = statusBarStyle.code

    @FromJson
    fun fromJson(statusBarStyle: String) =
        StatusBarStyle.values().find { it.code == statusBarStyle } ?: throw RuntimeException("Incorrect code")
}