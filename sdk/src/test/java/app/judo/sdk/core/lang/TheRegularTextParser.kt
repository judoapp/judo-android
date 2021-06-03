package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Success
import org.junit.Assert.assertEquals
import org.junit.Test

class TheRegularTextParser {

    @Test
    fun `Parses the Handle Barred text`() {
        // Arrange
        val expectedToken = Token.RegularText(
            value = " other text"
        )

        val input = ParserContext(
            text = " other text",
            Unit
        )

        val expected = Success(
            match = Parser.Match(
                value = expectedToken,
                input.copy(
                    position = input.text.length
                )
            ),
        )


        val parser = RegularTextParser<Unit>()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual")

        // Assert
        assertEquals(expected, actual)
    }

}