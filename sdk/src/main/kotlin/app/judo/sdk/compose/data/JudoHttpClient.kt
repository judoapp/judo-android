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

import android.content.Context
import app.judo.sdk.BuildConfig
import okhttp3.OkHttpClient

internal class JudoHttpClient(
    private val context: Context
) {
    private val userAgent: String by lazy {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            0
        )
        val httpAgent = System.getProperty("http.agent") ?: ""
        val clientPackageName = packageInfo.packageName
        val appVersion = packageInfo.versionName

        "$httpAgent $clientPackageName/$appVersion JudoCompose/${BuildConfig.LIBRARY_VERSION}"
    }

    val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder().apply {
            addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder().apply {
                        header("User-Agent", userAgent)
                    }.build()
                )
            }
        }.build()
    }
}
