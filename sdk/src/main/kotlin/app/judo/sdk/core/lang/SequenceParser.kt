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

internal class SequenceParser<U, out O>(
    private vararg val parsers: Parser<U, O>,
) : AbstractParser<U, List<O>>() {

    constructor(
        parsers: List<Parser<U, O>>
    ) : this(
        *parsers.toTypedArray()
    )

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, List<O>> {

        val seed: Parser<U, List<O>> = ReturnParser(emptyList())

        return parsers.fold(seed) { acc, parser ->
            BindMapParser(acc, parser) { list, value ->
                list + listOf(value)
            }
        }.parse(input)
    }

}
