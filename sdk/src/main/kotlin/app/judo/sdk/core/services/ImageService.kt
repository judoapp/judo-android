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