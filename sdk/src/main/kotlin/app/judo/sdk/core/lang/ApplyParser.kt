package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class ApplyParser<U, A, B>(
    private val functionParser: Parser<U, (input: A) -> B>,
    private val parserA: Parser<U, A>,
) : AbstractParser<U, B>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, B> {

        val pairParser = AndParser(functionParser, parserA)

        val parserB: Parser<U, B> = MapParser(pairParser) { (mapper: (input: A) -> B, value: A) ->
            mapper(value)
        }

        return when (val result = parserB.parse(input)) {
            is Failure -> {
                Failure(result.error)
            }
            is Success -> {
                val (value, context) = result.match
                Success(
                    Parser.Match(
                        value = value,
                        context = context
                    )
                )
            }
        }

    }

}