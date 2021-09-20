package app.judo.sdk.core.interpolation

import app.judo.sdk.core.lang.Interpolator

internal class ReplaceHelper : Interpolator.Helper {
    override fun invoke(data: Any, arguments: List<String>?): String {

        val oldValue = arguments?.getOrNull(0) ?: return "$data"

        val newValue = arguments.getOrNull(1) ?: return "$data"

        return "$data".replace(
            oldValue = oldValue,
            newValue = newValue
        )

    }
}