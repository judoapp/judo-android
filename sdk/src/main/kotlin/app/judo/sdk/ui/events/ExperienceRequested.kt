package app.judo.sdk.ui.events

import android.content.Intent
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_KEY
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_URL
import app.judo.sdk.core.environment.Environment.Keys.IGNORE_CACHE
import app.judo.sdk.core.environment.Environment.Keys.LOAD_FROM_MEMORY
import app.judo.sdk.core.environment.Environment.Keys.SCREEN_ID
import java.net.URI
import java.util.*

internal data class ExperienceRequested(
    private val intent: Intent? = null,
    val loadFromMemory: Boolean = intent?.getBooleanExtra(LOAD_FROM_MEMORY, false) ?: false,
    val experienceKey: String? = intent?.getStringExtra(EXPERIENCE_KEY),
    val experienceURL: String? = intent?.data?.toString() ?: intent?.getStringExtra(EXPERIENCE_URL),
    val ignoreCache: Boolean = intent?.getBooleanExtra(IGNORE_CACHE, false) ?: false,
) {
    val screenId: String? by lazy {
        val id = intent?.getStringExtra(SCREEN_ID)

        if (id != null) return@lazy id

        val url = experienceURL
        if (url?.contains(SCREEN_ID) == true) {
            val uuidRegex = Regex("[^&;]*")
            val sub = url.substringAfter("?$SCREEN_ID=")
            uuidRegex.find(sub)?.value
        } else {
            null
        }
    }

    /**
     * Either the [experienceURL] minus the [screenId] respectively.
     *
     * When launching a [Experience] from a URL this is the property that should be used.
     */
    val experienceURLForRequest: String? by lazy {
        experienceURL?.let { uriString ->
            val uri = URI.create(uriString)
            if(arrayOf("http", "https").contains(uri.scheme?.toLowerCase(Locale.ROOT))) {
                // An Experience link was opened directly, just exclude any query parameters (eg screenID):
                URI(uri.scheme, uri.authority, uri.path, null, uri.fragment).toString()
            } else {
                // An Experience link (wrapped as a deep link with a custom scheme) was opened directly.
                // Replace the scheme with "https", and exclude the query parameters.
                URI("https", uri.authority, uri.path, null, uri.fragment).toString()
            }
        }
    }
}