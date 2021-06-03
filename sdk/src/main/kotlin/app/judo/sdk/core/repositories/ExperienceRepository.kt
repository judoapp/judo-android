package app.judo.sdk.core.repositories

import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.data.Resource
import kotlinx.coroutines.flow.Flow

internal interface ExperienceRepository {

    fun retrieveExperience(
        aURL: String,
        ignoreCache: Boolean = false
    ): Flow<Resource<Experience, ExperienceError>>

    /**
     * Caches a Experience into memory.
     * The judo is associated to a given [key] if no key is passed
     * then the [Experience.id] is used instead.
     * @return The previous Experience associated to the [key] or null if there is none.
    * */
    fun put(experience: Experience, key: String? = null): Experience?

    /**
     * Returns the value corresponding to the given [key], or `null` if such a key is not present.
     */
    fun retrieveById(key: String): Experience?

}