package app.judo.sdk.core.lang

internal fun interface Parser<U, out O> {

    data class Error<U>(
        val context: ParserContext<U>,
        val message: String
    )

    data class Match<U, out O>(
        val value: O,
        val context: ParserContext<U>,
    )

    sealed class Result<U, out O> {

        data class Success<U, out O>(
            val match: Parser.Match<U, O>
        ) : Result<U, O>()

        data class Failure<U, out O>(
            val error: Parser.Error<U>,
        ) : Result<U, O>()

    }

    fun parse(input: ParserContext<U>): Result<U, O>

}