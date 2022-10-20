package app.judo.sdk.api.models

import java.net.URL

class Authorizer(
    private val pattern: String,
    private val block: (URLRequest) -> Unit
) {
    fun authorize(request: URLRequest) {
        val host = URL(request.url).host

        val wildcardAndRoot = pattern.split("*.")
        if(wildcardAndRoot.size > 2) return

        val root = wildcardAndRoot.lastOrNull() ?: return

        val hasWildcard = wildcardAndRoot.size > 1

        if((!hasWildcard && host == pattern) || (hasWildcard && (host == root || host.endsWith(".$root")))) {
            block(request)
        }
    }
}
