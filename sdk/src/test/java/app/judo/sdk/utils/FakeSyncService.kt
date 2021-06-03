package app.judo.sdk.utils

import app.judo.sdk.core.data.SyncData
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.services.SyncService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

internal class FakeSyncService : SyncService {

    var responseCode: Int = 200
    var nextLink = """https://test1.judo.app/sync?cursor=MjAyMC0xMS0yMFQxNjo0NDozNi44ODBa"""
    var dataUrl = """https://test1.judo.app/testexperience"""
    var onNext: () -> Unit = {}
    val syncResponse = SyncResponse(
        listOf(
            SyncData(dataUrl, false, 10)
        ), nextLink
    )

    override suspend fun getSync(aURL: String): Response<SyncResponse> {
        onNext()
        return when (responseCode) {
            200 -> Response.success(
                syncResponse
            )
            201 -> Response.success(
                null
            )
            else ->  Response.error(
                responseCode, "".toResponseBody("application/json".toMediaType())
            )
        }
    }

}
