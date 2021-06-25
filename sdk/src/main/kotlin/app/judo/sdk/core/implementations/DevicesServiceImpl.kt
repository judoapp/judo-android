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

import app.judo.sdk.core.data.RegistrationRequestBody
import app.judo.sdk.core.data.RegistrationResponse
import app.judo.sdk.core.services.DevicesService
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT

class DevicesServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val baseURLSupplier: () -> String?
) : DevicesService {

    companion object {
        const val url: String = "https://devices.judo.app"
    }

    interface DevicesAPI {
        @PUT("/register")
        suspend fun register(@Body body: RegistrationRequestBody): Response<RegistrationResponse>
    }

    private val api by lazy {

        Retrofit.Builder()
            .baseUrl(baseURLSupplier() ?: url)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(baseClientSupplier())
            .build().create(DevicesAPI::class.java)
    }

    override suspend fun register(body: RegistrationRequestBody): Response<RegistrationResponse> {
        return api.register(body)
    }
}
