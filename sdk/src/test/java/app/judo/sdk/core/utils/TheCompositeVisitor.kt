package app.judo.sdk.core.utils

import app.judo.sdk.api.models.Visitor
import app.judo.sdk.utils.TestExperience
import org.junit.Assert.assertEquals
import org.junit.Test

class TheCompositeVisitor {


    @Test
    fun `Composes other visitors correctly`() {
        // Arrange

        val expected = listOf("visited 1", "visited 2")

        val v1 = object : Visitor<String> {
            override fun getDefault(): String {
                return "visited 1"
            }
        }

        val v2 = object : Visitor<String> {
            override fun getDefault(): String {
                return "visited 2"
            }
        }

        val input = TestExperience()

        // Act
        val composite = visitorsOf(v1, v2)

        val actual = input.accept(composite)

        // Assert
        assertEquals(expected, actual)
    }

}