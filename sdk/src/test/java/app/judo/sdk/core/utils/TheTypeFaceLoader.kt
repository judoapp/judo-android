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

package app.judo.sdk.core.utils

import android.graphics.Typeface
import app.judo.sdk.api.models.Text
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.ExperienceRepositoryRobot
import app.judo.sdk.utils.FakeFontResourceService
import app.judo.sdk.utils.TestExperience
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

internal class TheTypeFaceLoader : AbstractRobotTest<ExperienceRepositoryRobot>() {

    override fun robotSupplier(): ExperienceRepositoryRobot {
        return ExperienceRepositoryRobot()
    }

    private val defaultTypeface: Typeface = Typeface.create("sans-serif", Typeface.NORMAL)

    @Test
    fun `Sets all the typeface variables on a given experience`() = runBlocking(robot.testCoroutineDispatcher) {
        // Arrange
        val experience = TestExperience()

        val typefaceMap = FakeFontResourceService().getTypefacesFor(experience.fonts)

        // Act
        TypeFaceLoader(typefaces = typefaceMap).loadTypefacesInto(experience)

        // Assert
        DynamicVisitor {

            on<Text> { text ->
                val message = "Inspecting:\n\t$text\n\n\tTypeface: ${text.typeface}"
                println(message)
                Assert.assertNotNull("Typeface is expected to not be null but was null", text.typeface)
                Assert.assertTrue(
                    "Typeface: ${text.typeface} is expected to be $defaultTypeface",
                    text.typeface == defaultTypeface
                )
            }

        }.visit(experience)

    }

}