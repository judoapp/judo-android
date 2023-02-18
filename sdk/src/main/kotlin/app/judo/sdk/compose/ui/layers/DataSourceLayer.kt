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

package app.judo.sdk.compose.ui.layers

import android.util.Log
import androidx.compose.runtime.*
import app.judo.sdk.compose.model.nodes.DataSource
import app.judo.sdk.compose.model.values.HttpMethod
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.URLRequest
import app.judo.sdk.compose.ui.data.Interpolator
import app.judo.sdk.compose.ui.data.makeDataContext
import app.judo.sdk.compose.ui.data.resolveJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun DataSourceLayer(node: DataSource) {
    val tag = "DataSourceLayer"
    val dataContext = makeDataContext(
        userInfo = Environment.LocalUserInfo.current?.invoke() ?: emptyMap(),
        urlParameters = Environment.LocalUrlParameters.current,
        data = Environment.LocalData.current
    )
    val interpolator = Interpolator(
        dataContext
    )

    val services = Environment.LocalServices.current ?: run {
        Log.e(tag, "Services not injected")
        return
    }

    val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://placeholder.judo.app")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(services.httpClient.client)
            .build()
            .create(DataSourceAPI::class.java)
    }

    val scope = rememberCoroutineScope()

    var state: State by remember { mutableStateOf(State.Loading) }

    // Interpolation on URL, headers, and body. If interpolation fails, yield empty.
    val url = interpolator.interpolate(node.url) ?: return
    val requestBody = node.httpBody?.let { interpolator.interpolate(it) ?: return@DataSourceLayer }
    val headers = node.headers.associateBy({
        it.key
    }, {
        interpolator.interpolate(it.value) ?: return@DataSourceLayer
    })

    val urlRequest = URLRequest(
        url = url,
        method = node.httpMethod,
        headers = HashMap(
            headers
        ),
        body = requestBody
    )

    Environment.LocalAuthorizerHandler.current?.invoke(urlRequest)

    LaunchedEffect(key1 = urlRequest) {
        state = State.Loading

        do {
            scope.launch {
                val response = try {
                    when (urlRequest.method) {
                        HttpMethod.GET -> {
                            api.get(urlRequest.url, urlRequest.headers)
                        }
                        HttpMethod.PUT -> {
                            val body = urlRequest.body
                            if (body == null) {
                                api.put(urlRequest.url, urlRequest.headers)
                            } else {
                                val contentType = "text/plain".toMediaType()
                                val requestBody = RequestBody.create(contentType, body)
                                api.putWithBody(urlRequest.url, urlRequest.headers, requestBody)
                            }
                        }
                        HttpMethod.POST -> {
                            val body = urlRequest.body
                            if (body == null) {
                                api.post(urlRequest.url, urlRequest.headers)
                            } else {
                                val contentType = "text/plain".toMediaType()
                                val requestBody = RequestBody.create(contentType, body)
                                api.postWithBody(urlRequest.url, urlRequest.headers, requestBody)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w("Judo", "Data source request failed, network error: $e")
                    state = State.Failed
                    return@launch
                }

                if (response.isSuccessful) {
                    val data = withContext(Dispatchers.IO) {
                        val byteResult = kotlin.runCatching {
                            response.body()?.string()
                        }

                        if (byteResult.isSuccess) {
                            return@withContext byteResult.getOrNull()?.let { resolveJson(it.toString()) }
                        } else {
                            Log.w("Judo", "Data source read error: ${byteResult.exceptionOrNull()}")
                        }
                    }
                    Log.d("Judo", "Data source loaded successfully.")
                    state = State.Success(data)
                } else {
                    Log.w("Judo", "Data source request failed: HTTP ${response.code()}: ${response.message()}")
                }
            }
            delay((node.pollInterval.seconds))
        } while (node.pollInterval > 0)
    }

    when (val currentState = state) {
        State.Loading -> {
            //TODO: replace this with a placeholder once performance issues are addressed.
            // CircularProgressIndicator(modifier = StripPackedJudoIntrinsics())
        }
        is State.Failed -> {
            // Disappear; composable is just empty.
        }
        is State.Success -> {
            CompositionLocalProvider(Environment.LocalData provides currentState.data) {
                Children(children = node.children)
            }
        }
    }
}

private sealed class State {
    object Loading : State()
    data class Success(val data: Any?) : State()
    object Failed : State()
}

private interface DataSourceAPI {
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
