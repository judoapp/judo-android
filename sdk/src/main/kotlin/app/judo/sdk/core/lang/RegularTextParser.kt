package app.judo.sdk.core.lang

internal class RegularTextParser<U> : AbstractParser<U, Token.RegularText>() {

    private val notHandleBar = NotParser(StringParser<U>("{{"))

    private val anyParser = PredicateParser<U> {
        true
    }

    private val textParser = ManyParser(
        AndRightParser(
            leftParser = notHandleBar,
            rightParser = anyParser
        )
    )

    private val tokenParser: Parser<U, Token.RegularText> = MapParser(textParser) {
        val value = it.joinToString("")
        Token.RegularText(
            value
        )
    }

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, Token.RegularText> {

        return MapParser(tokenParser) { token ->
            token.copy(
                position = input.position
            )
        }.parse(input)

    }

}