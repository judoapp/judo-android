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

package app.judo.sdk.compose.ui

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import app.judo.sdk.compose.data.JudoHttpClient
import app.judo.sdk.compose.data.JudoWebService
import app.judo.sdk.compose.ui.fonts.FontLoader
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

/**
 * Various singletons and services.
 */
internal data class Services(
    val context: Context
) {
    val httpClient: JudoHttpClient by lazy {
        JudoHttpClient(context)
    }

    val webService: JudoWebService by lazy {
        JudoWebService.make(httpClient)
    }

    val fontLoader: FontLoader by lazy {
        FontLoader(context, httpClient)
    }

    /**
     * Wherever needed, we use this loader instead of setting it through Coil.setImageLoader, as that
     * may override customer set ImageLoader settings.
     */
    val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .okHttpClient {
                httpClient.client
            }
            .build()
    }

    companion object {
        /**
         * The purpose of this composable is to construct the graph of services
         * and their dependencies. [Services] is then available via [Environment.LocalServices].
         *
         * If it turns out to have already been initialized in the current environment, then
         * nothing is done. This behaviour exists to account for the multiple entry
         * points for Judo composables in the public API.
         *
         * For convenience, it also makes Services available as a parameter to the nested composable.
         */
        @Composable
        fun Inject(
            content: @Composable (Services) -> Unit
        ) {
            val currentServices = Environment.LocalServices.current
            if (currentServices == null) {
                val services = Services(LocalContext.current)
                CompositionLocalProvider(
                    Environment.LocalServices provides services
                ) {
                    content(services)
                }
            } else {
                content(currentServices)
            }
        }
    }
}
