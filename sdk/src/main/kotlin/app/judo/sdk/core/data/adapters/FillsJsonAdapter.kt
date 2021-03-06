/*
 * Copyright (c) 2020-present, Rover Labs, Inc. All rights reserved.
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Rover.
 *
 * This copyright notice shall be included in all copies or substantial portions of
 * the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
            else -> throw JsonDataException("Unsupported fill type appeared in JSON: $fillType")
        }
    }

    @ToJson
    @Suppress("UNUSED_PARAMETER")
    fun toJson(writer: JsonWriter, value: Fill?) {
    }
}