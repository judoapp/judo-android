package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

sealed class BarBackground : Visitable {

    @JsonClass(generateAdapter = true)
    @Suppress("CanSealedSubClassBeObject")
    class TransparentBarBackground : BarBackground() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visit(this)
        }
    }

    @JsonClass(generateAdapter = true)
    @Suppress("CanSealedSubClassBeObject")
    class TranslucentBarBackground : BarBackground() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visit(this)
        }
    }

    @JsonClass(generateAdapter = true)
    data class OpaqueBarBackground(
        val color: ColorVariants
    ) : BarBackground() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visit(this)
        }
    }

    @JsonClass(generateAdapter = true)
    data class ImageBarBackground(
        val imageUrl: String
    ) : BarBackground() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visit(this)
        }
    }

}
