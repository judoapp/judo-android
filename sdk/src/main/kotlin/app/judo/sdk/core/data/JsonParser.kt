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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

object JsonParser {

    private val nodePolymorphicAdapterFactory: PolymorphicJsonAdapterFactory<Node> =
        PolymorphicJsonAdapterFactory.of(Node::class.java, "__typeName")
            .withSubtype(Screen::class.java, NodeType.SCREEN.code)
            .withDefaultValue(EmptyNode())

    private val actionAdapterFactory: PolymorphicJsonAdapterFactory<Action> = PolymorphicJsonAdapterFactory.of(
        Action::class.java,
        "__typeName"
    )
        .withSubtype(Action.Close::class.java, "CloseAction")
        .withSubtype(Action.PerformSegue::class.java, "PerformSegueAction")
        .withSubtype(Action.PresentWebsite::class.java, "PresentWebsiteAction")
        .withSubtype(Action.OpenURL::class.java, "OpenURLAction")
        .withSubtype(Action.Custom::class.java, "CustomAction")

    private val analyticsEventAdapter = PolymorphicJsonAdapterFactory.of(AnalyticsEvent::class.java, "type")
        .withSubtype(AnalyticsEvent.Register::class.java, "register")
        .withSubtype(AnalyticsEvent.Identify::class.java, "identify")
        .withSubtype(AnalyticsEvent.Screen::class.java, "screen")

    val moshi: Moshi = Moshi.Builder()
        .add(nodePolymorphicAdapterFactory)
        .add(actionAdapterFactory)
        .add(analyticsEventAdapter)
        .build()

    private val adapter = moshi.adapter(Experience::class.java)

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
