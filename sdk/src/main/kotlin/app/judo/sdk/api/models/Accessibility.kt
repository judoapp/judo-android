package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Accessibility(
    val isHidden: Boolean,
    val label: String? = null,
    val sortPriority: Int? = null,
    val isHeader: Boolean,
    val isSummary: Boolean,
    val playsSound: Boolean,
    val startsMediaSession: Boolean
)