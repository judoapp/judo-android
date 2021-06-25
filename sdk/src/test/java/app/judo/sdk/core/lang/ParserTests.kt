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

package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Success
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ParserTests {

    @Test
    fun `PredicateParser works`() {
        // Arrange

        val expected = Success(
            Parser.Match(
                value = 'a',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parser = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        // Act
        val actual = parser.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `MapParser works`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'b',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parser = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }


        val mapper = MapParser(
            parser
        ) {
            'b'
        }

        // Act
        val actual = mapper.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `AndParser works`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'a' to 'b',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 2,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )


        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parserB = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'b'
        }

        val parser = AndParser(
            parserA, parserB
        )

        // Act
        val actual = parser.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `AndLeftParser discards the right result`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'a',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 2,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parserB = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'b'
        }

        val parser = AndLeftParser(
            parserA, parserB
        )

        // Act
        val actual = parser.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `AndRightParser discards the left result`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'b',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 2,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parserB = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'b'
        }

        val parser = AndRightParser(
            parserA, parserB
        )

        // Act
        val actual = parser.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `BetweenParser discards the left and right results`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'b',
                context = ParserContext(
                    text = "abc",
                    state = Unit,
                    position = 3,
                )
            )
        )

        val input = ParserContext(
            text = "abc",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parserB = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'b'
        }

        val parserC = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'c'
        }

        val parser = BetweenParser(
            parserA = parserA,
            parserB = parserB,
            parserC = parserC
        )

        // Act
        val actual = parser.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `CharParser parses a single Char`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'a',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parser = CharParser<Unit>(
            'a'
        )

        // Act
        val actual = parser.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `OrParser parses the right parser if the left fails`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'a',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 1,
                )
            )
        )


        val expected2 = Success(
            Parser.Match(
                value = 'b',
                ParserContext(
                    text = "ba",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "ba",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parserB = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'b'
        }

        val parser = OrParser(
            parserA, parserB
        )

        // Act
        val actual = parser.parse(input = input)
        val actual2 = parser.parse(input = input2)

        // Assert
        assertEquals(expected, actual)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `BindParser works`() {
        // Arrange

        val expected = Success(
            Parser.Match(
                value = "ab",
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 2,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parserB = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'b'
        }


        val parser = BindParser(
            parserA
        ) { a: Char ->
            MapParser(parserB) { b ->
                "$a$b"
            }
        }

        // Act
        val actual: Parser.Result<Unit, String> = parser.parse(input = input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `ReturnParser transforms a object into a Parser that returns that object`() {
        // Arrange

        val lambda = {}

        val expected = Success(
            Parser.Match(
                value = lambda,
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 0,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parser = ReturnParser<Unit, () -> Unit>(lambda)

        // Act
        val actual = parser.parse(input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `ApplyParser transforms a into 1`() {
        // Arrange

        val expected = Success(
            Parser.Match(
                value = 1,
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val mapToInt = { char: Char ->
            if (char == 'a') 1 else 0
        }

        val liftedFunctionParser: Parser<Unit, (Char) -> Int> = LiftParser(mapToInt)

        val parserA = PredicateParser<Unit> {
            it == 'a'
        }

        val parser: ApplyParser<Unit, Char, Int> = ApplyParser(liftedFunctionParser, parserA)

        // Act
        val actual = parser.parse(input)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `LiftParser transforms a function with as single parameter into a Parser that returns that function`() {
        // Arrange
        val expected = 97

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parser: Parser<Unit, (Char) -> Int> = LiftParser { char: Char ->
            char.toInt()
        }

        // Act
        val actual = (parser.parse(input) as? Success)?.match?.value?.invoke('a')

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `SequenceParser produces a list`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = listOf('a', 'b'),
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 2,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parserB = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'b'
        }

        val parser: Parser<Unit, List<Char>> =
            SequenceParser(
                parserA,
                parserB
            )

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `StringParser produces a String`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = "ab",
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 2,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val string = "ab"

        val parser: Parser<Unit, String> = StringParser(expected = string)

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `LongParser produces a Long`() {
        // Arrange
        val long: Long = 12345678901

        val expected = Success(
            Parser.Match(
                value = long,
                ParserContext(
                    text = "12345678901",
                    state = Unit,
                    position = 11,
                )
            )
        )

        val input = ParserContext(
            text = "12345678901",
            state = Unit,
        )

        val parser: Parser<Unit, Long> = LongParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `IntegerParser produces a Int`() {
        // Arrange
        val long: Int = 1234567890

        val expected = Success(
            Parser.Match(
                value = long,
                ParserContext(
                    text = "1234567890",
                    state = Unit,
                    position = 10,
                )
            )
        )

        val input = ParserContext(
            text = "1234567890",
            state = Unit,
        )

        val parser: Parser<Unit, Int> = IntegerParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `IntegerParser handles negative values`() {
        // Arrange
        val long: Int = -1234567890

        val expected = Success(
            Parser.Match(
                value = long,
                ParserContext(
                    text = "-1234567890",
                    state = Unit,
                    position = 11,
                )
            )
        )

        val input = ParserContext(
            text = "-1234567890",
            state = Unit,
        )

        val parser: Parser<Unit, Int> = IntegerParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `IntegerParser does not pars Longs`() {
        // Arrange

        val expected = Parser.Result.Failure<Unit, Int>(
            Parser.Error(
                ParserContext(
                    text = "-12345678901",
                    state = Unit,
                ),
                message = "java.lang.NumberFormatException: For input string: \"-12345678901\""
            ),
        )

        val input = ParserContext(
            text = "-12345678901",
            state = Unit,
        )

        val parser: Parser<Unit, Int> = IntegerParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `LongParser handles negative values`() {
        // Arrange
        val long: Long = -12345678901

        val expected = Success(
            Parser.Match(
                value = long,
                ParserContext(
                    text = "-12345678901",
                    state = Unit,
                    position = 12,
                )
            )
        )

        val input = ParserContext(
            text = "-12345678901",
            state = Unit,
        )

        val parser: Parser<Unit, Long> = LongParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `LongParser catches NumberFormatExceptions`() {
        // Arrange
        val expected = Parser.Result.Failure<Unit, Long>(
            Parser.Error(
                ParserContext(
                    text = "12345678901345238347349834864386438",
                    state = Unit,
                ),
                "java.lang.NumberFormatException: For input string: \"12345678901345238347349834864386438\""
            )
        )

        val input = ParserContext(
            text = "12345678901345238347349834864386438",
            state = Unit,
        )

        val parser: Parser<Unit, Long> = LongParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)

    }

    @Test
    fun `DoubleParser produces a Double`() {
        // Arrange
        val long: Double = 0.01242342

        val expected = Success(
            Parser.Match(
                value = long,
                ParserContext(
                    text = "0.01242342",
                    state = Unit,
                    position = 10,
                )
            )
        )

        val input = ParserContext(
            text = "0.01242342",
            state = Unit,
        )

        val parser: Parser<Unit, Double> = DoubleParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `DoubleParser produces handles negative values`() {
        // Arrange
        val long: Double = -0.01242342

        val expected = Success(
            Parser.Match(
                value = long,
                ParserContext(
                    text = "-0.01242342",
                    state = Unit,
                    position = 11,
                )
            )
        )

        val input = ParserContext(
            text = "-0.01242342",
            state = Unit,
        )

        val parser: Parser<Unit, Double> = DoubleParser()

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `ManyParser produces a List of T even if there is no matches`() {

        // Arrange
        val expected = Success(
            Parser.Match(
                value = emptyList<String>(),
                ParserContext(
                    text = "b",
                    state = Unit,
                    position = 0,
                )
            )
        )

        val expected2 = Success(
            Parser.Match(
                value = listOf('a', 'a', 'a', 'a'),
                ParserContext(
                    text = "aaaab",
                    state = Unit,
                    position = 4,
                )
            )
        )

        val input = ParserContext(
            text = "b",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "aaaab",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parser = ManyParser(parser = parserA)

        // Act
        val actual = parser.parse(input)
        val actual2 = parser.parse(input2)

        println("ACTUAL: $actual\n")
        println("ACTUAL2: $actual2\n")

        // Assert
        assertEquals(expected, actual)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `Many1Parser produces a List of T only if there is at least 1 match`() {

        // Arrange
        val expected = Success(
            Parser.Match(
                value = emptyList<String>(),
                ParserContext(
                    text = "b",
                    state = Unit,
                    position = 0,
                )
            )
        )

        val expected2 = Success(
            Parser.Match(
                value = listOf('a', 'a', 'a', 'a'),
                ParserContext(
                    text = "aaaab",
                    state = Unit,
                    position = 4,
                )
            )
        )

        val input = ParserContext(
            text = "b",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "aaaab",
            state = Unit,
        )
        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parser: Parser<Unit, List<Char>> = Many1Parser(parser = parserA)

        // Act
        val actual = parser.parse(input)
        val actual2 = parser.parse(input2)

        println("ACTUAL: $actual\n")
        println("ACTUAL2: $actual2\n")

        // Assert
        assertTrue(actual is Parser.Result.Failure)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `MaybeParser succeeds if the value is there or not`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = 'a',
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val expected2 = Success(
            Parser.Match(
                value = null,
                ParserContext(
                    text = "b",
                    state = Unit,
                    position = 0,
                )
            )
        )

        val input = ParserContext(
            text = "ab",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "b",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parser: Parser<Unit, Char?> = MaybeParser(parser = parserA)

        // Act
        val actual = parser.parse(input)
        val actual2 = parser.parse(input2)

        println("ACTUAL: $actual\n")
        println("ACTUAL2: $actual2\n")

        // Assert
        assertEquals(expected, actual)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `NotParser fails if the given parser succeeds`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = Unit,
                ParserContext(
                    text = "b",
                    state = Unit,
                    position = 0,
                )
            )
        )

        val input = ParserContext(
            text = "b",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "ab",
            state = Unit,
        )

        val parserA = PredicateParser<Unit> { theCharacter ->
            theCharacter == 'a'
        }

        val parser = NotParser(parser = parserA)

        // Act
        val actual = parser.parse(input)
        val actual2 = parser.parse(input2)

        println("ACTUAL: $actual\n")
        println("ACTUAL2: $actual2\n")

        // Assert
        assertEquals(expected, actual)
        assertTrue(actual2 is Parser.Result.Failure)
    }

    @Test
    fun `Separator1Parser matches 1 or more separated values`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = listOf('a', 'b', 'c'),
                ParserContext(
                    text = "a,b,c",
                    state = Unit,
                    position = 5,
                )
            )
        )

        val input = ParserContext(
            text = "a,b,c",
            state = Unit,
        )

        val expected2 = Success(
            Parser.Match(
                value = listOf('a'),
                ParserContext(
                    text = "ab",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val input2 = ParserContext(
            text = "ab",
            state = Unit,
        )

        val letterParser = PredicateParser<Unit> { theCharacter ->
            theCharacter.isLetter()
        }

        val separatorParser = CharParser<Unit>(',')

        val parser = Separator1Parser(
            parser = letterParser,
            separatorParser = separatorParser
        )

        // Act
        val actual = parser.parse(input)
        val actual2 = parser.parse(input2)

        println("ACTUAL: $actual\n")
        println("ACTUAL2: $actual2\n")

        // Assert
        assertEquals(expected, actual)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `Separator1Parser fails when there is not at least 1 match`() {
        // Arrange
        val expected = Parser.Result.Failure<Unit, List<Char>>(
            Parser.Error(
                ParserContext(
                    text = "1,a,b",
                    state = Unit,
                    position = 0,
                ),
                "Character 1 did not match the predicate"
            )
        )

        val input = ParserContext(
            text = "1,a,b",
            state = Unit,
        )

        val letterParser = PredicateParser<Unit> { theCharacter ->
            theCharacter.isLetter()
        }

        val separatorParser = CharParser<Unit>(',')

        val parser = Separator1Parser(
            parser = letterParser,
            separatorParser = separatorParser
        )

        // Act
        val actual = parser.parse(input)

        println("ACTUAL: $actual\n")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `SeparatorParser matches 0 or more separated values`() {
        // Arrange
        val expected = Success(
            Parser.Match(
                value = listOf('a', 'b', 'c'),
                ParserContext(
                    text = "a,b,c",
                    state = Unit,
                    position = 5,
                )
            )
        )

        val input = ParserContext(
            text = "a,b,c",
            state = Unit,
        )

        val expected2 = Success(
            Parser.Match(
                value = listOf<Char>(),
                ParserContext(
                    text = "1ab",
                    state = Unit,
                    position = 0,
                )
            )
        )

        val input2 = ParserContext(
            text = "1ab",
            state = Unit,
        )

        val letterParser = PredicateParser<Unit> { theCharacter ->
            theCharacter.isLetter()
        }

        val separatorParser = CharParser<Unit>(',')

        val parser = SeparatorParser(
            parser = letterParser,
            separatorParser = separatorParser
        )

        // Act
        val actual = parser.parse(input)
        val actual2 = parser.parse(input2)

        println("ACTUAL: $actual\n")
        println("ACTUAL2: $actual2\n")

        // Assert
        assertEquals(expected, actual)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `BooleanParser parses true or false`() {
        // Arrange
        val expected1 = Success(
            Parser.Match(
                value = true,
                ParserContext(
                    text = "true",
                    state = Unit,
                    position = 4,
                )
            )
        )


        val expected2 = Success(
            Parser.Match(
                value = false,
                ParserContext(
                    text = "false",
                    state = Unit,
                    position = 5,
                )
            )
        )

        val input = ParserContext(
            text = "true",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "false",
            state = Unit,
        )

        val parser = BooleanParser<Unit>()

        // Act
        val actual = parser.parse(input = input)
        val actual2 = parser.parse(input = input2)

        // Assert
        assertEquals(expected1, actual)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `BooleanParser can ignore casing`() {
        // Arrange
        val expected1 = Success(
            Parser.Match(
                value = true,
                ParserContext(
                    text = "tRuE",
                    state = Unit,
                    position = 4,
                )
            )
        )


        val expected2 = Success(
            Parser.Match(
                value = false,
                ParserContext(
                    text = "fAlSe",
                    state = Unit,
                    position = 5,
                )
            )
        )

        val input = ParserContext(
            text = "tRuE",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "fAlSe",
            state = Unit,
        )

        val parser = BooleanParser<Unit>(ignoreCase = true)

        // Act
        val actual = parser.parse(input = input)
        val actual2 = parser.parse(input = input2)

        // Assert
        assertEquals(expected1, actual)
        assertEquals(expected2, actual2)
    }

    @Test
    fun `AnyOfParser matches any parsers given to it`() {
        // Arrange
        val expected1 = Success(
            Parser.Match(
                value = 'a',
                ParserContext(
                    text = "a,b",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val expected2 = Success(
            Parser.Match(
                value = 'b',
                ParserContext(
                    text = "b,a",
                    state = Unit,
                    position = 1,
                )
            )
        )

        val expected3 = Parser.Result.Failure<Unit, Char>(
            Parser.Error(
                ParserContext(
                    text = "b,a",
                    state = Unit,
                ),
                message = "Can not match an empty list of parsers."
            )
        )

        val input = ParserContext(
            text = "a,b",
            state = Unit,
        )

        val input2 = ParserContext(
            text = "b,a",
            state = Unit,
        )

        val a = PredicateParser<Unit> {
            it == 'a'
        }

        val b = PredicateParser<Unit> {
            it == 'b'
        }

        val parser = AnyOfParser(a, b)
        val parser2 = AnyOfParser<Unit, Char>(emptyList())

        // Act
        val actual = parser.parse(input = input)
        val actual2 = parser.parse(input = input2)
        val actual3 = parser2.parse(input = input2)

        // Assert
        assertEquals(expected1, actual)
        assertEquals(expected2, actual2)
        assertEquals(expected3, actual3)
    }

}