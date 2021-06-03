package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.api.models.Fill
import app.judo.sdk.api.models.GradientVariants
import app.judo.sdk.core.data.JsonParser
import com.squareup.moshi.*

@Suppress("UNCHECKED_CAST")
internal class FillsJsonAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Fill {
        val jsonValue = reader.readJsonValue() as Map<String, Any>
        val fillType = jsonValue["__typeName"]
        val opacity: Double = (jsonValue["opacity"] as? Double ?: 1.0)

        return when (fillType) {
            "FlatFill" -> {
                val colorAdapter = JsonParser.moshi.adapter<ColorVariants>(ColorVariants::class.java, emptySet(), "color")
                val color = colorAdapter.fromJsonValue(jsonValue["color"])
                Fill.FlatFill(color = color!!)
            }
            "GradientFill" -> {
                val gradientAdapter = JsonParser.moshi.adapter<GradientVariants>(GradientVariants::class.java, emptySet(), "gradient")
                val gradient = gradientAdapter.fromJsonValue(jsonValue["gradient"])!!
                Fill.GradientFill(gradient = gradient)
            }
            else -> throw JsonDataException()
        }
    }

    @ToJson
    @Suppress("UNUSED_PARAMETER")
    fun toJson(writer: JsonWriter, value: Fill?) {
    }
}