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

import app.judo.sdk.core.lang.Parser.Result
import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class Many1Parser<U, out O>(
    private val parser: Parser<U, O>,
) : AbstractParser<U, List<O>>() {

    override fun parseNonEmptyInput(input: ParserContext<U>): Result<U, List<O>> {

        return when (val initial = parser.parse(input)) {

            is Failure -> {
                return Failure(
                    initial.error,
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