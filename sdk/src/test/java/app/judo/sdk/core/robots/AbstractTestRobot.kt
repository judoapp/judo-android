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

package app.judo.sdk.core.robots

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.MutableEnvironment
import app.judo.sdk.core.implementations.EnvironmentImpl
import app.judo.sdk.core.implementations.KeyValueCacheImpl
import app.judo.sdk.core.web.*
import app.judo.sdk.utils.FakeAnalyticsService
import app.judo.sdk.utils.FakeFontResourceService
import app.judo.sdk.utils.TestLoggerImpl
import app.judo.sdk.utils.TestServerDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.CookieJar
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("MemberVisibilityCanBePrivate")
internal abstract class AbstractTestRobot {

    val testCoroutineDispatcher = TestCoroutineDispatcher().apply {
        Dispatchers.setMain(this)
    }

    protected val testScope: CoroutineScope = CoroutineScope(testCoroutineDispatcher)

    protected val serverDispatcher = TestServerDispatcher(
        onRequest = ::onRequest,
        onResponse = ::onResponse,
    )

    protected val server = MockWebServer().apply {
        setDispatcher(serverDispatcher)
        start()
    }

    protected val baseURL = server.url("/").toString()

    protected val application: Application = ApplicationProvider.getApplicationContext()

    protected var cookieJar: CookieJar? = null

    protected val backingEnvironment: MutableEnvironment = EnvironmentImpl(
        application
    ).apply {

        mainDispatcher = testCoroutineDispatcher

        defaultDispatcher = testCoroutineDispatcher

        ioDispatcher = testCoroutineDispatcher

        logger = TestLoggerImpl()

        keyValueCache = KeyValueCacheImpl(
            application
        )

        baseURL = this@AbstractTestRobot.baseURL

        baseClient = Http.coreClient(
            accessTokenSupplier = { configuration.accessToken },
            deviceIdSupplier = { keyValueCache.retrieveString(Environment.Keys.DEVICE_ID) ?: "TODO" },
            loggerSupplier = { logger },
            cookieJarSupplier = { cookieJar }
        )

        fontResourceService = FakeFontResourceService()

        eventQueue = FakeAnalyticsService()
    }

    val environment: Environment = backingEnvironment

    val cachePath = "${System.getProperty("user.dir")}/temp"

    open fun onSetUp() {}

    open fun onTearDown() {
        server.shutdown()

        val cacheDirectory = File(cachePath)

        if (cacheDirectory.exists()) {
            cacheDirectory.deleteRecursively()
        }

        (environment.ioDispatcher as? TestCoroutineDispatcher)?.cleanupTestCoroutines()

        testScope.cancel()

        Dispatchers.resetMain()
    }

    open fun onRequest(request: RecordedRequest) {
        /* no-op */
    }

    open fun onResponse(responseAndBody: Pair<MockResponse, String>) {
        /* no-op */
    }

}