package app.judo.sdk.core.sync

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
        val expected = "/test1.judo.app/testexperience"

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
        val expected = "/test1.judo.app/testexperience"

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

    @Test
    fun `Images can be prefetched at sync time`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = "/content.judo.app/images/"

        var callBackWasNeverCalled = true

        // Act
        robot.performSync(prefetchAssets = true) {
            // Assert
            robot.assertTheLastURLPathToBeFetchedStartsWith(
                expected
            )
            callBackWasNeverCalled = false
        }

        // Assert
        Assert.assertFalse(callBackWasNeverCalled)

    }

}