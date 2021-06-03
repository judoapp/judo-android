package app.judo.sdk.core.cache

internal interface KeyValueCache {
    fun putString(keyValuePair: Pair<String, String>): Boolean
    fun retrieveString(key: String): String?
}