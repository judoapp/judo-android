package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Border(
    val color: ColorVariants,
    val width: Float
): Visitable {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}