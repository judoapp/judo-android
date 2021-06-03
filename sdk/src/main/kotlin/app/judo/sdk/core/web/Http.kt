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
