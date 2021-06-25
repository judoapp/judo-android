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

import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.implementations.CookieJarImpl
import app.judo.sdk.core.implementations.ExperienceServiceImpl
import app.judo.sdk.core.web.Http
import app.judo.sdk.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ExperienceServiceTests {

    private val accessToken = "token1"
    private val deviceId = UUID.randomUUID().toString()
    private val logger = TestLoggerImpl()

    @Suppress("SpellCheckingInspection")
    private lateinit var experienceURL: String

    private lateinit var dispatcher: TestCoroutineDispatcher

    private lateinit var service: ExperienceService

    private lateinit var dirPath: String

    private lateinit var cacheDirectory: File

    private lateinit var cacheName: String

    private lateinit var server: MockWebServer

    private lateinit var baseClient: OkHttpClient

    private lateinit var serverDispatcher: TestServerDispatcher

    private lateinit var keyValueCache: KeyValueCache

    private lateinit var cookieJar: CookieJar

    @Before
    fun setUp() {

        dispatcher = TestCoroutineDispatcher()

        Dispatchers.setMain(dispatcher)

        serverDispatcher = TestServerDispatcher()

        server = MockWebServer().apply {
            setDispatcher(serverDispatcher)
            start()
        }

        dirPath = "${System.getProperty("user.dir")}/temp"

        cacheDirectory = File(dirPath)

        cacheName = ExperienceServiceImpl.cacheName

        keyValueCache = FakeKeyValueCache(
            logger
        )

        cookieJar = CookieJarImpl(
            keyValueCacheSupplier = { keyValueCache },
            loggerSupplier = { logger }
        )

        baseClient = Http.coreClient(
            accessTokenSupplier = { accessToken },
            deviceIdSupplier = { deviceId },
            loggerSupplier = { logger },
            cookieJarSupplier = { cookieJar }
        )

        val baseURL = server.url("/")

        experienceURL = "https://test1.judo.app/testexperience"

        service = ExperienceServiceImpl(
            cachePathSupplier = { dirPath },
            baseURLSupplier = { baseURL.toString() },
            clientSupplier = { baseClient },
            cacheSizeSupplier = { Environment.Sizes.EXPERIENCE_CACHE_SIZE }
        )
    }

    @After
    fun tearDown() {

        server.shutdown()

        if (cacheDirectory.exists()) {
            cacheDirectory.deleteRecursively()
        }

        Dispatchers.resetMain()
    }

    @Test
    fun `requests contains Accept json header`() = runBlocking {
        // Arrange
        val expected = "application/json"

        // Act
        val response = service.getExperience(experienceURL)

        val actual = response.headers()["Accept"]

        // Assert
        expected shouldEqual actual
    }

    @Test
    @Ignore("Brotli support will be added back in the future")
    fun `requests contain Accept-Encoding for brotli headers`() = runBlocking {
        // Arrange
        val expected = "br"

        service.getExperience(experienceURL)

        // Act
        val actual = serverDispatcher.actualRequest?.getHeader("Accept-Encoding")

        // Assert
        Assert.assertTrue(actual?.contains(expected) == true)
    }

    @Test
    fun `requests contain Accept-Encoding for gzip headers`() = runBlocking {
        // Arrange
        val expected = "gzip"

        service.getExperience(experienceURL)

        // Act
        val actual = serverDispatcher.actualRequest?.getHeader("Accept-Encoding")

        // Assert
        Assert.assertTrue(actual?.contains(expected) == true)
    }

    @Test
    fun `requests contain Judo-Access-Token header`() = runBlocking {
        // Arrange
        val expected = accessToken

        // Act
        val response = service.getExperience(experienceURL)

        val actual = response.headers()["Judo-Access-Token"]

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `requests contain Judo-Device-ID header`() = runBlocking {
        // Arrange
        val expected = deviceId

        // Act
        val response = service.getExperience(experienceURL)

        val actual = response.headers()["Judo-Device-ID"]

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `response is de-serialized correctly`() = runBlocking {
        // Arrange
        val experience = JsonParser.parseExperience(TestJSON.experience)
        val expected = experience?.id to experience?.nodes?.size

        // Act
        val actualExperience = service.getExperience(experienceURL).body()
        val actual = actualExperience?.id to actualExperience?.nodes?.size

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `cache is built with correct name`() = runBlocking {
        // Arrange
        val expected = true

        service.getExperience(experienceURL).body()

        // Act
        val actual = File("$dirPath/$cacheName").exists()

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `duplicate calls are returned from the cache`() = runBlocking {
        // Arrange
        val expected = true
        serverDispatcher.responseHeaders += "Cache-Control" to "public, max-age=31557600, immutable"

        service.getExperience(experienceURL)
        val response = service.getExperience(experienceURL)

        // Act
        val actual = response.raw().cacheResponse() != null

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `response cookies are cached`() = runBlocking {
        // Arrange
        val expected = "cookie1=things;cookie2=stuff;persistent=true"

        serverDispatcher.responseHeaders += "Set-Cookie" to expected

        // Act
        val actual = service.getExperience(experienceURL).headers()["Set-Cookie"]
        val actual2 = service.getExperience(experienceURL).headers()["Set-Cookie"]

        // Assert
        expected shouldEqual actual
        expected shouldEqual actual2
    }

}

