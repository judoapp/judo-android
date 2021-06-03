package app.judo.sdk.core.lang

internal class AndLeftParser<U, A, B>(
    private val leftParser: Parser<U, A>,
    private val rightParser: Parser<U, B>,
) : AbstractParser<U, A>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, A> {

        return BindParser(leftParser) { a ->
            MapParser(rightParser) {
                a
            }
        }.parse(input)

    }

}