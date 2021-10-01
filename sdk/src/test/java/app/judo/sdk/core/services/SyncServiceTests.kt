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

import app.judo.sdk.BuildConfig
import app.judo.sdk.core.data.SyncData
import app.judo.sdk.core.data.SyncResponse
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.implementations.SyncServiceImpl
import app.judo.sdk.core.web.Http
import app.judo.sdk.core.web.JudoCallInterceptor
import app.judo.sdk.utils.TestLoggerImpl
import app.judo.sdk.utils.TestServerDispatcher
import app.judo.sdk.utils.shouldEqual
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import java.util.*

@ExperimentalCoroutinesApi
class SyncServiceTests {

    private val deviceId = UUID.randomUUID().toString()
    private val accessToken = "token1"
    private val domainName = "test1.judo.app"
    private val logger = TestLoggerImpl()

    private lateinit var server: MockWebServer

    private lateinit var serverDispatcher: TestServerDispatcher

    private lateinit var dispatcher: TestCoroutineDispatcher

    private lateinit var service: SyncService

    private lateinit var baseURL: String

    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        dispatcher = TestCoroutineDispatcher()

        Dispatchers.setMain(dispatcher)

        serverDispatcher = TestServerDispatcher()

        server = MockWebServer().apply {
            setDispatcher(serverDispatcher)
            start()
        }

        baseURL = server.url("/").toString()

        client = Http.coreClient(
            loggerSupplier = { logger }
        ).newBuilder().apply {
            addInterceptor(
                JudoCallInterceptor(
                    accessTokenSupplier = { accessToken },
                    deviceIdSupplier = { deviceId },
                    loggerSupplier = { logger },
                    httpAgent = System.getProperty("http.agent") ?: "",
                    clientPackageName = { "com.client.test" },
                    appVersion = { "1.0.0" },
                )
            )
        }.build()

        service = SyncServiceImpl(
            baseClientSupplier = { client },
            baseURLSupplier = { baseURL }
        )

    }

    @After
    fun tearDown() {
        server.shutdown()
        Dispatchers.resetMain()
    }

    @Test
    fun `requests contains Accept json header`() {
        runBlocking {
            // Arrange
            val expected = "application/json"

            // Act
            service.getSync(domainName)

            val actual = serverDispatcher.actualRequest?.getHeader("Accept")

            // Assert
            expected shouldEqual actual
        }
    }

    @Test
    @Ignore("Brotli support will be added back in the future")
    fun `requests contain Accept-Encoding for brotli headers`() = runBlocking {
        // Arrange
        val expected = "br"

        // Act
        service.getSync(domainName)

        val actual = serverDispatcher.actualRequest?.getHeader("Accept-Encoding")

        // Assert
        Assert.assertTrue(actual?.contains(expected) == true)
    }

    @Test
    fun `requests contain Accept-Encoding for gzip headers`() = runBlocking {
        // Arrange
        val expected = "gzip"

        // Act
        service.getSync(domainName)

        val actual = serverDispatcher.actualRequest?.getHeader("Accept-Encoding")

        // Assert
        Assert.assertTrue(actual?.contains(expected) == true)
    }

    @Test
    fun `requests contain apiVersion query`() = runBlocking {
        // Arrange
        val expected = "apiVersion=${BuildConfig.API_VERSION}"

        // Act
        service.getSync(domainName)

        val actual = serverDispatcher.actualRequest?.requestUrl?.query()?.endsWith(expected) ?: true

        // Assert
        Assert.assertTrue(actual)
    }

    @Test
    fun `requests contain Judo-Access-Token header`() = runBlocking {
        // Arrange
        val expected = accessToken

        // Act
        service.getSync(domainName)

        val actual = serverDispatcher.actualRequest?.getHeader("Judo-Access-Token")

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `requests contain Judo-Device-ID header`() = runBlocking {
        // Arrange
        val expected = deviceId

        // Act
        service.getSync(aURL = domainName)

        val actual = serverDispatcher.actualRequest?.getHeader("Judo-Device-ID")

        // Assert
        expected shouldEqual actual
    }


    @Test
    fun `response is de-serialized correctly`() = runBlocking {
        // Arrange

        val expected = SyncResponse(
            data = listOf(
                SyncData(
                url = """https://test1.judo.app/testexperience""",
                removed = false,
                priority = 10
            )
            ),
            nextLink = """https://test1.judo.app/sync?cursor=MjAyMC0xMS0yMFQxNjo0NDozNi44ODBa"""
        )

        // Act
        val actual = service.getSync(domainName).body()

        // Assert
        expected shouldEqual actual
    }

}

