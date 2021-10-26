package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.Condition
import app.judo.sdk.api.models.Conditional
import app.judo.sdk.api.models.Predicate.*
import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.fromKeyPath
import app.judo.sdk.core.lang.Interpolator
import org.json.JSONObject

internal fun Conditional.resolve(dataContext: DataContext, interpolator: Interpolator? = null): Boolean {
    return conditions.resolve(dataContext, interpolator)
}

internal fun List<Condition>.resolve(dataContext: DataContext, interpolator: Interpolator? = null): Boolean {

    if (isEmpty()) return true

    return fold(initial = true) { accumulator, condition ->
        if (!accumulator) false else {
            condition.resolve(dataContext, interpolator)
        }
    }
}

internal fun Condition.resolve(dataContext: DataContext, interpolator: Interpolator? = null): Boolean {

    val lhs = dataContext.fromKeyPath(keyPath)
    val rhs = (value as? String)?.let {
        interpolator?.interpolate(it, dataContext)
    } ?: value

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
            lhs != "null" && lhs != JSONObject.NULL && lhs != null
        }

        IS_NOT_SET -> {
            lhs == "null" || lhs == JSONObject.NULL || lhs == null
        }

        IS_TRUE -> {
            "$lhs".equals("true", ignoreCase = true)
        }

        IS_FALSE -> {
            "$lhs".equals("false", ignoreCase = true)
        }
    }

}