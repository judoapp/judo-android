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

import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.repositories.SyncRepository
import app.judo.sdk.core.services.SyncService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class SyncRepositoryImpl(
    private val ioDispatcherSupplier: () -> CoroutineDispatcher,
    private val syncServiceSupplier: () -> SyncService,
    private val keyValueCacheSupplier: () -> KeyValueCache,
) : SyncRepository {

    override fun retrieveSync(aURL: String): Flow<Resource<SyncResponse, Throwable>> {
        return flow {

            emit(Resource.Loading())

            val cache = keyValueCacheSupplier()
            val service = syncServiceSupplier()
            val nextLink = cache.retrieveString(aURL) ?: aURL
            val response = service.getSync(nextLink)
            val body = response.body()

            if (response.isSuccessful) {
                if (body == null) {
                    emit(
                        Resource.Error(
                            Throwable(message = "Empty response body")
                        )
                    )
                } else {
                    cache.putString(aURL to body.nextLink)
                    emit(
                        Resource.Success(
                            body
                        )
                    )
                }
            } else {
                val errorMessage = withContext(ioDispatcherSupplier()) {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    response.errorBody()?.string()
                }

                emit(
                    Resource.Error(
                        Throwable(message = "Failed, status code ${response.code()}: $errorMessage")
                    )
                )
            }
        }.catch { exception ->
            emit(Resource.Error(exception))
        }
    }

}

