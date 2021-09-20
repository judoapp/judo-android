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

package app.judo.sdk.core.repositories

import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.Authorizer
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
     * @param authorizers A list of [Authorizer]s to apply in lieu of the authorizers specified by a developer in [Judo.Configuration].
     * @param urlQueryParams A map of url query parameters to apply for in memory experiences
     * @return The previous Experience associated to the [key] or null if there is none.
    * */
    fun put(experience: Experience, key: String? = null, authorizers: List<Authorizer>, urlQueryParams: Map<String, String>): Experience?

    /**
     * Returns the value corresponding to the given [key], or `null` if such a key is not present.
     */
    suspend fun retrieveById(key: String): Experience?

    fun remove(key: String)

    fun retrieveAuthorizersOverrideById(key: String): List<Authorizer>?

    fun retrieveUrlQueryParametersById(key: String): Map<String, String>?
}

