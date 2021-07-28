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

package app.judo.sdk.core.web

import app.judo.sdk.core.log.Logger
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

internal class BaseCallInterceptor(
    private val accessTokenSupplier: () -> String,
    private val deviceIdSupplier: () -> String,
    private val loggerSupplier: () -> Logger
) : Interceptor {

    companion object {
        private const val TAG = "BaseCallInterceptor"
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val logger = loggerSupplier()
        val original = chain.request()

        logger.v(TAG, "Request intercepted:\n\t$original")

        val newRequest = original.newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("Judo-Access-Token", accessTokenSupplier())
            addHeader("Judo-Device-ID", deviceIdSupplier())
        }.build()

        logger.v(TAG, "Request changed to:\n\t$newRequest")

        val response = try { chain.proceed(newRequest)} catch (e: Throwable) {
            logger.e(TAG, null, e)
            Response.Builder().apply {
                code(400)
                request(original)
                protocol(Protocol.HTTP_2)
                body(ResponseBody.create(null, ""))
                this.message(e.message ?: "Network Error")
            }.build()
        }

        logger.v(
            TAG,
            """
                |Response received:
                |   $response
                |   Code: ${response.code()}
                |   Headers:${response.headers()}
                |   Network response: ${response.networkResponse()?.body()}
                |   Cache response: ${response.cacheResponse()?.body()}
""".trimMargin()
        )

        return response
    }
}
