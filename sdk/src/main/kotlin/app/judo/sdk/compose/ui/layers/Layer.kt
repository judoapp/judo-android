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

package app.judo.sdk.compose.ui.layers

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.judo.sdk.compose.model.nodes.*
import app.judo.sdk.compose.model.nodes.Collection
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.layers.stacks.HStackLayer
import app.judo.sdk.compose.ui.layers.stacks.VStackLayer
import app.judo.sdk.compose.ui.layers.stacks.ZStackLayer

@Composable
internal fun Layer(node: Node) {
    val tag = "Judo.Layer"

    CompositionLocalProvider(Environment.LocalNode provides node) {
        when (node) {
            is Collection -> CollectionLayer(node)
            is Conditional -> ConditionalLayer(node)
            is DataSource -> DataSourceLayer(node)
            is Audio -> AudioLayer(node)
            is Carousel -> CarouselLayer(node)
            is Divider -> DividerLayer(node)
            is HStack -> HStackLayer(node)
            is VStack -> VStackLayer(node)
            is ZStack -> ZStackLayer(node)
            is Icon -> IconLayer(node)
            is Image -> ImageLayer(node)
            is PageControl -> PageControlLayer(node)
            is Rectangle -> RectangleLayer(node)
            is ScrollContainer -> ScrollContainerLayer(node)
            is Spacer -> SpacerLayer(node)
            is Text -> TextLayer(node)
            is Video -> VideoLayer(node)
            is WebView -> WebViewLayer(node)
            is Screen -> Log.e(tag, "Tried to Compose a ScreenLayer through ChildrenComposable. These should be handled by navigation.")
            is AppBar -> Log.v(tag, "Tried to Compose an AppBarLayer through ChildrenComposable. Ignoring.")
            else -> Log.e(tag, "Unable to find Composable for Node: ${node.javaClass.simpleName}")
        }
    }
}
