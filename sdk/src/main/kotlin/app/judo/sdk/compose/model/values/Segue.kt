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

@JsonClass(generateAdapter = true)
internal data class Segue(

    /**
     * The unique ID assigned to the segue, typically a UUID generated by the Mac app.
     */
    val id: String,

    /**
     * The ID of the [Node] which performs the [Segue].
     */
    val sourceID: String,

    /**
     * The ID of the [Screen] which the [Segue] transitions to.
     */
    val destinationID: String,

    /**
     * The style of the segue transition. Either a [SegueStyle.PUSH] (navigation) transition or
     * a [SegueStyle.MODAL] transition.
     */
    val style: SegueStyle
)

sealed class SegueStyle() {
    @JsonClass(generateAdapter = true)
    class PushSegue : SegueStyle()

    @JsonClass(generateAdapter = true)
    data class ModalSegue(
        // currently unused! We always do fullscreen.
        @Transient
        val presentationStyle: SeguePresentationStyleType? = null
    ) : SegueStyle()

    companion object {
        val SegueStylePolyAdapterFactory: PolymorphicJsonAdapterFactory<SegueStyle> =
            PolymorphicJsonAdapterFactory.of(SegueStyle::class.java, "__caseName")
                .withSubtype(PushSegue::class.java, SegueStyleType.PUSH.code)
                .withSubtype(ModalSegue::class.java, SegueStyleType.MODAL.code)
    }
}

enum class SegueStyleType(val code: String) {
    PUSH("push"),
    MODAL("modal");
}

/**
 * One of sheet or fullScreen.
 * This determines whether the screen appears full screen or with the modal card stack effect when presented modally.
 * When the `__caseName` is “modal” this property will be present—otherwise this property will be null.
 */
enum class SeguePresentationStyleType(val code: String) {
    SHEET("sheet"),
    FULLSCREEN("fullscreen");

    internal class SeguePresentationStyleAdapter {
        @ToJson
        fun toJson(presentationStyle: SeguePresentationStyleType) = presentationStyle.code

        @FromJson
        fun fromJson(presentationStyle: String) = SeguePresentationStyleType.valueOf(presentationStyle)
    }
}
