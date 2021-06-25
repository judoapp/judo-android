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

package app.judo.sdk.core.data.resolvers

import app.judo.sdk.core.data.jsonToMap
import app.judo.sdk.core.data.toList
import app.judo.sdk.core.lang.*
import org.json.JSONArray
import org.json.JSONObject

fun resolveJson(json: String): Any? {

    if (json.isEmpty()) {
        return null
    }

    // Object?
    try {
        if (json.startsWith("{")) {
            return jsonToMap(JSONObject(json))
        }
    } catch (e: Throwable) {
        /* no-op */
    }

    // Array?
    try {
        if (json.startsWith("[")) {
            return JSONArray(json).toList()
        }
    } catch (e: Throwable) {
        /* no-op */
    }

    // String Literal?
    try {
        if (json.startsWith("\"") && json.endsWith("\"")) {
            return json.drop(1).dropLast(1)
        }
    } catch (e: Throwable) {
        /* no-op */
    }
    // String
    try {
        if (json.contains(' ')) {
            return json
        }
    } catch (e: Throwable) {
        /* no-op */
    }

    val parserContext = ParserContext(json, Unit)

    val parser = AnyOfParser<Unit, Any?>(
        LongParser(),
        IntegerParser(),
        DoubleParser(),
        BooleanParser(ignoreCase = false)
    )

    return when (val result = parser.parse(parserContext)) {
        is Parser.Result.Failure -> {
            // This could be an emoji or some other unicode character
            // This can be supported later on.
            null
        }
        is Parser.Result.Success -> {
            result.match.value
        }
    }

}

