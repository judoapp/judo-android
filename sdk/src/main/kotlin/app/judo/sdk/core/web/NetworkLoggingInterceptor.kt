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
import okhttp3.Response

internal class NetworkLoggingInterceptor(
    private val loggerSupplier: () -> Logger
) : Interceptor {

    companion object {
        private const val TAG = "NetworkLoggingInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val logger = loggerSupplier()

        val request = chain.request()

        logger.v(
            TAG, """
                REQUEST SENT:
                    $request
                    BODY: ${request.body()}
                    URL: ${request.url()}
                """.trimIndent()
        )

        val response = chain.proceed(request)

        logger.v(
            TAG, """
            |RESPONSE RECEIVED:
            |   $response
            |   CODE: ${response.code()}
            |   HEADERS:${response.headers()}
            |   NETWORK RESPONSE: ${response.networkResponse()}
            |   CACHE RESPONSE: ${response.cacheResponse()}
""".trimMargin()
        )

        return response
    }

}
