package app.judo.sdk.core.services

import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.ImageServiceRobot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
internal class ImageServiceTests : AbstractRobotTest<ImageServiceRobot>() {

    override fun robotSupplier(): ImageServiceRobot {
        return ImageServiceRobot()
    }

    @Test
    fun `given a url when retrieveImages then return correct results`() = runBlocking(robot.testCoroutineDispatcher) {
        // Arrange
        val url = "https://content.judo.app/images/0f29b839b787f9ff5fe427afaf1b2986b5d4be0894518643c5509ef10b142ba6.png"

        // Act
        val results = robot.getImages(url)

        // Assert
        Assert.assertTrue(results.firstOrNull() is ImageService.Result.Success)
    }

}