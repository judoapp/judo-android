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

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import app.judo.sdk.core.services.ImageService
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

internal class ImageServiceImpl(
    private val context: Context,
    private val clientSupplier: () -> OkHttpClient,
    private val baseURLSupplier: () -> String?,
    private val imageCachePathSupplier: () -> String,
    private val cacheSizeSupplier: () -> Long,
) : ImageService {

    companion object {
        const val cacheName: String = "judo_image_cache"
    }

    val cache: Cache by lazy {
        Cache(
            File(File(imageCachePathSupplier()), cacheName),
            cacheSizeSupplier()
        )
    }

    private val imageClient: OkHttpClient by lazy {
        clientSupplier().newBuilder().apply {
            cache(cache)
        }.build()
    }

    private val loader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .okHttpClient(imageClient)
            .availableMemoryPercentage(1.0)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28)
                    add(ImageDecoderDecoder())
                else
                    add(GifDecoder())
            }
            .crossfade(true)
            .build()
    }

    override fun isImageCached(imageUrl: String): Boolean {
        val urlIterator = imageClient.cache()?.urls()
        urlIterator?.let {
            while (it.hasNext()) {
                if (urlIterator.next() == imageUrl) {
                    return true
                }
            }
        }
        return false
    }

    private val current = mutableMapOf<String, CompletableDeferred<ImageService.Result>>()

    @Synchronized
    override suspend fun getImageAsync(request: ImageService.Request): Deferred<ImageService.Result> {
        val sanitizedURL = baseURLSupplier()?.let { baseURL ->
            request.url.replaceFirst(
                Regex("""^(https://|http://)""", RegexOption.IGNORE_CASE),
                baseURL
            )
        } ?: request.url

        return synchronized(current) {
            current.getOrPut(sanitizedURL) {

                val deferred = CompletableDeferred<ImageService.Result>()
                var target: Drawable? = null

                val loadRequest = ImageRequest.Builder(context = context)
                    .data(sanitizedURL)
                    .listener(
                        onError = { _, error ->
                            deferred.complete(
                                ImageService.Result.Error(
                                    request = request,
                                    drawable = null,
                                    error = error
                                )
                            )
                        },
                        onSuccess = { _, metadata ->
                            deferred.complete(
                                ImageService.Result.Success(
                                    request = request,
                                    drawable = target!!,
                                    isFromCache = metadata.dataSource == DataSource.MEMORY_CACHE
                                )
                            )
                        })
                    .target { drawable ->
                        target = drawable
                    }
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()

                try {
                    loader.enqueue(loadRequest)
                } catch (error: Throwable) {
                    deferred.complete(
                        ImageService.Result.Error(
                            request = request,
                            drawable = null,
                            error = error
                        )
                    )
                }
                deferred
            }
        }
    }

}