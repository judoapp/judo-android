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

package app.judo.sdk.ui.models

import app.judo.sdk.api.models.DataSource
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Text
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.ExperienceTree
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
    fun `initializeFromUrl Updates State To RetrievedTree`() = runBlocking {
        // Act
        robot.initializeExperienceFromURL("https://test1.judo.app/testexperience")

        // Assert
        robot.assertThatExperienceStateAtPositionEquals(
            position = 2,
            state = ExperienceState.RetrievedTree(
                experienceTree = ExperienceTree(JsonParser.parseExperience(TestJSON.experience)!!)
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
            state = ExperienceState.RetrievedTree(
                experienceTree = ExperienceTree(experience)
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

            delay(2000)

            // Assert
            var actual = 0
            val inspector = DynamicVisitor {

                on<Experience> { exp ->
                    exp.nodes.forEach { node -> node.accept(this) }
                }

                on<DataSource> { node ->
                    println("DATA: ${node.data}")
                    if (node.data != null) actual++
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

        delay(1000)

        // Assert
        var actual = 0
        val inspector = DynamicVisitor {

            on<Experience> { exp ->
                exp.nodes.forEach { node ->
                    node.accept(this)
                }
            }

            on<DataSource> { node ->
                println("DATA: ${node.data}")
                if (node.data != null) actual++
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

        val userInfoSupplier =
            mapOf(
                "firstName" to "Jane",
                "lastName" to "Doe"
            )

        robot.setUserInfoTraits(
            userInfoSupplier
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

        val userInfoTraits =
            mapOf(
                "firstName" to "Jane",
                "lastName" to "Doe"
            )

        robot.setUserInfoTraits(
            userInfoTraits
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