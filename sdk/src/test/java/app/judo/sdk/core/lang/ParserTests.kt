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

}