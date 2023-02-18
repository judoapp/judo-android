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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLifecycleOwner
import app.judo.sdk.compose.model.nodes.Carousel
import app.judo.sdk.compose.model.nodes.Collection
import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.nodes.getItems
import app.judo.sdk.compose.ui.CarouselState
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.ViewID
import app.judo.sdk.compose.ui.data.DataContext
import app.judo.sdk.compose.ui.data.makeDataContext
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.utils.ExpandMeasurePolicy
import app.judo.sdk.compose.vendor.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun CarouselLayer(node: Carousel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val collectionIndex = Environment.LocalCollectionIndex.current
    val viewID = ViewID(node.id, collectionIndex)

    val dataContext = makeDataContext(
        userInfo = Environment.LocalUserInfo.current?.invoke() ?: emptyMap(),
        urlParameters = Environment.LocalUrlParameters.current,
        data = Environment.LocalData.current
    )

    val collection = carouselPages(node, dataContext)
    val startIndex = if (node.isLoopEnabled) Int.MAX_VALUE / 2 else 0
    val pagerCount = if (node.isLoopEnabled) Int.MAX_VALUE else collection.size
    val pagerState = rememberPagerState(initialPage = startIndex)

    DisposableEffect(lifecycleOwner) {
        Environment.LocalCarouselStates[viewID] =
            CarouselState(
                pagerState = pagerState,
                startIndex = startIndex,
                collectionSize = collection.size,
                isLoopEnabled = node.isLoopEnabled
            )
        onDispose {
            Environment.LocalCarouselStates.remove(viewID)
        }
    }
    LayerBox(judoModifiers = JudoModifiers(node)) {
        Layout(
            {
                HorizontalPager(
                    count = pagerCount,
                    state = pagerState
                ) { index ->
                    Box( contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize() ) {
                        val page = (index - startIndex).floorMod(collection.size)
                        CarouselPage(collection[page])
                    }
                }
            },
            measurePolicy = ExpandMeasurePolicy(false)
        )
    }
}

@Composable
private fun CarouselPage(carouselItem: CarouselItem) {
    if (carouselItem.item != null) {
        CompositionLocalProvider(Environment.LocalData provides carouselItem.item) {
            Children(listOf(carouselItem.node))
        }
    } else {
        Children(listOf(carouselItem.node))
    }
}

private data class CarouselItem(
    val node: Node,
    val item: Any? = null
)

private fun carouselPages(carousel: Carousel, dataContext: DataContext): List<CarouselItem> {
    val nodes = carousel.children.flatMap { node ->
        when (node) {
            is Collection -> {
                node.getItems(dataContext).flatMap { item ->
                    node.children.map { childNode ->
                        CarouselItem(childNode, item)
                    }
                }
            }
            else -> {
                listOf(CarouselItem(node))
            }
        }
    }

    return nodes.toList()
}
