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

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

internal sealed class Action {

    @JsonClass(generateAdapter = true)
    class Close : Action() {
        override fun equals(other: Any?): Boolean {
            return other is Close
        }

        override fun hashCode(): Int {
            return "close-action-123".hashCode()
        }
    }

    @JsonClass(generateAdapter = true)
    class PerformSegue(
        @Transient
        var screenID: String? = null,
        @Transient
        var segueStyle: SegueStyle? = null
    ) : Action() {
        @Transient
        var data: Any? = null
    }

    @JsonClass(generateAdapter = true)
    data class OpenURL(
        val url: String,
        val dismissExperience: Boolean
    ) : Action()

    @JsonClass(generateAdapter = true)
    data class PresentWebsite(
        val url: String
    ) : Action()

    @JsonClass(generateAdapter = true)
    @Suppress("CanSealedSubClassBeObject")
    class Custom(
        val dismissExperience: Boolean
    ) : Action()

    companion object {
        val ActionPolyAdapterFactory: PolymorphicJsonAdapterFactory<Action> = PolymorphicJsonAdapterFactory.of(
            Action::class.java, "__caseName"
        )
            .withSubtype(Close::class.java, ActionTypes.CLOSE.code)
            .withSubtype(PerformSegue::class.java, ActionTypes.PERFORM_SEGUE.code)
            .withSubtype(PresentWebsite::class.java, ActionTypes.PRESENT_WEBSITE.code)
            .withSubtype(OpenURL::class.java, ActionTypes.OPEN_URL.code)
            .withSubtype(Custom::class.java, ActionTypes.CUSTOM.code)
    }
}

internal enum class ActionTypes(val code: String) {
    PERFORM_SEGUE("performSegue"),
    OPEN_URL("openURL"),
    PRESENT_WEBSITE("presentWebsite"),
    CLOSE("close"),
    CUSTOM("custom"),
}

enum class ShareExperienceActionTypes(val code: String) {
    CURRENT("current"),
    INITIAL("initial");

    internal class ShareExperienceJsonAdapter {
        @ToJson
        fun toJson(type: ShareExperienceActionTypes) = type.code

        @FromJson
        fun fromJson(type: String) = ShareExperienceActionTypes.valueOf(type)
    }
}
