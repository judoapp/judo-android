package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

/**
 * Takes a Parser of A and maps its value into a
 * Parser of B via a parser factory.
 *
 * Then returns the result of the product parser.
 *
 * Commonly used to concatenate the results of two parsers in a dynamic way
 */
internal class BindParser<U, A, B>(
    private val parserA: Parser<U, A>,
    private val parserFactory: (value: A) -> Parser<U, B>,
) : AbstractParser<U, B>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, B> {

        return when (
            val result = parserA.parse(input)
        ) {

            is Failure -> {
                Failure(
                    result.error,
                )
            }

            is Success -> {
                val (value, context) = result.match
                parserFactory(value).parse(context)
            }

        }

    }

}