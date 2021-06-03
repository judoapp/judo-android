package app.judo.sdk.core.repositories

import app.judo.sdk.api.models.*
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.ExperienceRepositoryRobot
import app.judo.sdk.utils.TestJSON
import app.judo.sdk.utils.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
internal class ExperienceRepositoryTests : AbstractRobotTest<ExperienceRepositoryRobot>() {

    override fun robotSupplier(): ExperienceRepositoryRobot {
        return ExperienceRepositoryRobot()
    }

    private val experienceURL: String = "https://test1.judo.app/testexperience"

    @Test
    fun `repository emits Loading first`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = Resource.Loading<Experience>()

        // Act
        val actual = robot.retrieveExperience(experienceURL).first()

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `repository catches exceptions and emits them as an Error`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val expected = ErrorMessages.UNKNOWN_ERROR
        val error = Throwable(expected)

        robot.throwOnNextRequest(error)

        // Act
        var actual: String? = null

        robot.retrieveExperience(experienceURL).collect {
            actual = (it as? Resource.Error)?.error?.message
        }

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `repository emits Success if the response is a 200 and body is not null`() =
        runBlocking(robot.environment.ioDispatcher) {
            // Arrange
            val experience = JsonParser.parseExperience(TestJSON.experience)

            val expected = Resource.Success(experience)

            // Act
            var actual: Resource<Experience, Throwable>? = null

            robot.retrieveExperience(experienceURL).collect {
                actual = it
            }

            // Assert
            (expected.data?.id to expected.data?.nodes?.size) shouldEqual ((actual as? Resource.Success)?.data?.id to (actual as? Resource.Success)?.data?.nodes?.size)
        }

    @Test
    fun `repository emits Error if the response is not 200`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        robot.setResponseCodeTo(500)

        val expected = "End of input"

        // Act
        var actual: String? = null

        robot.retrieveExperience(experienceURL).collect {
            actual = (it as? Resource.Error)?.error?.message
        }

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `repository emits Error if the response body is null`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        robot.setResponseCodeTo(201)

        val expected = "End of input"

        // Act
        var actual: String? = null

        robot.retrieveExperience(experienceURL).collect {
            actual = (it as? Resource.Error)?.error?.message
        }

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `Experiences can be synchronously loaded into memory `() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val screenID = UUID.randomUUID().toString()
        val textId = UUID.randomUUID().toString()

        val expected = null

        val input = Experience(
            id = "1",
            version = 1,
            revisionID = 1,
            nodes = listOf(
                Screen(
                    id = screenID,
                    name = "Screen 1",
                    childIDs = listOf(textId),
                    backgroundColor = ColorVariants(
                        default = Color(
                            0f,
                            red = 0F,
                            green = 0F,
                            blue = 0F,
                        )
                    ),
                    statusBarStyle = StatusBarStyle.DEFAULT
                ),
                Text(
                    id = textId,
                    text = "Hello World coming at you from in memory!",
                    font = Font.Fixed(weight = FontWeight.Bold, size = 20F, isDynamic = false),
                    textAlignment = TextAlignment.CENTER,
                    textColor = ColorVariants(
                        default = Color(
                            1f,
                            red = 1F,
                            green = 1F,
                            blue = 1F,
                        ),
                    )
                )
            ),
            screenIDs = listOf(screenID),
            initialScreenID = screenID,
            appearance = Appearance.AUTO
        )

        // Act
        val actual: Experience? = robot.put(input)

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `Experiences can be synchronously extracted from memory`() = runBlocking(robot.environment.ioDispatcher) {
        // Arrange
        val screenID = UUID.randomUUID().toString()
        val textId = UUID.randomUUID().toString()

        val expected = Experience(
            id = "1",
            version = 1,
            revisionID = 1,
            nodes = listOf(
                Screen(
                    id = screenID,
                    name = "Screen 1",
                    childIDs = listOf(textId),
                    backgroundColor = ColorVariants(
                        default = Color(
                            0f,
                            red = 0F,
                            green = 0F,
                            blue = 0F,
                        )
                    ),
                    statusBarStyle = StatusBarStyle.DEFAULT
                ),
                Text(
                    id = textId,
                    text = "Hello World coming at you from in memory!",
                    font = Font.Fixed(weight = FontWeight.Bold, size = 20F, isDynamic = false),
                    textAlignment = TextAlignment.CENTER,
                    textColor = ColorVariants(
                        default = Color(
                            1f,
                            red = 1F,
                            green = 1F,
                            blue = 1F,
                        ),
                    )
                )
            ),
            screenIDs = listOf(screenID),
            initialScreenID = screenID,
            appearance = Appearance.AUTO
        )

        robot.put(expected)

        // Act
        val actual: Experience? = robot.retrieveById(key = expected.id.toString())

        // Assert
        expected shouldEqual actual
    }

}