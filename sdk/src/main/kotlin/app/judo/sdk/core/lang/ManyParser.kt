package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result
import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class ManyParser<U, out O>(
    private val parser: Parser<U, O>,
) : AbstractParser<U, List<O>>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Result<U, List<O>> {

        return when (val initial = parser.parse(input)) {

            is Failure -> {
                return Success(
                    Parser.Match(
                        emptyList(),
                        input
                    )
                )
            }

            is Success -> {

                val values = mutableListOf<O>().apply {
                    add(initial.match.value)
                }

                var next = parser.parse(initial.match.context)

                var nextContext = initial.match.context

                while (next is Success) {
                    values.add(next.match.value)
                    nextContext = next.match.context
                    next = parser.parse(nextContext)
                }

                Success(
                    Parser.Match(
                        values,
                        nextContext
                    )
                )

            }
        }

    }

}