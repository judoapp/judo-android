package app.judo.sdk.core.robots

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
internal class KeyValueCacheRobot : AbstractTestRobot(){

    fun createString(keyValuePair: Pair<String, String>): Boolean {
        return environment.keyValueCache.putString(keyValuePair)
    }

    fun retrieveString(key: String): String? {
        return environment.keyValueCache.retrieveString(key)
    }

}
