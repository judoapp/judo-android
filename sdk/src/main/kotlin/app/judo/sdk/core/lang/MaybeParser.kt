package app.judo.sdk.core.lang

internal class MaybeParser<U, A>(
    private val parser: Parser<U, A>,
) : AbstractParser<U, A?>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, A?> {

        val none = ReturnParser<U, A?>(null)

        val orNone = OrParser(parser, none)

        return orNone.parse(input)

    }

}