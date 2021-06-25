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

import app.judo.sdk.core.data.RegistrationRequestBody
import app.judo.sdk.core.data.RegistrationResponse
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.DevicesServiceRobot
import app.judo.sdk.utils.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
internal class DevicesServiceTest : AbstractRobotTest<DevicesServiceRobot>() {

    override fun robotSupplier(): DevicesServiceRobot {
        return DevicesServiceRobot()
    }

    @Test
    fun `requests contains Accept json header`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = "application/json"
        val deviceID = UUID.randomUUID().toString()
        val pushToken = "aToken"
        val environmentType = Environment.Type
        val body = RegistrationRequestBody(
            deviceID = deviceID,
            deviceToken = pushToken,
            environment = environmentType
        )

        robot.register(body)

        // Act
        val actual = robot.getRequestHeader("Accept")

        // Assert
        expected shouldEqual actual
    }

    @Test
    @Ignore("Brotli support will be added back in the future")
    fun `requests contain Accept-Encoding for brotli headers`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = "br"
        val deviceID = UUID.randomUUID().toString()
        val pushToken = "aToken"
        val environmentType = Environment.Type
        val body = RegistrationRequestBody(
            deviceID = deviceID,
            deviceToken = pushToken,
            environment = environmentType
        )

        robot.register(body)

        // Act
        val actual = robot.getRequestHeader("Accept-Encoding")

        // Assert
        Assert.assertTrue(actual?.contains(expected) == true)
    }

    @Test
    fun `requests contain Accept-Encoding for gzip headers`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = "gzip"
        val deviceID = UUID.randomUUID().toString()
        val pushToken = "aToken"
        val environmentType = Environment.Type
        val body = RegistrationRequestBody(
            deviceID = deviceID,
            deviceToken = pushToken,
            environment = environmentType
        )

        robot.register(body)

        // Act
        val actual = robot.getRequestHeader("Accept-Encoding")

        // Assert
        Assert.assertTrue(actual?.contains(expected) == true)
    }

    @Test
    fun `requests contain Judo-Access-Token header`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = robot.environment.accessToken

        val deviceID = UUID.randomUUID().toString()
        val pushToken = "aToken"
        val environmentType = Environment.Type
        val body = RegistrationRequestBody(
            deviceID = deviceID,
            deviceToken = pushToken,
            environment = environmentType
        )

        robot.register(body)

        // Act
        val actual = robot.getRequestHeader("Judo-Access-Token")

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `requests contain Judo-Device-ID header`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val deviceId = UUID.randomUUID().toString()
        val pushToken = "aToken"
        val environmentType = Environment.Type
        val body = RegistrationRequestBody(
            deviceID = deviceId,
            deviceToken = pushToken,
            environment = environmentType
        )

        robot.setDeviceIdTo(deviceId)

        robot.register(body)

        // Act
        val actual = robot.getRequestHeader("Judo-Device-ID")

        // Assert
        deviceId shouldEqual actual
    }

    @Test
    fun `response is de-serialized correctly`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange

        val deviceID = UUID.randomUUID().toString()
        val pushToken = "aToken"
        val environmentType = Environment.Type
        val body = RegistrationRequestBody(
            deviceID = deviceID,
            deviceToken = pushToken,
            environment = environmentType
        )

        val expected = RegistrationResponse(
            appId = 9,
            deviceToken = pushToken,
            isProduction = false
        )

        // Act
        val actual = robot.register(body).body()

        // Assert
        expected shouldEqual actual
    }
}