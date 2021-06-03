package app.judo.sdk.core.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationRequestBody(
    val deviceID: String,
    val deviceToken: String,
    val environment: String? = null
)
