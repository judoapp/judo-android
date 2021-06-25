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

package app.judo.sdk.core.services

import android.graphics.drawable.Drawable
import kotlinx.coroutines.Deferred

internal interface ImageService {

    data class Request(
        val url: String
    )

    /**
     * Represents the result of an image request.
     *
     * @see ImageService.getImageAsync
     */
    sealed class Result {
        abstract val drawable: Drawable?
        abstract val request: Request

        /**
         * Indicates that the request completed successfully.
         *
         * @param drawable The success drawable.
         * @param request The request that was executed to create this result.
         */
        data class Success(
            override val drawable: Drawable,
            override val request: Request,
            val isFromCache: Boolean = false,
        ) : Result()

        /**
         * Indicates that an error occurred while executing the request.
         *
         * @param drawable The error drawable.
         * @param request The request that was executed to create this result.
         * @param error The error that failed the request.
         */
        data class Error(
            override val drawable: Drawable?,
            override val request: Request,
            val error: Throwable,
        ) : Result()
    }

    suspend fun getImageAsync(request: Request): Deferred<Result>

    fun isImageCached(imageUrl: String): Boolean

}