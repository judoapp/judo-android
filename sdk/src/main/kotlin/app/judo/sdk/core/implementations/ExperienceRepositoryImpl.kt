/*
 * Copyright (c) 2020-present, Rover Labs, Inc. All rights reserved.
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Rover.
 *
 * This copyright notice shall be included in all copies or substantial portions of
 * the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package app.judo.sdk.core.implementations

import android.os.Build
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.Authorizer
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.FontResource
import app.judo.sdk.core.data.ExperienceTree
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.repositories.ExperienceTreeRepository
import app.judo.sdk.core.services.ExperienceService
import app.judo.sdk.core.services.FontResourceService
import app.judo.sdk.core.utils.TranslationLoader
import app.judo.sdk.core.utils.TypeFaceLoader
import app.judo.sdk.core.utils.visitorsOf
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.IOException
import java.util.*

internal class ExperienceRepositoryImpl(
    private val experienceServiceSupplier: () -> ExperienceService,
    private val fontResourceServiceSupplier: () -> FontResourceService,
    private val loggerSupplier: () -> Logger? = { null }
) : ExperienceTreeRepository {

    companion object {
        private const val TAG = "ExperienceRepositoryImpl"
    }

    private val inMemoryExperiences = mutableMapOf<String, Experience>()

    private suspend fun createTypeFaceLoader(fonts: List<FontResource>, ignoreCache: Boolean): TypeFaceLoader {
        fontResourceServiceSupplier().let { faceService ->

            val typefaceMap = faceService.getTypefacesFor(fonts, ignoreCache)

            return TypeFaceLoader(typefaces = typefaceMap)
        }
    }

    private fun createTranslationLoader(experience: Experience): TranslationLoader {
        experience.localization.let { translations ->

            val translator = TranslatorImpl(theTranslationMap = translations) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    listOf(Locale.getDefault().toLanguageTag())
                } else {
                    emptyList()
                }
            }

            return TranslationLoader(translator = translator::translate)

        }
    }

    private val authorizersOverrides = mutableMapOf<String, List<Authorizer>>()

    override fun retrieveExperience(
        aURL: String,
        ignoreCache: Boolean,
    ): Flow<Resource<Experience, ExperienceError>> {
        return flow {
            val logger = loggerSupplier()
            val service = experienceServiceSupplier()
            emit(Resource.Loading())
            val response = service.getExperience(aURL, ignoreCache)
            if (response.isSuccessful && response.body() != null) {

                val experience = response.body()!!

                val typeFaceLoader = createTypeFaceLoader(experience.fonts, ignoreCache)

                val translationLoader = createTranslationLoader(experience)

                experience.accept(visitorsOf(typeFaceLoader, translationLoader))

                emit(Resource.Success(experience))

            } else {
                val error = when (response.code()) {
                    404 -> {
                        ExperienceError.ExperienceNotFoundError(message = ErrorMessages.EXPERIENCE_NOT_FOUND)
                    }

                    // TODO: 2021-03-23 - Add UnauthorizedAccessError for 401 responses

                    else -> {
                        ExperienceError.UnexpectedError(message = ErrorMessages.UNKNOWN_ERROR)
                    }
                }

                emit(Resource.Error(error = error))
            }
        }.catch {
            val error = when (it) {
                is IOException -> {
                    ExperienceError.NetworkError(
                        message = it.message,
                        cause = it
                    )
                }

                is JsonDataException, is JsonEncodingException -> {
                    ExperienceError.MalformedExperienceError(
                        message = it.message,
                        cause = it
                    )
                }

                else -> {
                    ExperienceError.UnexpectedError(
                        message = it.message,
                        cause = it
                    )
                }
            }
            emit(Resource.Error(error))
        }
    }

    override fun put(experience: Experience, key: String?, authorizers: List<Authorizer>): Experience? {
        val experienceKey = key ?: experience.id
        authorizersOverrides.put(experienceKey, authorizers)
        return inMemoryExperiences.put(experienceKey, experience)
    }

    override fun remove(key: String) {
        authorizersOverrides.remove(key)
        inMemoryExperiences.remove(key)
    }

    override suspend fun retrieveById(key: String): Experience? {

        return inMemoryExperiences[key]?.let { experience ->
            val typeFaceLoader = createTypeFaceLoader(experience.fonts, false)

            val translationLoader = createTranslationLoader(experience)

            experience.accept(visitorsOf(typeFaceLoader, translationLoader))

            experience
        }
    }

    override fun retrieveAuthorizersOverrideById(key: String): List<Authorizer>? {
        return authorizersOverrides[key]
    }
}