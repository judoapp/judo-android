package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result

internal class SeparatorParser<U, out A, out B>(
    private val parser: Parser<U, A>,
    private val separatorParser: Parser<U, B>,
) : AbstractParser<U, List<A>>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Result<U, List<A>> {

        return when (val result = Separator1Parser(parser, separatorParser).parse(input)) {
            is Result.Failure -> {
                Result.Success(
                    Parser.Match(
                        value = emptyList(),
                        context = result.error.context
                    )
                )
            }
            is Result.Success -> {
                result
            }
        }

    }

}