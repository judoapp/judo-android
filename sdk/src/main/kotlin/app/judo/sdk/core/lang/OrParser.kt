package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class OrParser<U, A>(
    private val parserA: Parser<U, A>,
    private val parserB: Parser<U, A>,
) : AbstractParser<U, A>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, A> {

        return when (
            val firstResult = parserA.parse(input)
        ) {

            is Failure -> {
                parserB.parse(input)
            }

            is Success -> {
                firstResult
            }

        }

    }

}