package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.Condition
import app.judo.sdk.api.models.Conditional
import app.judo.sdk.api.models.Predicate.*
import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.fromKeyPath
import app.judo.sdk.core.data.resolvers.resolveJson
import org.json.JSONObject

internal fun Conditional.resolve(dataContext: DataContext): Boolean {
    return conditions.resolve(dataContext)
}

internal fun List<Condition>.resolve(dataContext: DataContext): Boolean {

    if (isEmpty()) return true

    return !map { condition ->

        val lhs = dataContext.fromKeyPath(condition.keyPath)
        val rhs = condition.value

        when (condition.predicate) {
            EQUALS -> {
                return "$lhs" == "$rhs"
            }
            DOES_NOT_EQUAL -> {
                return "$lhs" != "$rhs"
            }
            IS_GREATER_THAN -> {
                return try {

                    "$lhs".toDouble() > "$rhs".toDouble()

                } catch (_: Throwable) {
                    return false
                }
            }
            IS_LESS_THAN -> {
                return try {

                    "$lhs".toDouble() < "$rhs".toDouble()

                } catch (_: Throwable) {
                    return false
                }
            }

            IS_SET -> {
                lhs != null && lhs != "null" && lhs != JSONObject.NULL
            }
            IS_NOT_SET -> {
                lhs == null || lhs == "null" || lhs != JSONObject.NULL
            }

            IS_TRUE -> {
                "$lhs".equals("true", ignoreCase = true)
            }

            IS_FALSE -> {
                "$lhs".equals("false", ignoreCase = true)
            }
        }


    }.contains(false)

}