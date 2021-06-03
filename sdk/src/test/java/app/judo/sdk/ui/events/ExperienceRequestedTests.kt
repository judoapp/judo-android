package app.judo.sdk.ui.events

import android.content.Intent
import android.net.Uri
import app.judo.sdk.api.Judo
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.utils.shouldEqual
import org.junit.Test
import java.util.*

class ExperienceRequestedTests {
    @Test
    fun `handles implicit Intent with experience URL`() {
        // Arrange
        val expected = "https://brand.judo.app/a-Experience"
        val event = ExperienceRequested(experienceURL = expected)

        // Act
        val actual = event.experienceURLForRequest

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `screenID is extracted correctly`() {
        // Arrange
        val expectedScreenID = UUID.randomUUID().toString()
        val expectedExperienceURL = "https://brand.judo.app/a-experience"
        val input = "${expectedExperienceURL}?screenID=$expectedScreenID&foo=thing"
        val event = ExperienceRequested(experienceURL = input)

        // Act
        val actualScreenID = event.screenId
        val actualExperienceURL = event.experienceURLForRequest

        // Assert
        expectedScreenID shouldEqual actualScreenID
        expectedExperienceURL shouldEqual actualExperienceURL
    }

    @Test
    fun `handles implicit Intent with a deep link experience URI`() {
        // Arrange
        val expected = "https://brand.judo.app/a-Experience"
        val event = ExperienceRequested(experienceURL = "brand://brand.judo.app/a-Experience")

        // Act
        val actual = event.experienceURLForRequest

        // Assert
        expected shouldEqual actual
    }
}