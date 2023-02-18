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

internal sealed class Font {

    @JsonClass(generateAdapter = true)
    data class Dynamic(
        val textStyle: String,
        val emphases: Emphases
    ) : Font()

    @JsonClass(generateAdapter = true)
    data class Fixed(
        val weight: FontWeight,
        val size: Float
    ) : Font()

    @JsonClass(generateAdapter = true)
    data class Document(
        val textStyle: String,
        val fontFamily: String
    ) : Font()

    @JsonClass(generateAdapter = true)
    data class Custom(
        val size: Float,
        val fontName: String
    ) : Font()

    companion object {
        val FontPolyAdapterFactory: PolymorphicJsonAdapterFactory<Font> =
            PolymorphicJsonAdapterFactory.of(Font::class.java, "__caseName")
                .withSubtype(Dynamic::class.java, FontType.DYNAMIC.code)
                .withSubtype(Fixed::class.java, FontType.FIXED.code)
                .withSubtype(Document::class.java, FontType.DOCUMENT.code)
                .withSubtype(Custom::class.java, FontType.CUSTOM.code)
    }
}

internal enum class FontType(val code: String) {

    /**
     * A system font with a given semantic style that responds to the Dynamic Type system on iOS and the equivalent on Android.
     */
    DYNAMIC("dynamic"),

    /**
     * A system font with a fixed size and weight.
     */
    FIXED("fixed"),

    /**
     * A font which references an existing "Document Font" for its configuration.
     */
    DOCUMENT("document"),

    /**
     * A custom font which uses a specific font style and size.
     */
    CUSTOM("custom"),
}
