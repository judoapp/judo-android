package app.judo.sdk.core.repositories

import android.graphics.Typeface
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Font

interface TypeFaceRepository {
    suspend fun loadTypefacesFor(experience: Experience)
    fun retrieveForCustomFont(font: Font.Custom): Typeface?
}