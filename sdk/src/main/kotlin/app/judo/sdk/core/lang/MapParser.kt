package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class MapParser<U, A, B>(
    private val parser: Parser<U, A>,
    private val transform: (input: A) -> B,
) : AbstractParser<U, B>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, B> {

        return when (
            val result = parser.parse(input)
        ) {

            is Failure -> {
                Failure(
                    result.error,
                )
            }

            is Success -> {
                val (value, context) = result.match
                Success(
                    Parser.Match(
                        transform(value),
                        context
                    )
                )
            }

        }

    }

}