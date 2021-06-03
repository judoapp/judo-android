package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.MaxHeight
import com.squareup.moshi.*

internal class MaxHeightJsonAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): MaxHeight {
        return when (val jsonValue = reader.readJsonValue()) {
            is Double -> MaxHeight.Finite(value = jsonValue.toFloat())
            is String -> MaxHeight.Infinite()
            else -> throw JsonDataException()
        }
    }

    @ToJson
    @Suppress("UNUSED_PARAMETER")
    fun toJson(writer: JsonWriter, value: MaxHeight?) {
        throw UnsupportedOperationException()
    }
}