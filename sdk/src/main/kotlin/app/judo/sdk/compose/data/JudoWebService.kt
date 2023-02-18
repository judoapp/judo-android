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

package app.judo.sdk.compose.data

import app.judo.sdk.compose.model.values.CDNConfiguration
import app.judo.sdk.compose.model.values.ExperienceModel
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

internal interface JudoWebService {
    @GET
    suspend fun getExperience(@Url url: String): Response<ExperienceModel>

    @GET
    suspend fun getConfiguration(@Url url: String): CDNConfiguration

    companion object {
        fun make(httpClient: JudoHttpClient): JudoWebService {
            val retrofit = Retrofit
                .Builder()
                // This base URL is usually not used, but is required by Retrofit.
                .baseUrl("https://content.judo.app/")
                .addConverterFactory(MoshiConverterFactory.create(app.judo.sdk.compose.data.JsonParser.moshi))
                .client(
                    httpClient.client
                ).build()
            return retrofit.create(JudoWebService::class.java)
        }
    }
}
