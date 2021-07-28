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

import app.judo.sdk.api.analytics.AnalyticsEvent
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.services.IngestService
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException

class IngestServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val ioDispatcherSupplier: () -> CoroutineDispatcher,
    private val loggerSupplier: () -> Logger,
): IngestService {
    companion object {
        const val TAG: String = "IngestServiceImpl"
        const val URL: String = "https://analytics.judo.app/"
    }

    interface IngestAPI {
        @POST("/batch")
        suspend fun postBatch(
            @Body body: BatchBody
        ): Response<Void>
    }

    @JsonClass(generateAdapter = true)
    data class BatchBody(
        val batch: List<AnalyticsEvent>
    )

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(MoshiConverterFactory.create(JsonParser.moshi))
            .client(baseClientSupplier())
            .build().create(IngestAPI::class.java)
    }

    override suspend fun submitBatch(events: List<AnalyticsEvent>): Boolean {
        return try {
            val response = api.postBatch(BatchBody(events))
            return when (response.code()) {
                400, 422 -> {
                    val errorMessage = withContext(ioDispatcherSupplier()) {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        response.errorBody()?.string()
                    }
                    loggerSupplier().e(TAG, "Unable to submit invalid analytics events, dropping them: ${errorMessage ?: "empty"}")
                    true
                }
                in 200..299 -> {
                    true
                }
                else -> {
                    // other statuses, such 5xx, are soft errors, and the events should be retried.
                    val errorMessage = withContext(ioDispatcherSupplier()) {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        response.errorBody()?.string()
                    }
                    loggerSupplier().e(TAG, "Unable to submit analytics events due to transient API error, maintaining them in the queue: ${errorMessage ?: "empty"}")
                    false
                }
            }
        } catch(exception: IOException) {
            loggerSupplier().e(TAG, "Unable to submit analytics events due to network error, maintaining them in the queue: ${exception.message}")
            false
        }
    }
}