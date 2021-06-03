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

        logger.d(
            TAG, """
                REQUEST SENT:
                    $request
                    BODY: ${request.body()}
                    URL: ${request.url()}
                """.trimIndent()
        )

        val response = chain.proceed(request)

        logger.d(
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
