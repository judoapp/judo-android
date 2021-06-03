package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class PredicateParser<U>(
    private val predicate: (Char) -> Boolean,
) : AbstractParser<U, Char>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, Char> {

        val (text, _, position, line) = input

        val char = text[position]

        val matches = predicate(char)

        if (matches) {

            val nextLine = line.inc().takeIf { char == '\n' } ?: line

            val newContext = input.copy(
                position = position.inc(),
                line = nextLine
            )

            return Success(
                Parser.Match(
                    value = char,
                    context = newContext
                )
            )
        }

        return Failure(
            Parser.Error(
                context = input,
                message = "Character $char did not match the predicate"
            )
        )

    }

}