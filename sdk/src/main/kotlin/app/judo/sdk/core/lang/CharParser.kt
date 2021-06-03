package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class CharParser<U>(
    private val expected: Char,
) : AbstractParser<U, Char>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, Char> {

        return when (val result = PredicateParser<U> {
            it == expected
        }.parse(input)) {
            is Success -> result
            is Failure -> Failure(
                Parser.Error(
                    input,
                    "Expected: $expected but found: ${input.text.first()}"
                )
            )
        }

    }

}