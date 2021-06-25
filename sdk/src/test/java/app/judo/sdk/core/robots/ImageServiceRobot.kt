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

package app.judo.sdk.core.robots

import app.judo.sdk.core.services.ImageService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll

@ExperimentalCoroutinesApi
internal class ImageServiceRobot : AbstractTestRobot() {

    suspend fun getImages(vararg imageUrls: String): List<ImageService.Result> {

        val requests = imageUrls.map {
            it.replaceFirst("https://", baseURL)
        }.map { url -> ImageService.Request(url) }.toTypedArray()

        return requests.map {
            environment.imageService.getImageAsync(it)
        }.awaitAll()
    }

    suspend fun getImage(request: ImageService.Request): ImageService.Result {
        return environment.imageService.getImageAsync(request).await()
    }

    suspend fun getImageAsync(request: ImageService.Request): Deferred<ImageService.Result> {
        return environment.imageService.getImageAsync(request)
    }

    fun addHeader(header: Pair<String, String>) {
        serverDispatcher.responseHeaders += header
    }

}
