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

package app.judo.sdk.compose.model.values

import com.squareup.moshi.JsonClass
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

internal sealed class Fill {

    @JsonClass(generateAdapter = true)
    data class FlatFill(
        val color: ColorReference
    ) : Fill() {
        override fun setRelationships(
            documentColors: Map<String, DocumentColor>,
            documentGradients: Map<String, DocumentGradient>
        ) {
            color.setRelationships(documentColors)
        }
    }

    @JsonClass(generateAdapter = true)
    data class GradientFill(
        val gradient: GradientReference
    ) : Fill() {
        override fun setRelationships(
            documentColors: Map<String, DocumentColor>,
            documentGradients: Map<String, DocumentGradient>
        ) {
            gradient.setRelationships(documentGradients)
        }
    }

    companion object {
        val FillPolyAdapterFactory: PolymorphicJsonAdapterFactory<Fill> =
            PolymorphicJsonAdapterFactory.of(Fill::class.java, "__caseName")
                .withSubtype(FlatFill::class.java, FillType.FLAT.code)
                .withSubtype(GradientFill::class.java, FillType.GRADIENT.code)
    }

    abstract fun setRelationships(
        documentColors: Map<String, DocumentColor>,
        documentGradients: Map<String, DocumentGradient>
    )
}

internal enum class FillType(val code: String) {
    FLAT("flat"),
    GRADIENT("gradient")
}
