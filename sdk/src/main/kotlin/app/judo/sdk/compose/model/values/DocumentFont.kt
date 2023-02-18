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

@JsonClass(generateAdapter = true)
internal data class DocumentFont(
    val fontFamily: String,

    // optional to support document versions older than compatibility version 12
    var sources: List<Source>?,

    // Configurable styles:
    val largeTitle: CustomFont,
    val title: CustomFont,
    val title2: CustomFont,
    val title3: CustomFont,
    val headline: CustomFont,
    val body: CustomFont,
    val callout: CustomFont,
    val subheadline: CustomFont,
    val footnote: CustomFont,
    val caption: CustomFont,
    val caption2: CustomFont
) {
    @JsonClass(generateAdapter = true)
    data class CustomFont(
        /**
         * Font postscript name.
         */
        val fontName: String,
        val size: Float
    )

    @JsonClass(generateAdapter = true)
    data class Source(
        var assetName: String,

        /**
         * The postscript names supplied by this asset.
         *
         * Given in the same order as provided by the asset, if it is a
         * TTC/OTC font collection (Not yet supported).
         */
        var fontNames: List<String>
    )

    fun styleByName(name: String): CustomFont? = when (name) {
        "largeTitle" -> largeTitle
        "title" -> title
        "title2" -> title2
        "title3" -> title3
        "headline" -> headline
        "body" -> body
        "callout" -> callout
        "subheadline" -> subheadline
        "footnote" -> footnote
        "caption" -> caption
        "caption2" -> caption2
        else -> null
    }
}
