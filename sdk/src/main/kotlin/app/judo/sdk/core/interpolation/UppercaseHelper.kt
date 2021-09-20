package app.judo.sdk.core.interpolation

import app.judo.sdk.core.lang.Interpolator
import java.util.*

internal class UppercaseHelper : Interpolator.Helper {
    override fun invoke(data: Any, arguments: List<String>?): String {
        return "$data".toUpperCase(Locale.getDefault())
    }
}