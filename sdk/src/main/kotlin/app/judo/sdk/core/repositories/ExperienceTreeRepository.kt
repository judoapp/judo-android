package app.judo.sdk.core.repositories

import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.core.data.ExperienceTree
import app.judo.sdk.core.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal interface ExperienceTreeRepository: ExperienceRepository {
    fun retrieveExperienceTree(
        aURL: String,
        ignoreCache: Boolean = false
    ): Flow<Resource<ExperienceTree, ExperienceError>> {
        return retrieveExperience(aURL, ignoreCache).map { resource ->
            when (resource) {

                is Resource.Error -> {
                    Resource.Error(resource.error)
                }

                is Resource.Loading -> {
                    val tree = resource.cacheData?.let { experience -> ExperienceTree(experience) }
                    Resource.Loading(tree)
                }

                is Resource.Success -> {
                    Resource.Success(ExperienceTree(resource.data))
                }

            }
        }
    }

    suspend fun retrieveTreeById(key: String): ExperienceTree? {
        return retrieveById(key)?.let { ExperienceTree(it) }
    }
}