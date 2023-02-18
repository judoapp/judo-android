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
import com.squareup.moshi.ToJson

internal enum class FontWeight(val code: String) {
    UltraLight("ultraLight"),
    Thin("thin"),
    Light("light"),
    Regular("regular"),
    Medium("medium"),
    SemiBold("semiBold"),
    Bold("bold"),
    Heavy("heavy"),
    Black("black");

    internal class FontWeightJsonAdapter {
        @ToJson
        fun toJson(fontWeight: FontWeight) = fontWeight.code

        @FromJson
        fun fromJson(fontWeight: String) =
            FontWeight.values().find { it.code.equals(fontWeight, true) } ?: throw RuntimeException("Incorrect code")
    }
}
