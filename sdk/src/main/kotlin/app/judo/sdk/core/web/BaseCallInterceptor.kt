package app.judo.sdk.core.web

import app.judo.sdk.core.log.Logger
import okhttp3.Interceptor
import okhttp3.Response

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

        logger.d(TAG, "Request intercepted:\n\t$original")

        val newRequest = original.newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("Judo-Access-Token", accessTokenSupplier())
            addHeader("Judo-Device-ID", deviceIdSupplier())
        }.build()

        logger.d(TAG, "Request changed to:\n\t$newRequest")

        val response = chain.proceed(newRequest)

        logger.d(
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
