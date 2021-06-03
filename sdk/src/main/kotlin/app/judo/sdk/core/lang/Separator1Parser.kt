package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result

internal class Separator1Parser<U, out A, out B>(
    private val parser: Parser<U, A>,
    private val separatorParser: Parser<U, B>,
) : AbstractParser<U, List<A>>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Result<U, List<A>> {

        val manySeparatedParser = ManyParser(AndRightParser(separatorParser, parser))

        return BindMapParser(
            parserA = parser,
            parserB = manySeparatedParser
        ) { head, tail ->
            listOf(head) + tail
        }.parse(input = input)

    }

}