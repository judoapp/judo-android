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

internal abstract class AbstractParser<U, out O> : Parser<U, O> {

    override fun parse(input: ParserContext<U>): Parser.Result<U, O> {

        if (input.text.isEmpty())
            return Parser.Result.Failure(
                Parser.Error(
                    context = input,
                    message = "Input was empty"
                )
            )

        if (input.position > input.text.lastIndex)
            return Parser.Result.Failure(
                Parser.Error(
                    context = input,
                    message = "Reached the End of the input"
                )
            )

        return parseNonEmptyInput(input)

    }

    protected abstract fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, O>

}