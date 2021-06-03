package app.judo.sdk.core.services

internal interface DataSourceService {

    sealed class Result {

        data class Success(
            val body: String
        ) : Result()

        data class Failure(
            val error: Throwable
        ) : Result()

    }

    suspend fun getData(
        url: String,
        headers: Map<String, String> = emptyMap(),
    ): Result

    suspend fun putData(
        url: String,
        headers: Map<String, String> = emptyMap(),
        body: String? = null
    ): Result

    suspend fun postData(
        url: String,
        headers: Map<String, String> = emptyMap(),
        body: String? = null
    ): Result

}