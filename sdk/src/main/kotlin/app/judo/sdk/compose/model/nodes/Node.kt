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
import com.squareup.moshi.Json
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

internal interface Node {
    val id: String
    val name: String?
    val metadata: Metadata?
    var children: List<Node>
    val childIDs: List<String>

    val aspectRatio: Float?
    val action: Action?
    val frame: Frame?
    val opacity: Float?

    val padding: Padding?
    val layoutPriority: Int?
    val offset: Point?
    val shadow: Shadow?
    val mask: Node?
    val overlay: Overlay?
    val background: Background?
    val accessibility: Accessibility?

    @Json(name = "__type")
    val typeName: String

    companion object {
        val NodePolyAdapterFactory: PolymorphicJsonAdapterFactory<Node> =
            PolymorphicJsonAdapterFactory.of(Node::class.java, "__typeName")
                .withSubtype(ZStack::class.java, NodeType.ZSTACK.code)
                .withSubtype(VStack::class.java, NodeType.VSTACK.code)
                .withSubtype(HStack::class.java, NodeType.HSTACK.code)
                .withSubtype(ScrollContainer::class.java, NodeType.SCROLL_CONTAINER.code)
                .withSubtype(Audio::class.java, NodeType.AUDIO.code)
                .withSubtype(Video::class.java, NodeType.VIDEO.code)
                .withSubtype(Image::class.java, NodeType.IMAGE.code)
                .withSubtype(WebView::class.java, NodeType.WEB.code)
                .withSubtype(Text::class.java, NodeType.TEXT.code)
                .withSubtype(Rectangle::class.java, NodeType.RECTANGLE.code)
                .withSubtype(Screen::class.java, NodeType.SCREEN.code)
                .withSubtype(Spacer::class.java, NodeType.SPACER.code)
                .withSubtype(Carousel::class.java, NodeType.CAROUSEL.code)
                .withSubtype(Collection::class.java, NodeType.COLLECTION.code)
                .withSubtype(Conditional::class.java, NodeType.CONDITIONAL.code)
                .withSubtype(DataSource::class.java, NodeType.DATA_SOURCE.code)
                .withSubtype(PageControl::class.java, NodeType.PAGE_CONTROL.code)
                .withSubtype(Divider::class.java, NodeType.DIVIDER.code)
                .withSubtype(Icon::class.java, NodeType.ICON.code)
                .withSubtype(AppBar::class.java, NodeType.APP_BAR.code)
                .withSubtype(MenuItem::class.java, NodeType.MENU_ITEM.code)
                .withDefaultValue(EmptyNode())
    }

    /**
     * Node types that have relationships (including any belonging to its value types or nested
     * nodes) to fix up post-deserialization.
     *
     * Implementers be sure to call super!
     */
    fun setRelationships(
        nodes: Map<String, Node>,
        documentColors: Map<String, DocumentColor>,
        documentGradients: Map<String, DocumentGradient>,
        screens: Map<String, Screen>
    ) {
        // set up children:
        children = childIDs.mapNotNull { nodes[it] }

        // visit all modifiers that also need it:
        this.mask?.setRelationships(nodes, documentColors, documentGradients, screens)
        this.shadow?.color?.setRelationships(documentColors)
        this.background?.node?.setRelationships(nodes, documentColors, documentGradients, screens)
        this.overlay?.node?.setRelationships(nodes, documentColors, documentGradients, screens)

        // then visit all the children as well:
        children.forEach {
            it.setRelationships(nodes, documentColors, documentGradients, screens)
        }
    }

    val description: String
        get() = "$typeName(id=$id,name=$name)"
}

@Suppress("SpellCheckingInspection")
internal enum class NodeType(val code: String) {
    IMAGE("Image"),
    RECTANGLE("Rectangle"),
    WEB("WebView"),
    TEXT("Text"),
    SCREEN("Screen"),
    ZSTACK("ZStack"),
    SPACER("Spacer"),
    VSTACK("VStack"),
    HSTACK("HStack"),
    CAROUSEL("Carousel"),
    COLLECTION("Collection"),
    DATA_SOURCE("DataSource"),
    PAGE_CONTROL("PageControl"),
    SCROLL_CONTAINER("ScrollContainer"),
    AUDIO("Audio"),
    VIDEO("Video"),
    ICON("Icon"),
    DIVIDER("Divider"),
    APP_BAR("AppBar"),
    CONDITIONAL("Conditional"),
    MENU_ITEM("AppBarMenuItem");
}
