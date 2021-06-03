package app.judo.sdk.core.lang

internal class AndRightParser<U, A, B>(
    private val leftParser: Parser<U, A>,
    private val rightParser: Parser<U, B>,
) : AbstractParser<U, B>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, B> {

        return BindParser(leftParser) {
            MapParser(rightParser) { b ->
                b
            }
        }.parse(input)

    }

}