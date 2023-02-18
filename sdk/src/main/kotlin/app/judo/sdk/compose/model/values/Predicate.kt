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

internal enum class Predicate(val code: String) {
    EQUALS(code = "equals"),
    DOES_NOT_EQUAL(code = "doesNotEqual"),
    IS_GREATER_THAN(code = "isGreaterThan"),
    IS_LESS_THAN(code = "isLessThan"),
    IS_SET(code = "isSet"),
    IS_NOT_SET(code = "isNotSet"),
    IS_TRUE(code = "isTrue"),
    IS_FALSE(code = "isFalse");

    internal class PredicateJsonAdapter {
        @ToJson
        fun toJson(predicate: Predicate) = predicate.code

        @FromJson
        fun fromJson(predicate: String) =
            Predicate.values().find { it.code.equals(predicate, true) }
                ?: throw RuntimeException("Incorrect code")
    }
}
