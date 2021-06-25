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
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient

internal object Http {

    fun coreClient(
        accessTokenSupplier: () -> String,
        deviceIdSupplier: () -> String,
        loggerSupplier: () -> Logger,
        cookieJarSupplier: () -> CookieJar? = { null },
        vararg interceptors: Interceptor,
    ): OkHttpClient {

        val baseInterceptor: Interceptor =
            BaseCallInterceptor(
                accessTokenSupplier = accessTokenSupplier,
                deviceIdSupplier = deviceIdSupplier,
                loggerSupplier
            )

        return OkHttpClient.Builder().apply {
            addInterceptor(baseInterceptor)
            interceptors.forEach { addInterceptor(it) }
            addNetworkInterceptor(
                NetworkLoggingInterceptor(
                    loggerSupplier
                )
            )
            cookieJarSupplier()?.let(::cookieJar)
        }.build()

    }

}
