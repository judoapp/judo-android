package app.judo.sdk.core.lang

internal class SequenceParser<U, out O>(
    private vararg val parsers: Parser<U, O>,
) : AbstractParser<U, List<O>>() {

    constructor(
        parsers: List<Parser<U, O>>
    ) : this(
        *parsers.toTypedArray()
    )

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, List<O>> {

        val seed: Parser<U, List<O>> = ReturnParser(emptyList())

        return parsers.fold(seed) { acc, parser ->
            BindMapParser(acc, parser) { list, value ->
                list + listOf(value)
            }
        }.parse(input)
    }

}
