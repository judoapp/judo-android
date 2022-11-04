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

import app.judo.sdk.api.models.Authorizer
import app.judo.sdk.api.models.HttpMethod
import app.judo.sdk.api.models.URLRequest
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.services.DataSourceService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

@Suppress("BlockingMethodInNonBlockingContext")
internal class DataSourceServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val baseURLSupplier: () -> String?,
    private val loggerSupplier: () -> Logger? = { null },
    private val authorizersSupplier: () -> List<Authorizer> = { emptyList() }
) : DataSourceService {

    companion object {
        /**
         * This is just a place holder URL (needed to configure Retrofit) and will be replaced
         * during actual calls.
         */
        private const val url: String = "https://placeholder.judo.app"
    }

    interface DataSourceAPI {
        @GET
        suspend fun get(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
        ): Response<ResponseBody>

        @PUT
        suspend fun put(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
        ): Response<ResponseBody>

        @PUT
        suspend fun putWithBody(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
            @Body body: RequestBody,
        ): Response<ResponseBody>

        @POST
        suspend fun post(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
        ): Response<ResponseBody>

        @POST
        suspend fun postWithBody(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
            @Body body: RequestBody,
        ): Response<ResponseBody>

    }

    private val api by lazy {

        Retrofit.Builder()
            .baseUrl(baseURLSupplier() ?: url)
            .client(baseClientSupplier())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(DataSourceAPI::class.java)
    }

    override suspend fun performRequest(
        urlRequest: URLRequest,
        authorizersOverride: List<Authorizer>?
    ): DataSourceService.Result {
        val sanitizedURL = baseURLSupplier()?.let { baseURL ->
            urlRequest.url.replaceFirst(Regex("""^(https://|http://)""", RegexOption.IGNORE_CASE), baseURL)
        } ?: urlRequest.url

        val modifiedRequest = urlRequest.copy(url = sanitizedURL)

        val authorizers = authorizersOverride ?: authorizersSupplier()

        authorizers.forEach { authorizer ->
            // the authorizers mutate the urlrequest.
            authorizer.authorize(modifiedRequest)
        }

        val response: Response<ResponseBody> = when(modifiedRequest.method) {
            HttpMethod.GET -> {
                api.get(sanitizedURL, modifiedRequest.headers)
            }
            HttpMethod.PUT -> {
                val body = modifiedRequest.body
                if (body == null) {
                    api.put(sanitizedURL, modifiedRequest.headers)
                } else {
                    val contentType = "text/plain".toMediaType()
                    val requestBody = body.toRequestBody(contentType)

                    api.putWithBody(sanitizedURL, modifiedRequest.headers, requestBody)
                }
            }
            HttpMethod.POST -> {
                val body = modifiedRequest.body
                if (body == null) {
                    api.post(sanitizedURL, modifiedRequest.headers)
                } else {
                    val contentType = "text/plain".toMediaType()
                    val requestBody = body.toRequestBody(contentType)

                    api.postWithBody(sanitizedURL, modifiedRequest.headers, requestBody)
                }
            }
        }

        val json = response.body()?.string()
        if (response.isSuccessful && json != null) {
            return DataSourceService.Result.Success(
                body = json
            )
        }

        return DataSourceService.Result.Failure(
            Throwable(response.message())
        )
    }
}
