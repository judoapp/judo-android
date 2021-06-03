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