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

import android.net.Uri
import app.judo.sdk.compose.ui.URLRequest
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import java.net.URL

@JsonClass(generateAdapter = true)
class Authorizer(
    var pattern: String,
    var method: Method,
    var key: String,
    var value: String
) {
    enum class Method(val code: String) {
        HEADER("header"),

        QUERY_STRING("queryString")
    }

    class AuthorizerMethodAdapter {
        @ToJson
        fun toJson(method: Method) = method.code

        @FromJson
        fun fromJson(method: String) = Method.values().find { it.code == method } ?: throw RuntimeException("Incorrect Authorizer Method value")
    }

    fun authorize(request: URLRequest) {
        val host = URL(request.url).host

        val wildcardAndRoot = pattern.split("*.")
        if (wildcardAndRoot.size > 2) return

        val root = wildcardAndRoot.lastOrNull() ?: return

        val hasWildcard = wildcardAndRoot.size > 1

        if ((!hasWildcard && host == pattern) || (hasWildcard && (host == root || host.endsWith(".$root")))) {
            when (method) {
                Method.HEADER -> {
                    request.headers[key] = value
                }
                Method.QUERY_STRING -> {
                    val builder = Uri.parse(request.url).buildUpon()
                    builder.appendQueryParameter(key, value)
                    request.url = builder.toString()
                }
            }
        }
    }
}
