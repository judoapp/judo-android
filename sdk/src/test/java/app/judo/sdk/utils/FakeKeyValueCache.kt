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
}