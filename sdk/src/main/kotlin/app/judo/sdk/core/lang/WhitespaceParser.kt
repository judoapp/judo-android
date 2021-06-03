package app.judo.sdk.core.lang

internal class WhitespaceParser<U> : AbstractParser<U, String>() {

    private val whiteSpacePredicate = PredicateParser<U> {
        it.isWhitespace()
    }

    private val spaceParser: Parser<U, String> = MapParser(
        parser = ManyParser(parser = whiteSpacePredicate)
    ) { value ->
        value.joinToString(separator = "")
    }

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, String> {
        return spaceParser.parse(input)
    }

}