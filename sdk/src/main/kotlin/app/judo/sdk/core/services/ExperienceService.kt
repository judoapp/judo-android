package app.judo.sdk.core.services

import app.judo.sdk.api.models.Experience
import retrofit2.Response

internal interface ExperienceService {

    suspend fun getExperience(aURL: String, skipCache: Boolean = false): Response<Experience>

}