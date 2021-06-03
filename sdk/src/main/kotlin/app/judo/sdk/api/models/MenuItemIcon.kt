package app.judo.sdk.api.models

import app.judo.sdk.core.lang.Interpolator
import com.squareup.moshi.JsonClass

sealed class MenuItemIcon : Visitable {

    @JsonClass(generateAdapter = true)
    data class AnImage(
        val imageURL: String
    ) : MenuItemIcon() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visit(this)
        }

        @Transient
        internal var interpolator: Interpolator? = null

        internal val interpolatedImageURL: String
            get() {
                return interpolator?.interpolate(imageURL) ?: imageURL
            }

    }

    @JsonClass(generateAdapter = true)
    data class AnIcon(
        val icon: Icon
    ) : MenuItemIcon() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visit(this)
        }
    }

}

