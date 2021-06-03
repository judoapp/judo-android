package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Success

internal class LiftParser<U, A, B>(
    private val function: (A) -> B,
) : AbstractParser<U, (A) -> B>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, (A) -> B> {

        return Success(
            Parser.Match(
                value = function,
                context = input
            )
        )

    }

}