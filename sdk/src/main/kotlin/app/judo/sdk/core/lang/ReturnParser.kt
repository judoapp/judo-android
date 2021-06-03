package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Success

internal class ReturnParser<U, A>(
    private val value: A,
) : AbstractParser<U, A>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, A> {

        return Success(
            Parser.Match(
                value = value,
                context = input
            )
        )

    }

}