package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class MapStateParser<U, A, B>(
    private val parser: Parser<U, A>,
    private val transform: (oldState: U, oldValue: A) -> Pair<U, B>,
) : AbstractParser<U, B>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, B> {

        return when (
            val result = parser.parse(input)
        ) {

            is Failure -> {
                Failure(
                    result.error,
                )
            }

            is Success -> {
                val (value, context) = result.match

                val (newState, newValue) = transform(context.state, value)

                Success(
                    Parser.Match(
                        newValue,
                        context.copy(
                            state = newState
                        )
                    )
                )
            }

        }

    }

}