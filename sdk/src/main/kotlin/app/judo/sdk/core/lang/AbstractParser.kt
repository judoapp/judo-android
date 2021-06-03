package app.judo.sdk.core.lang

internal abstract class AbstractParser<U, out O> : Parser<U, O> {

    override fun parse(input: ParserContext<U>): Parser.Result<U, O> {

        if (input.text.isEmpty())
            return Parser.Result.Failure(
                Parser.Error(
                    context = input,
                    message = "Input was empty"
                )
            )

        if (input.position > input.text.lastIndex)
            return Parser.Result.Failure(
                Parser.Error(
                    context = input,
                    message = "Reached the End of the input"
                )
            )

        return parseNonEmptyInput(input)

    }

    protected abstract fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, O>

}