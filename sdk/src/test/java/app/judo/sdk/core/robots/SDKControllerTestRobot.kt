package app.judo.sdk.core.robots

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

    fun initializeControllerWith(accessToken: String, domains: Array<out String>) {

        backingEnvironment.domainNames =
            domains.toSet()

        controller = SDKControllerImpl().apply {
            logger = TestLoggerImpl()
            this.environment = this@SDKControllerTestRobot.environment
            initialize(
                application = application,
                accessToken = accessToken,
                domains = domains
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

        Assert.assertTrue(syncResponse != null && experienceResponse != null && experienceResponses.size == 2)

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

    suspend fun setPushToken(fcmToken: String) {
        controller.setPushToken(fcmToken = fcmToken)
    }

    fun assertThatRegistrationResponseWasReceived() {
        val actual = responses.find { it.second == TestJSON.register_response }
        Assert.assertNotNull(actual)
    }

}