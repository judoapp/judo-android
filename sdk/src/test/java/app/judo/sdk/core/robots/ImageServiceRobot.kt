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
