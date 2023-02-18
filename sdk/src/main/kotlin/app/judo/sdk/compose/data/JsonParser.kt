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

package app.judo.sdk.compose.data

import app.judo.sdk.compose.model.nodes.WebViewSource
import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.model.values.Action
import app.judo.sdk.compose.model.values.Alignment
import app.judo.sdk.compose.model.values.Appearance
import app.judo.sdk.compose.model.values.AssetSource
import app.judo.sdk.compose.model.values.Axis
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.model.values.Emphases
import app.judo.sdk.compose.model.values.Fill
import app.judo.sdk.compose.model.values.Font
import app.judo.sdk.compose.model.values.FontWeight
import app.judo.sdk.compose.model.values.GradientReference
import app.judo.sdk.compose.model.values.MaxHeight
import app.judo.sdk.compose.model.values.MaxWidth
import app.judo.sdk.compose.model.values.MenuItemVisibility
import app.judo.sdk.compose.model.values.OffsetJsonAdapter
import app.judo.sdk.compose.model.values.PageControlStyle
import app.judo.sdk.compose.model.values.Predicate
import app.judo.sdk.compose.model.values.ResizingMode
import app.judo.sdk.compose.model.values.StatusBarStyle
import app.judo.sdk.compose.model.values.TextAlignment
import app.judo.sdk.compose.model.values.TextTransform
import app.judo.sdk.compose.model.values.VideoResizingMode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

internal object JsonParser {

    // TODO: re-add analytics and move this to AnalyticsEvent.
//    private val analyticsEventAdapter = PolymorphicJsonAdapterFactory.of(AnalyticsEvent::class.java, "type")
//        .withSubtype(AnalyticsEvent.Register::class.java, "register")
//        .withSubtype(AnalyticsEvent.Identify::class.java, "identify")
//        .withSubtype(AnalyticsEvent.Screen::class.java, "screen")

    val moshi: Moshi = Moshi.Builder()
        // Polymorphic model adapters.
        .add(Node.NodePolyAdapterFactory)
        .add(ColorReference.ColorReferencePolyAdapterFactory)
        .add(Font.FontPolyAdapterFactory)
        .add(PageControlStyle.PageControlStylePolyAdapterFactory)
        .add(Action.ActionPolyAdapterFactory)
        .add(AssetSource.AssetSourcePolyAdapterFactory)
        .add(GradientReference.GradientReferencePolyAdapterFactory)
        .add(Fill.FillPolyAdapterFactory)
        .add(SegueStyle.SegueStylePolyAdapterFactory)
        .add(WebViewSource.WebViewSourcePolyAdapterFactory)
        // Model adapters.
        .add(SeguePresentationStyleType.SeguePresentationStyleAdapter())
        .add(FontWeight.FontWeightJsonAdapter())
        .add(Emphases.EmphasesAdapter())
        .add(Appearance.AppearanceAdapter())
        .add(MaxHeight.MaxHeightJsonAdapter())
        .add(MaxWidth.MaxWidthJsonAdapter())
        .add(Alignment.AlignmentJsonAdapter())
        .add(VideoResizingMode.VideoResizingModeAdapter())
        .add(TextAlignment.TextAlignmentJsonAdapter())
        .add(ResizingMode.PatternTypeJsonAdapter())
        .add(Axis.ScrollAxisJsonAdapter())
        .add(TextTransform.TextTransformAdapter())
        .add(StatusBarStyle.StatusBarStyleJsonAdapter())
        .add(MenuItemVisibility.MenuItemVisibilityJsonAdapter())
        .add(HttpMethod.HttpMethodJsonAdapter())
        .add(Predicate.PredicateJsonAdapter())
        .add(ShareExperienceActionTypes.ShareExperienceJsonAdapter())
        .add(Authorizer.AuthorizerMethodAdapter())
        //        .add(analyticsEventAdapter)
        .add(OffsetJsonAdapter())
        .add(DimensionsJsonAdapter())
        .build()

    private val adapter = app.judo.sdk.compose.data.JsonParser.moshi.adapter(ExperienceModel::class.java)

    fun parseExperience(json: String): ExperienceModel? {
        return app.judo.sdk.compose.data.JsonParser.adapter.fromJson(json)
    }

//    fun parseAnalyticsEvents(json: String): List<AnalyticsEvent> {
//        val parameterizedType = Types.newParameterizedType(
//            MutableList::class.java,
//            AnalyticsEvent::class.java
//        )
//        val adapter: JsonAdapter<List<AnalyticsEvent>> = moshi.adapter(parameterizedType)
//
//        return adapter.fromJson(json) ?: listOf()
//    }

    fun parseDictionaryMap(json: String): Map<String, Any> {
        val adapter: JsonAdapter<Map<String, Any>> = app.judo.sdk.compose.data.JsonParser.moshi
            .adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
        return adapter.fromJson(json) ?: emptyMap()
    }

    fun encodeDictionaryMap(map: Map<String, Any>): String {
        val adapter: JsonAdapter<Map<String, Any>> = app.judo.sdk.compose.data.JsonParser.moshi
            .adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
        return adapter.toJson(map)
    }

//    fun encodeAnalyticsEvents(events: List<AnalyticsEvent>): String {
//        val parameterizedType = Types.newParameterizedType(
//            MutableList::class.java,
//            AnalyticsEvent::class.java
//        )
//        val adapter: JsonAdapter<List<AnalyticsEvent>> = moshi.adapter(parameterizedType)
//        return adapter.toJson(events)
//    }
}
