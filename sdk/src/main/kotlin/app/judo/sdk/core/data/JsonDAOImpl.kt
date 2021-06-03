package app.judo.sdk.core.data

import org.json.JSONArray
import org.json.JSONObject

class JsonDAOImpl(
    json: String
) : JsonDAO {

    private val obj = JSONObject(json)

    override fun findValueByKeys(keys: List<String>): String? {

        return try {
            when {
                keys.size > 1 -> {
                    val json = obj.opt(keys.first())?.toString()
                    json?.let { JsonDAOImpl(it).findValueByKeys(keys.drop(1)) }
                }
                keys.size == 1 -> {
                    obj.opt(keys.first())?.toString()
                }
                else -> {
                    obj.toString()
                }
            }
        } catch (e: Throwable) {
            null
        }

    }

    override fun findArrayByKey(key: String): List<JsonDAO> {
        return try {

            val keys: List<String> = key.split (".")

            var next: JSONObject? = obj

            var result: JSONArray? = null

            keys.forEachIndexed { index, theKey ->
                if (index == keys.lastIndex) {
                    result = next?.optJSONArray(theKey)
                } else {
                    next = next?.optJSONObject(theKey)
                }
            }

            val length = result?.length() ?: 0

            (0..length).mapNotNull { index ->
                result?.optJSONObject(index)?.toString()?.let { json ->
                    JsonDAOImpl(json)
                }
            }

        } catch (e: Throwable) {
            emptyList()
        }
    }

}