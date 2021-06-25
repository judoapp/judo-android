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

package app.judo.sdk.ui.events

import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.ui.robots.EventBusTestRobot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
internal class EventBusTests : AbstractRobotTest<EventBusTestRobot>() {

    override fun robotSupplier(): EventBusTestRobot {
        return EventBusTestRobot()
    }

    @Test
    fun `given event published when  then subscribers receive it`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = "1"
            val expected2 = "2"
            val expected3 = "3"

            robot.subscribe()

            // Act
            robot.publish(expected)
            robot.publish(expected2)
            robot.publish(expected3)

            // Assert
            robot.assertThatEventsContains(
                event = expected,
                atPosition = 0
            )

            robot.assertThatEventsContains(
                event = expected2,
                atPosition = 1
            )

            robot.assertThatEventsContains(
                event = expected3,
                atPosition = 2
            )
        }

}