package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Success
import org.junit.Assert.assertEquals
import org.junit.Test

class TheHandleBarParser {

    @Test
    fun `Parses the Handle Barred text with function name correctly`() {
        // Arrange

        val expectedToken = Token.HandleBarExpression(
            value = "{{ date data.key1.value   \"YY-mm\" }}",
            keyword = Keyword.DATA,
            functionName = FunctionName.DATE,
            functionArgument = "YY-mm",
            keys = listOf("key1", "value")
        )

        val input = ParserContext(
            text = "{{ date data.key1.value   \"YY-mm\" }} other text",
            state = Unit,
        )

        val expected = Success(
            match = Parser.Match(
                expectedToken,
                input.copy(
                    position = expectedToken.value.length
                )
            ),
        )

        val parser = HandleBarParser<Unit>()

        // Act
        val actual = parser.parse(input)

        val keys = (actual as? Success)?.match?.value?.keys

        println("ACTUAL: $actual")
        println("KEYS: $keys")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Parses the Handle Barred text without function name correctly`() {
        // Arrange

        val expectedToken = Token.HandleBarExpression(
            value = "{{ data.key1.value }}",
            keyword = Keyword.DATA,
            keys = listOf("key1", "value")
        )

        val input = ParserContext(
            text = "{{ data.key1.value }} other text",
            state = Unit,
        )

        val expected = Success(
            match = Parser.Match(
                expectedToken,
                input.copy(
                    position = expectedToken.value.length
                )
            ),
        )

        val parser = HandleBarParser<Unit>()

        // Act
        val actual = parser.parse(input)

        val keys = (actual as? Success)?.match?.value?.keys

        println("ACTUAL: $actual")
        println("KEYS: $keys")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Parses the Handle Barred text`() {
        // Arrange

        val expectedToken = Token.HandleBarExpression(
            value = "{{ date data.key1.value   \"YY-mm\" }}",
            keyword = Keyword.DATA,
            functionName = FunctionName.DATE,
            functionArgument = "YY-mm",
            keys = listOf("key1", "value")
        )

        val input = ParserContext(
            text = "{{ date data.key1.value   \"YY-mm\" }} other text",
            state = Unit,
        )

        val expected = Success(
            match = Parser.Match(
                expectedToken,
                input.copy(
                    position = expectedToken.value.length
                )
            ),
        )

        val expectedToken2 = Token.HandleBarExpression(
            value = "{{ user.key1.value }}",
            keyword = Keyword.USER,
            keys = listOf("key1", "value")
        )

        val input2 = ParserContext(
            text = "{{ user.key1.value }} other text",
            state = Unit,
        )

        val expected2 = Success(
            match = Parser.Match(
                expectedToken2,
                input2.copy(
                    position = expectedToken2.value.length
                )
            ),
        )

        val parser = HandleBarParser<Unit>()

        // Act
        val actual = parser.parse(input)
        val actual2 = parser.parse(input2)

        val keys = (actual as? Success)?.match?.value?.keys
        val keys2 = (actual2 as? Success)?.match?.value?.keys

        println("ACTUAL: $actual")
        println("KEYS: $keys")

        println("ACTUAL2: $actual2")
        println("KEYS2: $keys2")

        // Assert
        assertEquals(expected, actual)
        assertEquals(expected2, actual2)
    }

}