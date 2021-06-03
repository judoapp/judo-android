package app.judo.sdk.utils

import android.graphics.Typeface
import app.judo.sdk.api.models.FontResource
import app.judo.sdk.core.services.FontResourceService

class FakeFontResourceService(
    private val defaultTypeface: Typeface = Typeface.create("sans-serif", Typeface.NORMAL),
) : FontResourceService {

    override suspend fun getTypefacesFor(fonts: List<FontResource>, ignoreCache: Boolean): Map<String, Typeface> {
        val result = mutableMapOf<String, Typeface>()

        fonts.forEach { resource ->
            when (resource) {

                is FontResource.Collection -> {
                    resource.names.forEach { name ->
                        result[name] = defaultTypeface
                    }
                }

                is FontResource.Single -> {
                    result[resource.name] = defaultTypeface
                }
            }
        }

        return result.toMap()
    }
}