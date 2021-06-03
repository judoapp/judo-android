package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Experience(
    val id: String,
    val version: Int,
    val revisionID: Int,
    val name: String? = null,
    val nodes: List<Node>,
    val screenIDs: List<String>,
    val initialScreenID: String,
    val appearance: Appearance,
    val fonts: List<FontResource> = emptyList(),
    val localization: Map<String, Map<String, String>> = emptyMap()
) : Visitable {

    /**
     * Get all the [NODE_TYPE] nodes in this judo if it has any.
     *
     * This is a computed getter property.
     *
     * WARNING: This is a shallow get and only get nodes one level deep in the [nodes] list.
     */
    inline fun <reified NODE_TYPE> nodes(): List<NODE_TYPE> {
        return nodes.filterIsInstance<NODE_TYPE>()
    }

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

}
