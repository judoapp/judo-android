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

typealias DataContext = Map<String, Any?>

internal fun makeDataContext(
    userInfo: Map<String, Any>,
    urlParameters: Map<String, String>,
    data: Any? = null
): DataContext {
    return hashMapOf<String, Any?>(
        Pair(Keyword.USER.value, userInfo),
        Pair(Keyword.URL.value, urlParameters),
        Pair(Keyword.DATA.value, data)
    )
}

internal tailrec fun DataContext.fromKeyPath(keyPath: List<String>): Any? {

    if (keyPath.isEmpty()) {
        return null
    }

    if (keyPath.size == 1) {
        return this[keyPath.first()]
    }

    val head = keyPath.first()

    val value = this[head] ?: return null

    if (value !is Map<*, *>) {
        return value
    }

    if (value.isEmpty()) return null

    if (value.keys.firstOrNull() !is String) {
        return value
    }

    @Suppress("UNCHECKED_CAST")
    return (value as Map<String, *>).fromKeyPath(keyPath.drop(1))
}

internal fun DataContext.fromKeyPath(keyPath: String): Any? {
    return fromKeyPath(keyPath.split('.'))
}

internal fun DataContext.arrayFromKeyPath(keyPath: String): List<Any?> {
    return arrayFromKeyPath(keyPath.split('.'))
}

internal fun DataContext.arrayFromKeyPath(keyPath: List<String>): List<Any?> {
    return fromKeyPath(keyPath) as? List<Any?> ?: emptyList()
}

internal val DataContext.urlParameters: Map<String, String>
    get() {
        @Suppress("UNCHECKED_CAST")
        return (this["url"] as? Map<String, String>) ?: emptyMap()
    }

internal val DataContext.data: Any?
    get() = this["data"]

internal val DataContext.userInfo: Map<String, Any>
    @Suppress("UNCHECKED_CAST")
    get() = (this["user"] as? Map<String, Any>) ?: emptyMap()
