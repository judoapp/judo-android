package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.TextTransform
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class TextTransformAdapter {
    @ToJson
    fun toJson(textTransform: TextTransform?) = textTransform?.code

    @FromJson
    fun fromJson(textTransform: String) =
        TextTransform.values().find { it.code == textTransform } ?: throw RuntimeException("Incorrect code")
}