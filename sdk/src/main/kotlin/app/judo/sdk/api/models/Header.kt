package app.judo.sdk.api.models

import app.judo.sdk.core.lang.Interpolator
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Header(
    val key: String,
    val value: String
) {

    @Transient
    internal var interpolator: Interpolator? = null

    internal val interpolatedKey: String
        get() {
            return interpolator?.interpolate(key) ?: key
        }

    internal val interpolatedValue: String
        get() {
            return interpolator?.interpolate(value) ?: value
        }

}