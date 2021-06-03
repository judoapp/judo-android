package app.judo.sdk.core.repositories

import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.data.SyncResponse
import kotlinx.coroutines.flow.Flow

internal interface SyncRepository {
    fun retrieveSync(aURL: String): Flow<Resource<SyncResponse, Throwable>>
}
