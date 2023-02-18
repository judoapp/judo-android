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

import app.judo.sdk.compose.model.values.Predicate.*
import app.judo.sdk.compose.ui.data.DataContext
import app.judo.sdk.compose.ui.data.Interpolator
import app.judo.sdk.compose.ui.data.fromKeyPath
import com.squareup.moshi.JsonClass
import org.json.JSONObject

@JsonClass(generateAdapter = true)
internal data class Condition(
    val keyPath: String,
    val predicate: Predicate,
    val value: Any?
)

internal fun Condition.isSatisfied(dataContext: DataContext): Boolean {
    val lhs = dataContext.fromKeyPath(keyPath)
    val rhs = (value as? String)?.let {
        val interpolator = Interpolator(dataContext)
        interpolator.interpolate(it)
    } ?: value

    return isSatisifed(
        lhs = lhs,
        rhs = rhs,
        predicate = predicate
    )
}

internal fun isSatisifed(lhs: Any?, rhs: Any?, predicate: Predicate): Boolean {
    return when (predicate) {
        EQUALS -> {
            when (rhs) {
                is String -> {
                    (lhs as? String)?.let {
                        it == rhs
                    } ?: false
                }
                is Number -> {
                    (lhs as? Number)?.toDouble()?.let {
                        it == rhs.toDouble()
                    } ?: false
                }
                else -> false
            }
        }
        DOES_NOT_EQUAL -> {
            when (rhs) {
                is String -> {
                    (lhs as? String)?.let {
                        it != rhs
                    } ?: true
                }
                is Number -> {
                    (lhs as? Number)?.toDouble()?.let {
                        it != rhs.toDouble()
                    } ?: true
                }
                else -> true
            }
        }
        IS_GREATER_THAN -> {
            if (rhs is Number) {
                (lhs as? Number)?.toDouble()?.let {
                    it > rhs.toDouble()
                } ?: false
            } else {
                false
            }
        }
        IS_LESS_THAN -> {
            if (rhs is Number) {
                (lhs as? Number)?.toDouble()?.let {
                    it < rhs.toDouble()
                } ?: false
            } else {
                false
            }
        }
        IS_SET -> {
            lhs != "null" && lhs != JSONObject.NULL && lhs != null
        }
        IS_NOT_SET -> {
            lhs == "null" || lhs == JSONObject.NULL || lhs == null
        }
        IS_TRUE -> {
            "$lhs".equals("true", ignoreCase = true)
        }
        IS_FALSE -> {
            "$lhs".equals("false", ignoreCase = true)
        }
    }
}
