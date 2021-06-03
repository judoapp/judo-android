package app.judo.sdk.core.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SyncResponse(
    val data: List<SyncData>,
    val nextLink: String
)

@JsonClass(generateAdapter = true)
internal data class SyncData(
    val url: String,
    val removed: Boolean,
    val priority: Int
)