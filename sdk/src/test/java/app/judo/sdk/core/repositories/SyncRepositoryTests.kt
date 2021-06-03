package app.judo.sdk.core.repositories

import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.implementations.SyncRepositoryImpl
import app.judo.sdk.utils.FakeKeyValueCache
import app.judo.sdk.utils.FakeSyncService
import app.judo.sdk.utils.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
internal class SyncRepositoryTests {

    private lateinit var service: FakeSyncService

    private lateinit var repository: SyncRepositoryImpl

    private lateinit var keyValueCache: KeyValueCache

    @Before
    fun setUp() {

        service = FakeSyncService()

        keyValueCache = FakeKeyValueCache()

        repository = SyncRepositoryImpl(
            syncServiceSupplier = { service },
            keyValueCacheSupplier = { keyValueCache }
        )

    }

    @Test
    fun `repository emits Loading first`() = runBlockingTest {
        // Arrange
        val expected = Resource.Loading<SyncResponse>()

        // Act
        val actual = repository.retrieveSync("test1.judo.app").first()

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `repository catches exceptions and emits them as an Error`() = runBlockingTest {
        // Arrange
        val expected = Throwable("exception")

        service.onNext = {
            throw expected
        }

        // Act
        var actual: Throwable? = null

        repository.retrieveSync("test1.judo.app").collect {
            actual = (it as? Resource.Error)?.error
        }

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `repository emits Success if the response is a 200 and body is not null`() = runBlockingTest {
        // Arrange
        val expected = Resource.Success(service.syncResponse)

        // Act
        var actual: Resource<SyncResponse, Throwable>? = null

        repository.retrieveSync("test1.judo.app").collect {
            actual = it
        }

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `repository emits Error if the response is not 200`() = runBlockingTest {
        // Arrange
        service.responseCode = 500

        val expected = Throwable(
            Response.error<SyncResponse>(
                500, "".toResponseBody("application/json".toMediaType())
            ).message()
        ).message

        // Act
        var actual: String? = null

        repository.retrieveSync("test1.judo.app").collect {
            actual = (it as? Resource.Error)?.error?.message
        }

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `repository emits Error if the response body is null`() = runBlockingTest {
        // Arrange
        service.responseCode = 201

        val expected = "OK"

        // Act
        var actual: String? = null

        repository.retrieveSync("test1.judo.app").collect {
            actual = (it as? Resource.Error)?.error?.message
        }

        // Assert
        expected shouldEqual actual
    }

}