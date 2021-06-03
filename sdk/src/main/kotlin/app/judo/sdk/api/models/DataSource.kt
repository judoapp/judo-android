package app.judo.sdk.api.models

import app.judo.sdk.core.data.JsonDAO
import app.judo.sdk.core.lang.Interpolator
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataSource(
    val url: String,
    val childIDs: List<String>,
    val headers: List<Header>,
    val httpBody: String? = null,
    val httpMethod: HttpMethod,
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
) : Node {

    override val typeName: String = NodeType.DATA_SOURCE.code

    @Transient
    internal var jsonDAO: JsonDAO? = null

    @Transient
    internal var interpolator: Interpolator? = null

    internal val interpolatedURL: String
        get() = interpolator?.interpolate(url) ?: url

    internal val interpolatedHttpBody: String?
        get() = httpBody?.let { interpolator?.interpolate(it) } ?: httpBody

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

}