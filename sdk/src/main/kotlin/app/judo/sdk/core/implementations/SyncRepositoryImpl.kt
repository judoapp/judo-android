package app.judo.sdk.core.implementations

import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.repositories.SyncRepository
import app.judo.sdk.core.services.SyncService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

internal class SyncRepositoryImpl(
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

            if (response.isSuccessful && body != null) {
                cache.putString(aURL to body.nextLink)
                emit(
                    Resource.Success(
                        body
                    )
                )
            } else {
                emit(
                    Resource.Error(
                        Throwable(message = response.message())
                    )
                )
            }

        }.catch { exception ->
            emit(Resource.Error(exception))
        }
    }

}

