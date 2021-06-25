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

internal class LongParser<U> : AbstractParser<U, Long>() {

    private val numberParser = PredicateParser<U> { theCharacter ->
        theCharacter in '0'..'9'
    }

    private val maybeAMinusParser = MaybeParser(PredicateParser<U> { theCharacter ->
        theCharacter == '-'
    })

    private val oneOrMoreNumbersParser = MapParser(Many1Parser(numberParser)) {
        it.joinToString("")
    }

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, Long> {
        return try {

            MapParser(AndParser(maybeAMinusParser, oneOrMoreNumbersParser)) { (minus, strings) ->
                "${minus?.toString() ?: ""}$strings".toLong()
            }.parse(input)

        } catch (e: NumberFormatException) {
            Parser.Result.Failure(Parser.Error(input, e.toString()))
        }
    }

}