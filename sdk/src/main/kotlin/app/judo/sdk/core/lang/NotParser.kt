package app.judo.sdk.core.lang

internal class NotParser<U, A>(
    private val parser: Parser<U, A>,
) : AbstractParser<U, Unit>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, Unit> {

        return when (val result = parser.parse(input)) {

            is Parser.Result.Failure -> {
                Parser.Result.Success(
                    Parser.Match(
                        value = Unit,
                        input
                    )
                )
            }

            is Parser.Result.Success -> {
                Parser.Result.Failure(
                    Parser.Error(
                        context = input,
                        message = "Expected NOT to match ${result.match.value}"
                    )
                )
            }

        }

    }

}