package app.judo.sdk.core.lang

/**
 * Takes a Parser of A and maps its value into a
 * Parser of B via a parser factory.
 *
 * Then returns the result of the product parser.
 *
 * Commonly used to concatenate the results of two parsers in a dynamic way
 */
internal class BindMapParser<U, A, B, C>(
    private val parserA: Parser<U, A>,
    private val parserFactory: (value: A) -> Parser<U, B>,
    private val transform: (A, B) -> C
) : AbstractParser<U, C>() {

    constructor(
        parserA: Parser<U, A>,
        parserB: Parser<U, B>,
        transform: (A, B) -> C
    ) : this(parserA, { parserB }, transform)

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, C> {

        return BindParser(parserA) { a ->
            MapParser(parserFactory(a)) { b ->
                transform(a, b)
            }
        }.parse(input = input)

    }

}