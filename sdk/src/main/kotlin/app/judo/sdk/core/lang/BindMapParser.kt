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

/**
 * Takes a Parser of A and maps its value into a
 * Parser of B via a parser factory.
 *
 * Then returns the result of the product parser.
 *
 * Commonly used to concatenate the results of two parsers in a dynamic way
 */
internal class BindMapParser<U, A, B, C>(
    private val parserA: Parser<U, A>,
    private val parserFactory: (value: A) -> Parser<U, B>,
    private val transform: (A, B) -> C
) : AbstractParser<U, C>() {

    constructor(
        parserA: Parser<U, A>,
        parserB: Parser<U, B>,
        transform: (A, B) -> C
    ) : this(parserA, { parserB }, transform)

    override fun parseNonEmptyInput(input: ParserContext<U>): Parser.Result<U, C> {

        return BindParser(parserA) { a ->
            MapParser(parserFactory(a)) { b ->
                transform(a, b)
            }
        }.parse(input = input)

    }

}