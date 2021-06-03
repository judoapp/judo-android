package app.judo.sdk.core.services

import app.judo.sdk.core.implementations.TranslatorImpl
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.TranslatorRobot
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

internal class TheTranslator : AbstractRobotTest<TranslatorRobot>() {

    override fun robotSupplier(): TranslatorRobot {
        return TranslatorRobot()
    }

    private val theTranslationMap: Map<String, Map<String, String>> = mapOf(
        "fr" to mapOf("hello world" to "Bonjour le monde"),
        "fr-rUS" to mapOf("hello world" to "Bonjour le monde"),
        "ar" to mapOf("hello world" to "مرحبا بالعالم"),
    )

    @Test
    fun `Algorithm returns the given text if no translation is possible`() =
        runBlocking(robot.testCoroutineDispatcher) {

            // Arrange
            val expected = "hello world"

            val theTextToTranslate = "hello world"

            val theUsersPreferredLanguages = listOf("de")

            val theTranslator = TranslatorImpl(
                theTranslationMap,
            ) {
                theUsersPreferredLanguages
            }

            var actual: String? = null

            // Act
            actual = theTranslator.translate(theTextToTranslate)

            // Assert
            Assert.assertEquals(expected, actual)

        }

    @Test
    fun `Algorithm finds the closest matching translation with dashes`() = runBlocking(robot.testCoroutineDispatcher) {

        // Arrange
        val expected = "مرحبا بالعالم"

        val theTextToTranslate = "hello world"

        val theUsersPreferredLanguages = listOf("ar-ES", "fr-CH", "en-US")

        val theTranslator = TranslatorImpl(
            theTranslationMap,
        ) {
            theUsersPreferredLanguages
        }

        var actual: String? = null

        // Act
        actual = theTranslator.translate(theTextToTranslate)

        // Assert
        Assert.assertEquals(expected, actual)

    }

    @Test
    fun `Algorithm finds the closest matching translation with underscores`() =
        runBlocking(robot.testCoroutineDispatcher) {

            // Arrange
            val expected = "Bonjour le monde"

            val theTextToTranslate = "hello world"

            val theUsersPreferredLanguages = listOf("fr_CH", "ar_ES", "en_US")

            val theTranslator = TranslatorImpl(
                theTranslationMap,
            ) {
                theUsersPreferredLanguages
            }

            var actual: String? = null

            // Act
            actual = theTranslator.translate(theTextToTranslate)

            // Assert
            Assert.assertEquals(expected, actual)

        }

}