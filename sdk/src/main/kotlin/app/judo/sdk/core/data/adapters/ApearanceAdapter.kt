package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.Appearance
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class AppearanceAdapter {
    @ToJson
    fun toJson(appearance: Appearance) = appearance.code

    @FromJson
    fun fromJson(appearance: String) =
        Appearance.values().find { it.code == appearance } ?: throw RuntimeException("Incorrect Appearance code")
}