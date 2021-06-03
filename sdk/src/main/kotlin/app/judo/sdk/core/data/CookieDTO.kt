package app.judo.sdk.core.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CookieDTO(
    val name: String = "",
    val value: String = "",
    val expiresAt: Long = 0L,
    val domain: String = "",
    val path: String = "",
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val persistent: Boolean = true,
    val hostOnly: Boolean = false,
)