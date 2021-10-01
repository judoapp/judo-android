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

package app.judo.sdk.core.data

import app.judo.sdk.api.analytics.AnalyticsEvent
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.data.adapters.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

object JsonParser {

    private val barBackgroundPolymorphicAdapterFactory: PolymorphicJsonAdapterFactory<BarBackground> =
        PolymorphicJsonAdapterFactory.of(BarBackground::class.java, "__typeName")
            .withSubtype(BarBackground.ImageBarBackground::class.java, "ImageBarBackground")
            .withSubtype(BarBackground.OpaqueBarBackground::class.java, "OpaqueBarBackground")
            .withSubtype(BarBackground.TransparentBarBackground::class.java, "TransparentBarBackground")
            .withSubtype(BarBackground.TranslucentBarBackground::class.java, "TranslucentBarBackground")

    private val nodePolymorphicAdapterFactory: PolymorphicJsonAdapterFactory<Node> =
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

    private val actionAdapterFactory: PolymorphicJsonAdapterFactory<Action> = PolymorphicJsonAdapterFactory.of(
        Action::class.java, "__typeName"
    )
        .withSubtype(Action.Close::class.java, "CloseAction")
        .withSubtype(Action.PerformSegue::class.java, "PerformSegueAction")
        .withSubtype(Action.PresentWebsite::class.java, "PresentWebsiteAction")
        .withSubtype(Action.OpenURL::class.java, "OpenURLAction")
        .withSubtype(Action.Custom::class.java, "CustomAction")

    private val pageControlStyleAdapter: PolymorphicJsonAdapterFactory<PageControlStyle> = PolymorphicJsonAdapterFactory.of(
        PageControlStyle::class.java, "__typeName"
    )
        .withSubtype(PageControlStyle.DefaultPageControlStyle::class.java, "DefaultPageControlStyle")
        .withSubtype(PageControlStyle.LightPageControlStyle::class.java, "LightPageControlStyle")
        .withSubtype(PageControlStyle.DarkPageControlStyle::class.java, "DarkPageControlStyle")
        .withSubtype(PageControlStyle.InvertedPageControlStyle::class.java, "InvertedPageControlStyle")
        .withSubtype(PageControlStyle.CustomPageControlStyle::class.java, "CustomPageControlStyle")
        .withSubtype(PageControlStyle.ImagePageControlStyle::class.java, "ImagePageControlStyle")

    private val fontAdapterFactory: PolymorphicJsonAdapterFactory<Font> = PolymorphicJsonAdapterFactory.of(
        Font::class.java, "__typeName"
    )
        .withSubtype(Font.Fixed::class.java, "FixedFont")
        .withSubtype(Font.Dynamic::class.java, "DynamicFont")
        .withSubtype(Font.Custom::class.java, "CustomFont")

    private val fontResourceAdapterFactory: PolymorphicJsonAdapterFactory<FontResource> = PolymorphicJsonAdapterFactory.of(
        FontResource::class.java, "__typeName"
    )
        .withSubtype(FontResource.Single::class.java, "FontResource")
        .withSubtype(FontResource.Collection::class.java, "FontCollectionResource")

    private val webViewSourceAdapterFactory: PolymorphicJsonAdapterFactory<WebViewSource> = PolymorphicJsonAdapterFactory.of(
        WebViewSource::class.java, "__typeName"
    )
        .withSubtype(WebViewSource.URL::class.java, "WebViewURLSource")
        .withSubtype(WebViewSource.HTML::class.java, "WebViewHTMLSource")

    private val analyticsEventAdapter = PolymorphicJsonAdapterFactory.of(AnalyticsEvent::class.java, "type")
        .withSubtype(AnalyticsEvent.Register::class.java, "register")
        .withSubtype(AnalyticsEvent.Identify::class.java, "identify")
        .withSubtype(AnalyticsEvent.Screen::class.java, "screen")

    private val fontWeightAdapter = FontWeightJsonAdapter()
    private val horizontalAlignmentAdapter = HorizontalAlignmentJsonAdapter()
    private val verticalAlignmentAdapter = VerticalAlignmentJsonAdapter()
    private val alignmentAdapter = AlignmentJsonAdapter()
    private val httpMethodAdapter = HttpMethodJsonAdapter()
    private val videoResizingModeAdapter = VideoResizingModeAdapter()
    private val textAlignmentAdapter = TextAlignmentJsonAdapter()
    private val maxWidthAdapter = MaxWidthJsonAdapter()
    private val emphasisAdapter = EmphasesAdapter()
    private val maxHeightAdapter = MaxHeightJsonAdapter()
    private val fillsAdapter = FillsJsonAdapter()
    private val patternTypeAdapter = PatternTypeJsonAdapter()
    private val scrollAxisAdapter = ScrollAxisJsonAdapter()
    private val offsetJsonAdapter = OffsetJsonAdapter()
    private val textTransformAdapter = TextTransformAdapter()
    private val statusBarStyleAdapter = StatusBarStyleJsonAdapter()
    private val segueStyleAdapter = SegueStyleAdapter()
    private val menuItemVisibilityAdapter = MenuItemVisibilityJsonAdapter()
    private val appearanceAdapter = AppearanceAdapter()
    private val predicateJsonAdapter = PredicateJsonAdapter()


    val moshi: Moshi = Moshi.Builder()
        .add(fontAdapterFactory)
        .add(fontResourceAdapterFactory)
        .add(fontWeightAdapter)
        .add(emphasisAdapter)
        .add(appearanceAdapter)
        .add(horizontalAlignmentAdapter)
        .add(verticalAlignmentAdapter)
        .add(barBackgroundPolymorphicAdapterFactory)
        .add(nodePolymorphicAdapterFactory)
        .add(actionAdapterFactory)
        .add(pageControlStyleAdapter)
        .add(maxHeightAdapter)
        .add(maxWidthAdapter)
        .add(fillsAdapter)
        .add(alignmentAdapter)
        .add(videoResizingModeAdapter)
        .add(textAlignmentAdapter)
        .add(patternTypeAdapter)
        .add(scrollAxisAdapter)
        .add(offsetJsonAdapter)
        .add(textTransformAdapter)
        .add(segueStyleAdapter)
        .add(statusBarStyleAdapter)
        .add(menuItemVisibilityAdapter)
        .add(httpMethodAdapter)
        .add(predicateJsonAdapter)
        .add(analyticsEventAdapter)
        .add(webViewSourceAdapterFactory)
        .build()

    private val adapter = moshi.adapter(Experience::class.java)

    fun parseExperience(json: String): Experience? {
        return adapter.fromJson(json)
    }

    fun parseJudoMessage(json: String): JudoMessage? {
        return moshi.adapter(JudoMessage::class.java).fromJson(json)
    }

    fun parseAnalyticsEvents(json: String): List<AnalyticsEvent> {
        val parameterizedType = Types.newParameterizedType(
            MutableList::class.java,
            AnalyticsEvent::class.java
        )
        val adapter: JsonAdapter<List<AnalyticsEvent>> = moshi.adapter(parameterizedType)

        return adapter.fromJson(json) ?: listOf()
    }

    fun parseDictionaryMap(json: String): Map<String, Any> {
        val adapter: JsonAdapter<Map<String, Any>> = moshi
            .adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
        return adapter.fromJson(json) ?: emptyMap()
    }

    fun encodeDictionaryMap(map: Map<String, Any>): String {
        val adapter: JsonAdapter<Map<String, Any>> = moshi
            .adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
        return adapter.toJson(map)
    }

    fun encodeAnalyticsEvents(events: List<AnalyticsEvent>): String {
        val parameterizedType = Types.newParameterizedType(
            MutableList::class.java,
            AnalyticsEvent::class.java
        )
        val adapter: JsonAdapter<List<AnalyticsEvent>> = moshi.adapter(parameterizedType)
        return adapter.toJson(events)
    }
}
