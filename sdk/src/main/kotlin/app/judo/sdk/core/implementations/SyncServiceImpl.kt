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

import app.judo.sdk.BuildConfig
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.services.SyncService
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

internal class SyncServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val baseURLSupplier: () -> String?
) : SyncService {

    companion object {
        const val url: String = "https://127.0.0.1"
    }

    interface SyncAPI {
        @GET
        suspend fun getSync(
            @Url aURL: String,
            @Query("apiVersion") apiVersion: Int = BuildConfig.API_VERSION
        ): Response<SyncResponse>
    }

    private val api by lazy {

        Retrofit.Builder()
            .baseUrl(baseURLSupplier() ?: url)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(baseClientSupplier())
            .build().create(SyncAPI::class.java)
    }

    override suspend fun getSync(aURL: String): Response<SyncResponse> {
        val theURL = "${
            baseURLSupplier() ?: "https://"
        }${
            aURL.removePrefix("https://")
        }${
            "/sync".takeUnless { aURL.contains("/sync?") } ?: ""
        }"
        return api.getSync(aURL = theURL)
    }

}

