package app.judo.sdk.core.lang

internal class StringParser<U>(
    expected: String,
) : AbstractParser<U, String>() {

    private val parsers = expected.map { expectedChar ->
        CharParser<U>(expectedChar)
    }

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, String> {

        return MapParser(
            parser = SequenceParser(parsers = parsers)
        ) { characters ->
            characters.joinToString(separator = "")
        }.parse(input = input)

    }

}