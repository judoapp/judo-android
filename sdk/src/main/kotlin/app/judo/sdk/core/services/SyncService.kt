package app.judo.sdk.core.services

import app.judo.sdk.core.data.SyncResponse
import retrofit2.Response

internal interface SyncService {
    suspend fun getSync(aURL: String): Response<SyncResponse>
}