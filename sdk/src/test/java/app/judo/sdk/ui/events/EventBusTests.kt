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