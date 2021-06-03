package app.judo.sdk.core.lang

/**
 * Runs all the parsers and discards the left and right results
 */
internal class BetweenParser<U, A, B, C>(
    private val parserA: Parser<U, A>,
    private val parserB: Parser<U, B>,
    private val parserC: Parser<U, C>,
) : AbstractParser<U, B>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, B> {

        val bParser = AndRightParser(
            leftParser = parserA,
            rightParser = parserB
        )

        return BindParser(bParser) { b ->
            MapParser(parser = parserC) {
                b
            }
        }.parse(input)
    }

}