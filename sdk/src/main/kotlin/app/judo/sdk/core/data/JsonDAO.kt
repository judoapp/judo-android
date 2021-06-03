package app.judo.sdk.core.data

interface JsonDAO {

    fun findValueByKeys(keys: List<String>): String?

    fun findValueByKey(key: String): String? {
        return findValueByKeys(listOf(key))
    }

    fun findArrayByKey(key: String): List<JsonDAO>

    fun value(): String? {
        return findValueByKeys(emptyList())
    }

}
