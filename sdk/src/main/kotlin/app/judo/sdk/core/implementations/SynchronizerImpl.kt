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

import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.Environment.RegexPatterns
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.core.sync.Synchronizer
import app.judo.sdk.core.utils.ImageURLExtractor
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList

/**
 * Orchestrates the synchronization of Experiences across
 * the different services of the SDK.
 */
internal class SynchronizerImpl(
    private val environment: Environment,
) : Synchronizer {

    companion object {
        private const val TAG = "Synchronizer"
    }

    override suspend fun performSync(onComplete: () -> Unit) {
        try {
            environment.syncRepository.retrieveSync(
                environment.configuration.domain
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        environment.logger.v(
                            tag = TAG,
                            data = "Loading Sync Data"
                        )
                    }
                    is Resource.Success -> {
                        handleSyncResponse(resource.data)
                    }
                    is Resource.Error -> {
                        environment.logger.e(
                            tag = TAG,
                            message = "Error Synchronizing:\n${resource.error.message}",
                            error = resource.error
                        )
                    }
                }
            }
        } catch (error: Throwable) {
            environment.logger.e(
                tag = TAG,
                message = "Error Synchronizing:\n${error.message}",
                error = error
            )
        } finally {

            environment.logger.v(
                tag = TAG,
                data = "Sync Completed"
            )
            onComplete()

        }

    }

    private suspend fun handleSyncResponse(
        syncResponse: SyncResponse
    ) {

        // Extract the URLs of the Experiences that need to be synced
        val urls: List<String> = syncResponse.data.map { syncData -> syncData.url }

        // Sync the experiences and extract the successful results
        val experienceFlows: List<Flow<Experience>> = urls.map { url ->
            environment.experienceRepository.retrieveExperience(
                aURL = url,
                ignoreCache = true
            ).mapNotNull { experienceResource: Resource<Experience, Throwable> ->
                // Transform the Flow<Resource> into a Flow<Experience>
                // Then strip out the nulls to get a clean list
                when (experienceResource) {
                    is Resource.Loading -> {
                        environment.logger.v(
                            tag = TAG,
                            data = "Loading experience for:\n$url"
                        )
                        null
                    }
                    is Resource.Success -> {
                        environment.logger.v(
                            tag = TAG,
                            data = "Fetched experience:\n${experienceResource.data}"
                        )
                        experienceResource.data
                    }
                    is Resource.Error -> {
                        environment.logger.e(
                            tag = TAG,
                            message = "Error loading experience:\nURL: $url\nError: ${experienceResource.error.message}",
                            error = experienceResource.error
                        )
                        null
                    }
                }
            }
        }

        val listOfExperiences: List<Experience> =
            experienceFlows.flatMap { flow: Flow<Experience> ->
                flow.toList()
            }
    }

    /**
     * No longer used nor exposed, but maintained for reference until this feature is replaced with
     * a new approach.
     */
    private suspend fun fetchAssets(assetUrls: Set<String>) {
        assetUrls.filterNot { url ->
            url.contains(RegexPatterns.HANDLE_BAR_EXPRESSION_PATTERN)
        }.map { url ->
            environment.imageService.getImageAsync(ImageService.Request(url))
        }.awaitAll().forEach { result ->
            when (result) {
                is ImageService.Result.Success -> {
                    environment.logger.v(
                        tag = TAG,
                        data = "Fetched experience image: ${result.request.url}"
                    )
                }
                is ImageService.Result.Error -> {
                    environment.logger.e(
                        tag = TAG,
                        message = "Image failed to load: ${result.request.url}",
                        error = result.error
                    )
                }
            }

        }
    }

}
