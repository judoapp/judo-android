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

package app.judo.sdk.compose.ui.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal fun resolveJson(json: String): Any? {

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

    // unsupported data appeared. We don't support bare primitive value types (yet).
    return null
}

@Throws(JSONException::class)
private fun jsonToMap(json: JSONObject): Map<String, Any?> {

    var result = mapOf<String, Any?>()

    if (json !== JSONObject.NULL) {
        result = toMap(json)
    }

    return result
}

@Throws(JSONException::class)
private fun toMap(jsonObject: JSONObject): Map<String, Any?> {

    val result: MutableMap<String, Any?> = java.util.HashMap()
    val keysIterator = jsonObject.keys()

    while (keysIterator.hasNext()) {

        val key = keysIterator.next()
        var value = jsonObject[key]

        if (value is JSONArray) {
            value = value.toList()
        } else if (value is JSONObject) {
            value = toMap(value)
        }

        result[key] = value
    }

    return result
}

@Throws(JSONException::class)
private fun JSONArray.toList(): List<Any> {

    val result = mutableListOf<Any>()

    for (i in 0 until length()) {

        var value = this[i]

        if (value is JSONArray) {
            value = value.toList()
        } else if (value is JSONObject) {
            value = toMap(value)
        }

        result.add(value)
    }

    return result
}