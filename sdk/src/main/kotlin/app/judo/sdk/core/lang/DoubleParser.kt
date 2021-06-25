/*
 * Copyright (c) 2020-present, Rover Labs, Inc. All rights reserved.
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Rover.
 *
 * This copyright notice shall be included in all copies or substantial portions of
 * the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package app.judo.sdk.core.lang

internal class DoubleParser<U> : AbstractParser<U, Double>() {

    private val maybeMinusParser = MaybeParser(CharParser<U>('-'))

    private val numberParser = PredicateParser<U> {
        it in '0'..'9'
    }

    private val dotParser = StringParser<U>(".")

    private val oneOrMoreNumbers = MapParser(Many1Parser(numberParser)) {
        it.joinToString("")
    }

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, Double> {
        return try {
            val doubleParser = SequenceParser(
                oneOrMoreNumbers,
                dotParser,
                oneOrMoreNumbers,
            )

            val maybeMinusAndDouble = AndParser(maybeMinusParser, doubleParser)

            MapParser(
                parser = maybeMinusAndDouble
            ) { (minus, strings) ->
                "${minus?.toString() ?: ""}${strings.joinToString("")}".toDouble()
            }.parse(input)
        } catch (e: NumberFormatException) {
            Parser.Result.Failure(Parser.Error(input, e.toString()))
        }
    }

}