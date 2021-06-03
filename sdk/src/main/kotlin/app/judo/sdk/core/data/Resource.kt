package app.judo.sdk.core.data

internal sealed class Resource<out T, out E : Throwable> {

    data class Loading<out T>(
        val cacheData: T? = null
    ) : Resource<T, Nothing>()

    data class Success<out T>(
        val data: T
    ) : Resource<T, Nothing>()

    data class Error<out E : Throwable>(
        val error: E,
    ) : Resource<Nothing, E>()

}
