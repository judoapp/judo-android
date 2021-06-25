package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Conditional(
    override val id: String,
    val conditions: List<Condition> = emptyList(),
    val childIDs: List<String> = emptyList(),
    override val name: String? = null,
    override val metadata: Metadata? = null,
) : NodeContainer {

    override fun getChildNodeIDs(): List<String> {
        return childIDs
    }

    override val typeName = NodeType.CONDITIONAL.code

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

}
