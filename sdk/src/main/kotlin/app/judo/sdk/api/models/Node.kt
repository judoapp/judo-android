package app.judo.sdk.api.models

import com.squareup.moshi.Json

interface Node : Visitable {
    val id: String
    val name: String?
    val metadata: Metadata?

    @Json(name = "__type")
    val typeName: String
}