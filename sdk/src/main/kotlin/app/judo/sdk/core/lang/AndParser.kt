package app.judo.sdk.core.lang

internal class AndParser<U, A, B>(
    private val parserA: Parser<U, A>,
    private val parserB: Parser<U, B>,
) : AbstractParser<U, Pair<A, B>>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, Pair<A, B>> {

        return BindParser(parserA) { a ->
            MapParser(parserB) { b ->
                a to b
            }
        }.parse(input)

    }

}