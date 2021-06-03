package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.MaxWidth
import com.squareup.moshi.*

internal class MaxWidthJsonAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): MaxWidth {
        return when (val jsonValue = reader.readJsonValue()) {
            is Double -> MaxWidth.Finite(value = jsonValue.toFloat())
            is String -> MaxWidth.Infinite()
            else -> throw JsonDataException()
        }
    }

    @ToJson
    @Suppress("UNUSED_PARAMETER")
    fun toJson(writer: JsonWriter, value: MaxWidth?) {
        throw UnsupportedOperationException()
    }
}