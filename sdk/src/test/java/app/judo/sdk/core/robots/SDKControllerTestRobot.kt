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

import app.judo.sdk.api.Judo
import app.judo.sdk.core.controllers.SDKControllerImpl
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.utils.TestJSON
import app.judo.sdk.utils.TestLoggerImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import java.util.*

@ExperimentalCoroutinesApi
internal class SDKControllerTestRobot : AbstractTestRobot() {

    private lateinit var controller: SDKControllerImpl

    private val responses = mutableListOf<Pair<MockResponse, String>>()

    fun initializeControllerWith(accessToken: String, domain: String) {
        backingEnvironment.configuration = backingEnvironment.configuration.copy(domain = domain)

        controller = SDKControllerImpl().apply {
            logger = TestLoggerImpl()
            this.environment = this@SDKControllerTestRobot.environment
            initialize(
                application = application,
                configuration = Judo.Configuration(
                    accessToken = accessToken,
                    domain = domain
                )
            )
        }

    }

    override fun onResponse(responseAndBody: Pair<MockResponse, String>) {
        super.onResponse(responseAndBody)
        responses += responseAndBody
    }

    suspend fun performSync(prefetchAssets: Boolean = false, onComplete: () -> Unit = {}) {
        controller.performSync(prefetchAssets, onComplete)
    }

    fun assertThatASyncResponseAndTwoExperiencesWereRetrieved() {

        val syncResponse = responses.find { it.second.contains("/test") }?.second
        val experienceResponse = responses.find { it.second.contains(""""id": "3"""") }?.second
        val experienceResponses = responses.filter { it.second.contains(""""id": "3"""") }

        Assert.assertTrue(syncResponse != null && experienceResponse != null && experienceResponses.size == 1)

    }

    fun assertThatASyncResponseAndAExperienceWereRetrieved() {

        val syncResponse = responses.find { it.second.contains("/test") }?.second
        val experienceResponse = responses.find { it.second.contains(""""id": "3"""") }?.second

        Assert.assertTrue(syncResponse != null && experienceResponse != null)

    }

    fun assertThatTheCacheContainsADeviceID() {
        Assert.assertNotNull(
            UUID.fromString(
                environment.keyValueCache.retrieveString(Environment.Keys.DEVICE_ID) ?: ""
            )
        )
    }

    fun overrideDeviceIDWith(expected: UUID) {
        backingEnvironment.keyValueCache.putString(Environment.Keys.DEVICE_ID to expected.toString())
    }

    fun assertThatTheDeviceIDEquals(expected: UUID) {
        val actual = UUID.fromString(environment.keyValueCache.retrieveString(Environment.Keys.DEVICE_ID) ?: "")
        Assert.assertEquals(expected, actual)
    }

    suspend fun handleRemoteMessagingData(input: Map<String, String>) {
        controller.onFirebaseRemoteMessageReceived(data = input)
    }

    fun assertThatASyncWasTriggered() {
        assertThatASyncResponseAndTwoExperiencesWereRetrieved()
    }

    fun setPushToken(fcmToken: String) {
        controller.setPushToken(fcmToken = fcmToken)
    }
}