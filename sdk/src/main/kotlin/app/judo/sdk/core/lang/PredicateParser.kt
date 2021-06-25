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