package app.judo.sdk.api.models

/**
 * A mutable description of the configuration of an outbound HTTP API request to a Data Source.
 */
data class URLRequest(
    var url: String,
    var method: HttpMethod,
    var headers: HashMap<String, String> = hashMapOf(),
    var body: String? = null
)
