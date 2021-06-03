package app.judo.sdk.core.utils

import app.judo.sdk.api.models.Screen
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.utils.TestJSON
import org.junit.Assert

import org.junit.Before
import org.junit.Test

internal class TheSourceAndParentBuilder {

    @Before
    fun setUp() {
    }

    @Test
    fun `Builds the correct source trees`() {
        // Arrange
        val experience = JsonParser.parseExperience(TestJSON.data_source_experience_single_screen)!!

        val input = experience.nodes<Screen>().first { it.id == experience.initialScreenID }

        val builder = ParentChildExtractor(experience = experience)

        // Act
        val actual = builder.extract(input)

        actual.forEach {
            println(it)
        }

//        println(actual)

        // Assert
        Assert.assertTrue(actual.isNotEmpty())
    }

}