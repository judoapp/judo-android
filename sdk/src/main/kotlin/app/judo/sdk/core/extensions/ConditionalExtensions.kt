package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.Condition
import app.judo.sdk.api.models.Conditional
import app.judo.sdk.api.models.Predicate.*
import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.fromKeyPath
import org.json.JSONObject

internal fun Conditional.resolve(dataContext: DataContext): Boolean {
    return conditions.resolve(dataContext)
}

internal fun List<Condition>.resolve(dataContext: DataContext): Boolean {

    if (isEmpty()) return true

    return fold(initial = true) { accumulator, condition ->
        if (!accumulator) false else {
            condition.resolve(dataContext)
        }
    }
}

internal fun Condition.resolve(dataContext: DataContext): Boolean {

    val lhs = dataContext.fromKeyPath(keyPath)
    // TODO: 2021-07-20 Interpolate the value
    val rhs = /*(value as? String)?.let {
        interpolator(it, dataContext)
    } ?: return false*/ value ?: return false

    return when (predicate) {
        EQUALS -> {
            when (rhs) {
                is String -> {
                    (lhs as? String)?.let {
                        it == rhs
                    } ?: false
                }

                is Number -> {
                    (lhs as? Number)?.toDouble()?.let {
                        it == rhs.toDouble()
                    } ?: false
                }

                else -> false
            }
        }

        DOES_NOT_EQUAL -> {
            when (rhs) {

                is String -> {
                    (lhs as? String)?.let {
                        it != rhs
                    } ?: false
                }

                is Number -> {
                    (lhs as? Number)?.toDouble()?.let {
                        it != rhs.toDouble()
                    } ?: false
                }

                else -> false

            }
        }

        IS_GREATER_THAN -> {
            if (rhs is Number) {
                (lhs as? Number)?.toDouble()?.let {
                    it > rhs.toDouble()
                } ?: false
            } else {
                false
            }
        }

        IS_LESS_THAN -> {
            if (rhs is Number) {
                (lhs as? Number)?.toDouble()?.let {
                    it < rhs.toDouble()
                } ?: false
            } else {
                false
            }
        }

        IS_SET -> {
            lhs != "null" && lhs != JSONObject.NULL
        }

        IS_NOT_SET -> {
            lhs == "null" || lhs == JSONObject.NULL
        }

        IS_TRUE -> {
            "$lhs".equals("true", ignoreCase = true)
        }

        IS_FALSE -> {
            "$lhs".equals("false", ignoreCase = true)
        }
    }

}