package app.judo.sdk.core.services

import android.graphics.Typeface
import app.judo.sdk.api.models.FontResource

internal interface FontResourceService {

    suspend fun getTypefacesFor(fonts: List<FontResource>, ignoreCache: Boolean = false): Map<String, Typeface>

}