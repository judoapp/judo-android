package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

/**
 * Applies modifications to the state object and then
 *
 * returns the the previous match but with the new state.
 */
internal class StateParser<U, A>(
    private val parser: Parser<U, A>,
    private val transform: (oldState: U) -> U,
) : AbstractParser<U, A>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, A> {

        return when (
            val result = parser.parse(input)
        ) {

            is Failure -> {
                Failure(
                    result.error,
                )
            }

            is Success -> {
                val context = result.match.context

                val newState = transform(context.state)

                Success(
                    result.match.copy(
                        context = context.copy(
                            state = newState
                        )
                    )
                )
            }

        }

    }

}