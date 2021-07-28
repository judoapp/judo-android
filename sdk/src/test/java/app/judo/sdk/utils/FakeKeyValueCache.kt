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

package app.judo.sdk.utils

import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.log.Logger

internal class FakeKeyValueCache(
    private val logger: Logger? = null
) : KeyValueCache {

    companion object {
        private const val TAG = "FakeKeyValueCache"
    }

    private val pairs = mutableMapOf<String, String>()

    override fun putString(keyValuePair: Pair<String, String>): Boolean {
        logger?.d(TAG, "Caching:\n\t$keyValuePair")
        pairs[keyValuePair.first] = keyValuePair.second
        return true
    }

    override fun retrieveString(key: String): String? {
        logger?.d(TAG, "Retrieving:\n\t$key")
        val result = pairs[key]
        logger?.d(TAG, "Returning:\n\t$result")
        return result
    }

    override fun remove(key: String): Boolean {
        logger?.d(TAG, "Removing:\n\t$key")
        pairs.remove(key)
        return true
    }
}