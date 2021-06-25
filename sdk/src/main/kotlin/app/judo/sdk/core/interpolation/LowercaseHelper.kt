package app.judo.sdk.core.interpolation

import app.judo.sdk.core.lang.Interpolator
import java.util.*

internal class LowercaseHelper : Interpolator.Helper {
    override fun invoke(data: Any?, argument: String?): String {
        return "$data".toLowerCase(Locale.getDefault())
    }
}