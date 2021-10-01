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

package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.*
import app.judo.sdk.core.struct.TreeNode
import app.judo.sdk.ui.state.*

internal fun Node.toRenderable(): Renderable {
    return when (this) {

        is Text -> {

            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            TextRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment
            )

        }

        is HStack -> {

            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            HStackRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment,
            )
        }

        is VStack -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            VStackRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment,
            )
        }

        is ZStack -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            ZStackRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment,
            )
        }

        is DataSource -> {
            DataSourceRenderable(
                node = this,
            )
        }

        is Screen -> {
            ScreenRenderable(
                node = this,
            )
        }

        is AppBar -> {
            AppBarRenderable(
                node = this,
            )
        }

        is Audio -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            AudioRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment
            )
        }

        is Carousel -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            CarouselRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment,
            )
        }

        is app.judo.sdk.api.models.Collection -> {
            CollectionRenderable(
                node = this,
            )
        }

        is Conditional -> {
            ConditionalRenderable(
                node = this,
            )
        }

        is Divider -> {
            DividerRenderable(
                node = this
            )
        }

        is Icon -> {
            IconRenderable(
                node = this,
                mask = mask?.toRenderable()
            )
        }

        is Image -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            ImageRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment
            )
        }

        is MenuItem -> {
            MenuItemRenderable(
                node = this
            )
        }

        is PageControl -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            val renderablePageControlStyle: RenderablePageControlStyle = when (style) {
                is PageControlStyle.DarkPageControlStyle -> RenderablePageControlStyle.DarkPageControlStyle
                is PageControlStyle.DefaultPageControlStyle -> RenderablePageControlStyle.DefaultPageControlStyle
                is PageControlStyle.InvertedPageControlStyle -> RenderablePageControlStyle.InvertedPageControlStyle
                is PageControlStyle.LightPageControlStyle -> RenderablePageControlStyle.LightPageControlStyle
                is PageControlStyle.CustomPageControlStyle -> RenderablePageControlStyle.CustomPageControlStyle(
                    style.normalColor,
                    style.currentColor
                )
                is PageControlStyle.ImagePageControlStyle -> RenderablePageControlStyle.ImagePageControlStyle(
                    normalColor = style.normalColor,
                    currentColor = style.currentColor,
                    normalImage = style.normalImage.toRenderable() as ImageRenderable,
                    currentImage = style.currentImage.toRenderable() as ImageRenderable
                )
            }

            PageControlRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment,
                renderablePageControlStyle = renderablePageControlStyle
            )
        }

        is Rectangle -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            RectangleRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment
            )
        }

        is ScrollContainer -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            ScrollContainerRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment
            )
        }

        is Spacer -> {
            SpacerRenderable(
                node = this
            )
        }

        is Video -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            VideoRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment

            )
        }

        is WebView -> {
            val mask = mask?.toRenderable()

            val backgroundAndAlignment = background.toRenderable()

            val overlayAndAlignment = overlay.toRenderable()

            WebViewRenderable(
                node = this,
                mask = mask,
                backgroundAndAlignment = backgroundAndAlignment,
                overlayAndAlignment = overlayAndAlignment
            )
        }

        else -> throw IllegalStateException("Unsupported node: ${this::class.simpleName}")

    }
}

internal fun Background?.toRenderable(): Pair<Renderable, Alignment>? {
    return this?.node
        ?.toRenderable()
        ?.let { renderable -> renderable to alignment }
}

internal fun Overlay?.toRenderable(): Pair<Renderable, Alignment>? {
    return this?.node
        ?.toRenderable()
        ?.let { renderable -> renderable to alignment }
}

internal fun List<Node>.toModelTree(
    rootNodeID: String,
    nodesMap: Map<String, Node> = associateBy { it.id },
): ModelTree {

    val node = first { it.id == rootNodeID }

    return node.toModelTree(nodeMap = nodesMap)

}

internal fun Node.toModelTree(
    nodeMap: Map<String, Node>
): ModelTree {

    return if (this is NodeContainer) {

        val newBranches = getChildNodeIDs().mapNotNull { nodeMap[it] }.map {
            it.toModelTree(nodeMap = nodeMap)
        }.toMutableList()

        TreeNode(this, newBranches)

    } else {
        TreeNode(this)
    }

}

internal fun List<Node>.addImplicitStacksForScrollContainers(screenID: String): List<Node> {

    val modifiedNodes = toMutableList()

    val scrollContainers = (firstOrNull { it.id == screenID } as? Screen)?.let { screen ->
        filterIsInstance<ScrollContainer>().filter { it.id in screen.childIDs }
    } ?: emptyList()

    scrollContainers.map { scrollContainer ->
        if (scrollContainer.childIDs.count() > 1) {
            val stackNode = if (scrollContainer.axis == Axis.VERTICAL) {
                VStack(
                    id = "${scrollContainer.id}-vStack",
                    spacing = 0f,
                    alignment = HorizontalAlignment.CENTER,
                    childIDs = scrollContainer.childIDs
                )
            } else {
                HStack(
                    id = "${scrollContainer.id}-hStack",
                    spacing = 0f,
                    alignment = VerticalAlignment.CENTER,
                    childIDs = scrollContainer.childIDs
                )
            }
            val newScrollContainer = scrollContainer.copy(childIDs = listOf(stackNode.id))
            modifiedNodes.remove(scrollContainer)
            modifiedNodes.add(newScrollContainer)
            modifiedNodes.add(stackNode)
        }
    }

    return modifiedNodes
}