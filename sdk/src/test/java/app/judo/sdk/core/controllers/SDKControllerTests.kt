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

package app.judo.sdk.core.controllers

import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.SDKControllerTestRobot
import app.judo.sdk.utils.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
internal class SDKControllerTests : AbstractRobotTest<SDKControllerTestRobot>() {

    override fun robotSupplier(): SDKControllerTestRobot {
        return SDKControllerTestRobot()
    }

    @Test
    fun `user can initialize the sdk with valid credentials`() {

        // Arrange
        val accessToken = "token1"
        val domainName = "https://test1.judo.app"

        var actual: Throwable? = null

        // Act
        try {

            robot.initializeControllerWith(
                accessToken, domainName
            )

        } catch (e: Throwable) {

            actual = e

        }

        // Assert
        Assert.assertNull(actual)

    }


    @Test
    fun `user can not initialize the sdk with an invalid accessToken`() {

        // Arrange
        val accessToken = ""
        val domainName = "https://test1.judo.app"

        val expected = IllegalArgumentException(
            ErrorMessages.ACCESS_TOKEN_NOT_BLANK
        ).toString()

        var actual: String? = null

        // Act
        try {

            robot.initializeControllerWith(
                accessToken, domainName
            )

        } catch (e: Throwable) {

            actual = e.toString()

        }

        // Assert
        expected shouldEqual actual

    }

    @Test
    fun `user can not initialize the sdk with an invalid domain`() {

        // Arrange
        val accessToken = "token1"
        val domain = ""

        val expected = IllegalArgumentException(
            ErrorMessages.DOMAIN_NAME_NOT_BLANK
        ).toString()

        var actual: String? = null

        // Act
        try {

            robot.initializeControllerWith(
                accessToken = accessToken,
                domain = domain
            )

        } catch (e: Throwable) {

            actual = e.toString()

        }

        // Assert
        expected shouldEqual actual

    }

    @Test
    fun `DEVICE_ID is added to the cache upon initialization`() = runBlocking(robot.environment.ioDispatcher) {

        // Arrange
        val accessToken = "token1"
        val domain = "test1.judo.app"

        // Act
        robot.initializeControllerWith(
            accessToken = accessToken,
            domain = domain
        )

        // Assert
        robot.assertThatTheCacheContainsADeviceID()

    }

    @Test
    fun `DEVICE_ID is not overridden if already in cache`() = runBlocking(robot.environment.ioDispatcher) {

        // Arrange
        val accessToken = "token1"
        val domain = "test1.judo.app"

        val expected = UUID.randomUUID()

        robot.overrideDeviceIDWith(expected)

        // Act
        robot.initializeControllerWith(
            accessToken = accessToken,
            domain = domain
        )

        // Assert
        robot.assertThatTheDeviceIDEquals(expected)

    }
}