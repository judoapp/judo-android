package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.HttpMethod
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class HttpMethodJsonAdapter {
    @ToJson
    fun toJson(method: HttpMethod) = method.name

    @FromJson
    fun fromJson(method: String) =
        HttpMethod.values().find { it.name == method } ?: throw RuntimeException("Incorrect method")
}