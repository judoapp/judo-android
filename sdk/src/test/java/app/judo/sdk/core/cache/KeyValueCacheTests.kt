package app.judo.sdk.core.cache

import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.KeyValueCacheRobot
import app.judo.sdk.utils.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
internal class KeyValueCacheTests : AbstractRobotTest<KeyValueCacheRobot>() {

    override fun robotSupplier(): KeyValueCacheRobot {
        return KeyValueCacheRobot()
    }

    @Test
    fun `Strings can be put into the cache`() {
        // Arrange
        val expected = true

        // Act
        val actual = robot.createString("aKey" to "value")

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `Strings can be retrieved from the cache by a String key`() {
        // Arrange
        val expected = "value"
        robot.createString("aKey" to expected)

        // Act
        val actual = robot.retrieveString("aKey")

        // Assert
        expected shouldEqual actual
    }

}