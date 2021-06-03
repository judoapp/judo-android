package app.judo.sdk.core.lang

import java.util.regex.Pattern

internal class TokenizerImpl : Tokenizer {

    private val regularTextTextParser: Parser<Unit, Token.RegularText> = RegularTextParser()

    private val handleBarParser: Parser<Unit, Token.HandleBarExpression> = HandleBarParser()

    private val parser: Parser<Unit, List<Token>> = ManyParser(
        OrParser(
            parserA = handleBarParser,
            parserB = regularTextTextParser
        )
    )

    override fun tokenize(text: String): List<Token> {

        val openPattern = Regex("\\{\\{")

        val closingPattern = Regex(Pattern.quote("}}"))

        val openingCount = openPattern.findAll(input = text).count()

        val closingCount = closingPattern.findAll(input = text).count()

        val textIsImbalanced = openingCount != closingCount

        if (textIsImbalanced) {
            throw IllegalStateException(
                """ALL HANDLEBARS MUST BE OPENED AND CLOSED CORRECTLY
                    |The amount of {{ must be equal to the amount of }}
                    |{{ = $openingCount & }} = $closingCount
                """.trimMargin()
            )
        }

        val input = ParserContext(
            text = text,
            Unit,
        )

        return when (val result = parser.parse(input = input)) {

            is Parser.Result.Failure -> {
                emptyList()
            }

            is Parser.Result.Success -> {
                result.match.value
            }

        }
    }

}