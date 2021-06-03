package app.judo.sdk.core.lang

internal class IdentifierParser<U>(
    private val invalidCharacter: List<Char> = emptyList()
) : AbstractParser<U, String>() {

    constructor(
        vararg invalidCharacters: Char
    ) : this(invalidCharacters.toList())

    private val validIdentifierCharacterParser = PredicateParser<U> { theCharacter ->
        !invalidCharacter.contains(theCharacter)
    }

    private val parser = MapParser(
        parser = Many1Parser(validIdentifierCharacterParser)
    ) { characters ->
        characters.joinToString(separator = "")
    }

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, String> {
        return parser.parse(input)
    }

}