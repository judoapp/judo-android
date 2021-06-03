package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NavBarAppearance(
    val standardConfiguration: NavBarAppearanceConfiguration? = null
)