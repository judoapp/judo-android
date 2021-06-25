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

package app.judo.sdk.utils

import app.judo.sdk.core.data.SyncData
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.services.SyncService
import okhttp3.MediaType
import okhttp3.ResponseBody
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
                responseCode, ResponseBody.create(MediaType.parse("application/json"), "")
            )
        }
    }

}
