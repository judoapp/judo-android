package app.judo.sdk.ui.models

import app.judo.sdk.api.models.DataSource
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Text
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.ui.robots.ExperienceViewModelRobot
import app.judo.sdk.core.utils.DynamicVisitor
import app.judo.sdk.utils.TestJSON
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
internal class ExperienceViewModelTests : AbstractRobotTest<ExperienceViewModelRobot>() {

    override fun robotSupplier(): ExperienceViewModelRobot {
        return ExperienceViewModelRobot()
    }

    @Test
    fun `ExperienceState starts Empty`() = runBlocking {
        // Assert
        robot.assertThatExperienceStateAtPositionEquals(
            position = 0,
            state = ExperienceState.Empty
        )
    }

    @Test
    fun `initializeFromUrl Updates State To Loading`() = runBlocking {
        // Act
        robot.initializeExperienceFromURL("https://test1.judo.app/testexperience")

        // Assert
        robot.assertThatExperienceStateAtPositionEquals(
            position = 1,
            state = ExperienceState.Loading
        )
    }

    @Test
    fun `initializeFromUrl Updates State To Retrieved`() = runBlocking {
        // Act
        robot.initializeExperienceFromURL("https://test1.judo.app/testexperience")

        // Assert
        robot.assertThatExperienceStateAtPositionEquals(
            position = 2,
            state = ExperienceState.Retrieved(
                experience = JsonParser.parseExperience(TestJSON.experience)!!
            )
        )
    }

    @Test
    fun `initializeFromMemory Updates State To Retrieved`() = runBlocking {
        // Arrange
        val experience = JsonParser.parseExperience(TestJSON.experience)!!
        robot.loadExperienceIntoMemory(
            experience
        )

        // Act
        robot.initializeExperienceFromMemory(
            experience.id
        )

        // Assert
        robot.assertThatExperienceStateAtPositionEquals(
            position = 1,
            state = ExperienceState.Retrieved(
                experience = experience
            )
        )
    }

    @Test
    fun `DataSources are loaded into experiences loaded from a URL`() {
        runBlocking {

            // Arrange
            val expected = 2

            // Act
            robot.initializeExperienceFromURL("https://test1.judo.app/datasourcetestexperience")

            // Assert
            var actual = 0
            val inspector = DynamicVisitor {

                on<Experience> { exp ->
                    exp.nodes.forEach { node -> node.accept(this) }
                }

                on<DataSource> { node ->
                    println("DAO: ${node.jsonDAO?.value()}")
                    if (node.jsonDAO != null) actual++
                }

            }

            robot.inspectExperienceAt(position = 2, inspector)
            Assert.assertEquals(expected, actual)

        }

    }

    @Test
    fun `DataSources are loaded into in memory experiences`() = runBlocking {
        // Arrange
        val expected = 2
        val experience = JsonParser.parseExperience(TestJSON.data_source_experience_single_screen)!!
        robot.loadExperienceIntoMemory(
            experience
        )

        // Act
        robot.initializeExperienceFromMemory(
            experience.id
        )

        delay(500)

        // Assert
        var actual = 0
        val inspector = DynamicVisitor {

            on<Experience> { exp ->
                exp.nodes.forEach { node ->
                    node.accept(this)
                }
            }

            on<DataSource> { node ->
                println("DAO: ${node.jsonDAO}")
                if (node.jsonDAO != null) actual++
            }

        }

        experience.accept(inspector)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `User data is loaded into in memory experiences`() = runBlockingTest {
        /// Arrange
        val expected = "Hello, Jane Doe"
        val experience = JsonParser.parseExperience(TestJSON.user_data_experience)!!

        val userDataSupplier = UserDataSupplier {
            mapOf(
                "firstName" to "Jane",
                "lastName" to "Doe"
            )
        }

        robot.setUserDataSupplierTo(
            userDataSupplier
        )

        robot.loadExperienceIntoMemory(
            experience
        )

        // Act
        robot.initializeExperienceFromMemory(
            experience.id
        )

        delay(500)

        // Assert
        var actual = ""
        val inspector = DynamicVisitor {

            on<Experience> { exp ->
                exp.nodes.forEach { node ->
                    node.accept(this)
                }
            }

            on<Text> { node ->
                println(node.interpolatedText)
                actual = node.interpolatedText
            }

        }

        experience.accept(inspector)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `User data is loaded into experiences loaded from a URL`() = runBlocking {
        /// Arrange
        val expected = "Hello, Jane Doe"

        val userDataSupplier = UserDataSupplier {
            mapOf(
                "firstName" to "Jane",
                "lastName" to "Doe"
            )
        }

        robot.setUserDataSupplierTo(
            userDataSupplier
        )

        // Act
        robot.initializeExperienceFromURL("https://test1.judo.app/userdatatestexperience")

        // Assert
        var actual = ""
        val inspector = DynamicVisitor {

            on<Experience> { exp ->
                exp.nodes.forEach { node ->
                    node.accept(this)
                }
            }

            on<Text> { node ->
                println(node.interpolatedText)
                actual = node.interpolatedText
            }

        }

        robot.inspectExperienceAt(
            position = 2,
            inspector
        )

        Assert.assertEquals(expected, actual)
    }

}