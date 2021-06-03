package app.judo.sdk.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusBarAppearance(
    @Json(name = "androidStyle")
    val statusBarStyle: StatusBarStyle,
    val backgroundColor: ColorVariants
)