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

package app.judo.sdk.core.sync

import app.judo.sdk.BuildConfig
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.SynchronizerTestRobot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
internal class SynchronizerTests : AbstractRobotTest<SynchronizerTestRobot>() {

    override fun robotSupplier(): SynchronizerTestRobot {
        return SynchronizerTestRobot()
    }

    @Test
    fun `URL in SyncResponse is used to fetch a Experience`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = "/test1.judo.app/testexperience?apiVersion=${BuildConfig.API_VERSION}"

        // Act
        robot.performSync()

        // Assert
        robot.assertTheLastURLPathToBeFetchedWas(
            expected
        )
    }

    @Test
    fun `callback is run when the sync is complete`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = "/test1.judo.app/testexperience?apiVersion=${BuildConfig.API_VERSION}"

        var callBackWasNeverCalled = true

        // Act
        robot.performSync() {
            // Assert
            robot.assertTheLastURLPathToBeFetchedWas(
                expected
            )
            callBackWasNeverCalled = false
        }

        // Assert
        Assert.assertFalse(callBackWasNeverCalled)

    }
}