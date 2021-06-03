package app.judo.sdk.core.lang

import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.core.data.JsonDAO

internal interface Interpolator {

    var jsonDAO: JsonDAO?

    val userDataSupplier: UserDataSupplier

    fun interpolate(theTextToInterpolate: String): String?

}