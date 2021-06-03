package app.judo.sdk.core.implementations

import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.services.DataSourceService
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

@Suppress("BlockingMethodInNonBlockingContext")
internal class DataSourceServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val baseURLSupplier: () -> String?,
    private val loggerSupplier: () -> Logger? = { null }
) : DataSourceService {

    companion object {
        /**
         * This is just a place holderURL and will be replaced during actual calls
         */
        private const val url: String = "https://devices.judo.app"
    }

    interface DataSourceAPI {
        @GET
        suspend fun get(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
        ): Response<ResponseBody>

        @PUT
        suspend fun put(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
        ): Response<ResponseBody>

        @PUT
        suspend fun putWithBody(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
            @Body body: RequestBody,
        ): Response<ResponseBody>

        @POST
        suspend fun post(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
        ): Response<ResponseBody>

        @POST
        suspend fun postWithBody(
            @Url url: String,
            @HeaderMap headers: Map<String, String>,
            @Body body: RequestBody,
        ): Response<ResponseBody>

    }

    private val api by lazy {

        Retrofit.Builder()
            .baseUrl(baseURLSupplier() ?: url)
            .client(baseClientSupplier())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(DataSourceAPI::class.java)
    }

    override suspend fun getData(
        url: String,
        headers: Map<String, String>
    ): DataSourceService.Result {

        val sanitizedURL = baseURLSupplier()?.let { baseURL ->
            url.replaceFirst(Regex("""^(https://|http://)""", RegexOption.IGNORE_CASE), baseURL)
        } ?: url

        val response = api.get(sanitizedURL, headers)
        val json = response.body()?.string()
        if (response.isSuccessful && json != null) {
            return DataSourceService.Result.Success(
                body = json
            )
        }

        return DataSourceService.Result.Failure(
            Throwable(response.message())
        )
    }

    override suspend fun putData(
        url: String,
        headers: Map<String, String>,
        body: String?
    ): DataSourceService.Result {

        val sanitizedURL = baseURLSupplier()?.let { baseURL ->
            url.replaceFirst(Regex("""^(https://|http://)""", RegexOption.IGNORE_CASE), baseURL)
        } ?: url

        val response = if (body == null) {
            api.put(sanitizedURL, headers)
        } else {

            val contentType = MediaType.parse("text/plain")
            val requestBody = RequestBody.create(contentType, body)

            api.putWithBody(sanitizedURL, headers, requestBody)
        }


        val json = response.body()?.string()
        if (response.isSuccessful && json != null) {
            return DataSourceService.Result.Success(
                body = json
            )
        }

        return DataSourceService.Result.Failure(
            Throwable(response.message())
        )

    }

    override suspend fun postData(
        url: String,
        headers: Map<String, String>,
        body: String?
    ): DataSourceService.Result {

        val sanitizedURL = baseURLSupplier()?.let { baseURL ->
            url.replaceFirst(Regex("""^(https://|http://)""", RegexOption.IGNORE_CASE), baseURL)
        } ?: url

        val response = if (body == null) {
            api.post(sanitizedURL, headers)
        } else {


            val contentType = MediaType.parse("text/plain")
            val requestBody = RequestBody.create(contentType, body)

            api.postWithBody(sanitizedURL, headers, requestBody)
        }

        val json = response.body()?.string()
        if (response.isSuccessful && json != null) {
            return DataSourceService.Result.Success(
                body = json
            )
        }

        return DataSourceService.Result.Failure(
            Throwable(response.message())
        )
    }
}
