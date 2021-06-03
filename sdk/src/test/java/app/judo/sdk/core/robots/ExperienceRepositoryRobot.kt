package app.judo.sdk.core.robots

import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.data.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.mockwebserver.RecordedRequest

internal class ExperienceRepositoryRobot : AbstractTestRobot() {

    private var nextThrowable: Throwable? = null

    fun retrieveExperience(judoURL: String): Flow<Resource<Experience, Throwable>> {
        return environment.experienceRepository.retrieveExperience(judoURL)
    }

    fun throwOnNextRequest(throwable: Throwable) {
        nextThrowable = throwable
    }

    fun setResponseCodeTo(code: Int) {
        serverDispatcher.code = {
            code
        }
    }

    override fun onRequest(request: RecordedRequest) {
        nextThrowable?.let {
            nextThrowable = null
            throw it
        }
    }

    fun put(experience: Experience, key: String? = null): Experience? {
        return environment.experienceRepository.put(experience, key)
    }

    fun retrieveById(key: String): Experience? {
        return environment.experienceRepository.retrieveById(key = key)
    }

}
