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

package app.judo.sdk.compose.model.nodes

import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.ui.data.Interpolator
import com.squareup.moshi.JsonClass
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

@JsonClass(generateAdapter = true)
internal data class WebView(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    override val opacity: Float? = null,
    override val aspectRatio: Float? = null,
    override val frame: Frame? = null,
    override val padding: Padding? = null,
    override val layoutPriority: Int? = null,
    override val offset: Point? = null,
    override val shadow: Shadow? = null,
    override val mask: Node? = null,
    override val accessibility: Accessibility? = null,
    override val overlay: Overlay? = null,
    override val background: Background? = null,
    override val action: Action? = null,
    override var children: List<Node> = emptyList(),
    override val childIDs: List<String> = emptyList(),
    val source: WebViewSource,
    val isScrollEnabled: Boolean
) : Node {
    override val typeName = NodeType.WEB.code
}

sealed class WebViewSource {

    @JsonClass(generateAdapter = true)
    data class URL(
        val value: String
    ) : WebViewSource()

    @JsonClass(generateAdapter = true)
    data class HTML(
        val value: String
    ) : WebViewSource()

    companion object {
        val WebViewSourcePolyAdapterFactory: PolymorphicJsonAdapterFactory<WebViewSource> =
            PolymorphicJsonAdapterFactory.of(WebViewSource::class.java, "__caseName")
                .withSubtype(URL::class.java, WebViewSourceType.URL.code)
                .withSubtype(HTML::class.java, WebViewSourceType.HTML.code)
    }
}

internal enum class WebViewSourceType(val code: String) {
    URL("url"),
    HTML("html")
}

internal fun WebViewSource.interpolatedSource(interpolator: Interpolator): WebViewSource? {
    return when (this) {
        is WebViewSource.URL -> {
            val interpolatedUrl = interpolator.interpolate(value) ?: return null
            WebViewSource.URL(interpolatedUrl)
        }
        is WebViewSource.HTML -> {
            val interpolatedAssetName = interpolator.interpolate(value) ?: return null
            WebViewSource.HTML(interpolatedAssetName)
        }
    }
}
