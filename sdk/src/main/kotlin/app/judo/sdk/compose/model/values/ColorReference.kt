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
 * A reference to a [ColorValue]. These can come in different flavors depending on use-case.
 * For more information on each type, see [CustomColor], [DocumentColor], and [SystemColor].
 */
internal sealed class ColorReference {

    /**
     * [CustomColor] is a type of [ColorReference] that is in fact not a reference, but rather a one-off,
     * anonymous color that is used only here. The [ColorValue] is present in [CustomColor], and can't
     * be referenced elsewhere in the document.
     *
     * For re-using colors for the whole document, see [DocumentColor].
     * For colors defined by the system and its theme, see [SystemColor].
     */
    @JsonClass(generateAdapter = true)
    data class CustomColor(
        val customColor: ColorValue
    ) : ColorReference()

    /**
     * [DocumentColor] is a type of [ColorReference] for Document-level accessibility, so you can
     * reuse the same [ColorValue] in multiple spots within the [ExperienceModel].
     *
     * For one-off, single use colors, see [CustomColor].
     * For colors defined by the system and its theme, see [SystemColor].
     */
    @JsonClass(generateAdapter = true)
    data class DocumentColor(
        val documentColorID: String,
        @Transient var documentColor: app.judo.sdk.compose.model.values.DocumentColor? = null
    ) : ColorReference() {
        override fun setRelationships(documentColors: Map<String, app.judo.sdk.compose.model.values.DocumentColor>) {
            documentColor = documentColors.get(documentColorID)
        }
    }

    /**
     * [SystemColor] means a system-defined "semantic color" is used, drawn from the list of iOS system/semantic colors.
     * The same iOS colors are used for Android, and the name of the semantic property is present through [colorName].
     *
     * For one-off, single use colors, see [CustomColor].
     * For re-using colors for the whole document, see [DocumentColor].
     */
    @JsonClass(generateAdapter = true)
    data class SystemColor(
        val colorName: String
    ) : ColorReference()

    companion object {
        val ColorReferencePolyAdapterFactory: PolymorphicJsonAdapterFactory<ColorReference> =
            PolymorphicJsonAdapterFactory.of(ColorReference::class.java, "referenceType")
                .withSubtype(CustomColor::class.java, ColorReferenceType.CUSTOM.code)
                .withSubtype(DocumentColor::class.java, ColorReferenceType.DOCUMENT.code)
                .withSubtype(SystemColor::class.java, ColorReferenceType.SYSTEM.code)
    }

    open fun setRelationships(
        documentColors: Map<String, app.judo.sdk.compose.model.values.DocumentColor>
    ) {
        // no-op.
    }
}

internal enum class ColorReferenceType(val code: String) {

    /**
     * [CUSTOM] means this [ColorReference] is not in fact a reference, but rather a one-off,
     * anonymous color that is used only here. The [ColorValue] will be present in [ColorReference.customColor].
     */
    CUSTOM("custom"),

    /**
     * [DOCUMENT] means a reference to a Document-level DocumentColor defined by the designer that
     * may be used across many usage sites.
     */
    DOCUMENT("document"),

    /**
     * [SYSTEM] means a system-defined “semantic” color is used, drawn from the list of iOS
     * system/semantic colors. They are also used on Android. The name will be present in [ColorReference.colorName].
     */
    SYSTEM("system")
}
