package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Shadow(
    val color: ColorVariants,
    val blur: Int,
    val x: Int,
    val y: Int
) : Visitable {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}