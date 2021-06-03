package app.judo.sdk.core.implementations

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.environment.Environment

internal class KeyValueCacheImpl(
    private val context: Context
) : KeyValueCache {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            Environment.Keys.PREFERENCES_NAME,
            MODE_PRIVATE
        )
    }

    override fun putString(keyValuePair: Pair<String, String>): Boolean {
        return sharedPreferences.edit().apply {
            putString(keyValuePair.first, keyValuePair.second)
        }.commit()
    }

    override fun retrieveString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

}