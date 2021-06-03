package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.Point
import com.squareup.moshi.*

@Suppress("UNCHECKED_CAST")
internal class OffsetJsonAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Point {
        return when (val jsonValue = reader.readJsonValue()) {
            is List<*> -> {
                val paddingList = jsonValue as List<Int>
                Point(x = paddingList[0], y = paddingList[1])
            }
            else -> throw JsonDataException()
        }
    }

    @ToJson
    @Suppress("UNUSED_PARAMETER")
    fun toJson(writer: JsonWriter, value: Point?) {
        throw UnsupportedOperationException()
    }
}