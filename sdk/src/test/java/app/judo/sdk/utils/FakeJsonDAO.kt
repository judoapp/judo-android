package app.judo.sdk.utils

import app.judo.sdk.core.data.JsonDAO

internal class FakeJsonDAO(
    private val json: String
) : JsonDAO {

    override fun findValueByKeys(keys: List<String>): String? {
        val names = ('A'..'J')

        keys.forEach(::println)

        return if (keys.last() == "firstName") {
            names.random().toString()
        } else {
            null
        }

    }

    override fun findArrayByKey(key: String): List<JsonDAO> {

        val fakeDataDAOs = ('A'..'J').map { name ->
            FakeJsonDAO("$name")
        }

        return if (key == "data") {
            fakeDataDAOs
        } else {
            emptyList()
        }
    }

}
