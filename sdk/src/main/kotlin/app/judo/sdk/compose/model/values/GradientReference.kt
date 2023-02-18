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

/**
 * A reference to a [GradientValue]. These can come in different flavors depending on use-case.
 * For more information on each type, see [CustomGradient] and [DocumentGradient].
 */
internal sealed class GradientReference {

    /**
     * [CustomGradient] means this [GradientReference] is not in fact a reference, but rather a one-off,
     * anonymous gradient that is used only here. The [GradientValue] is present inside [CustomGradient].
     *
     * For gradients that are re-usable in the whole document, see [DocumentGradient].
     */
    @JsonClass(generateAdapter = true)
    data class CustomGradient(
        val customGradient: GradientValue
    ) : GradientReference()

    /**
     * [DocumentGradient] is a reference to a Document-level [GradientValue] defined by the designers
     * that may be used in multiple places throughout the [ExperienceModel].
     *
     * For one-off, single use gradients, see [CustomGradient].
     */
    @JsonClass(generateAdapter = true)
    data class DocumentGradient(
        val documentGradientID: String,
        @Transient var documentGradient: app.judo.sdk.compose.model.values.DocumentGradient? = null
    ) : GradientReference() {
        override fun setRelationships(documentGradients: Map<String, app.judo.sdk.compose.model.values.DocumentGradient>) {
            documentGradient = documentGradients.get(documentGradientID)
        }
    }

    companion object {
        val GradientReferencePolyAdapterFactory: PolymorphicJsonAdapterFactory<GradientReference> =
            PolymorphicJsonAdapterFactory.of(GradientReference::class.java, "referenceType")
                .withSubtype(CustomGradient::class.java, GradientReferenceType.CUSTOM.code)
                .withSubtype(DocumentGradient::class.java, GradientReferenceType.DOCUMENT.code)
    }

    open fun setRelationships(
        documentGradients: Map<String, app.judo.sdk.compose.model.values.DocumentGradient>
    ) {
        // no-op.
    }
}

internal enum class GradientReferenceType(val code: String) {

    /**
     * [CUSTOM] means this [GradientReference] is not in fact a reference, but rather a one-off,
     * anonymous gradient that is used only here. The [ColorValue] will be present in CustomGradient.
     */
    CUSTOM("custom"),

    /**
     * [DOCUMENT] means a reference to a Document-level DocumentGradient defined by the
     * designer that may be used in multiple places.
     */
    DOCUMENT("document")
}
