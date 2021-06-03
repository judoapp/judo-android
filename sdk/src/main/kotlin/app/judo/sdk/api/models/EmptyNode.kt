package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class EmptyNode : Node {

    override val id: String = "empty"
    override val name: String? = null
    override val metadata: Metadata? = null

    override val typeName: String = "EmptyNode"

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}