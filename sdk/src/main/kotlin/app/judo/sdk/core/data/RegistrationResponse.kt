package app.judo.sdk.core.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResponse(
    val appId: Int,
    val deviceToken: String,
    val isProduction: Boolean
)