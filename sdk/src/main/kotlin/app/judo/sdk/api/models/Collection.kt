package app.judo.sdk.api.models

import app.judo.sdk.core.data.JsonDAO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Collection(
    override val id: String,
    override val metadata: Metadata? = null,
    val childIDs: List<String>,
    val filters: List<Any>,
    val dataKey: String,
    override val name: String? = null,
    val sortDescriptors: List<Any>
) : Node {

    override val typeName: String = NodeType.COLLECTION.code

    @Transient
    internal var jsonDAOs: List<JsonDAO>? = null

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

}