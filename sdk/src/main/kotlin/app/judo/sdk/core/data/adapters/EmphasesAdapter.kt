package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.Emphases
import com.squareup.moshi.*

@Suppress("UNCHECKED_CAST")
internal class EmphasesAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Emphases {
        return when (val jsonValue = reader.readJsonValue()) {
            is List<*> -> {
                val emphasis = jsonValue as List<String>
                var bold = false
                var italic = false

                emphasis.forEach {
                    when (it) {
                        "bold" -> bold = true
                        "italic" -> italic = true
                    }
                }
                Emphases(bold, italic)
            }
            else -> throw JsonDataException()
        }
    }

    @ToJson
    @Suppress("UNUSED_PARAMETER")
    fun toJson(writer: JsonWriter, value: Emphases?) {
        throw UnsupportedOperationException()
    }
}