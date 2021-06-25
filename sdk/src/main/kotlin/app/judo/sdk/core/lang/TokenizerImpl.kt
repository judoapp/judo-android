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

import java.util.regex.Pattern

internal class TokenizerImpl : Tokenizer {

    private val regularTextTextParser: Parser<Unit, Token.RegularText> = RegularTextParser()

    private val handleBarParser: Parser<Unit, Token.HandleBarExpression> = HandleBarParser()

    private val parser: Parser<Unit, List<Token>> = ManyParser(
        OrParser(
            parserA = handleBarParser,
            parserB = regularTextTextParser
        )
    )

    private val cachedMappings = mutableMapOf<String, List<Token>>()

    override fun tokenize(text: String): List<Token> {

        cachedMappings[text]?.let {
            return it
        }

        val openPattern = Regex("\\{\\{")

        val closingPattern = Regex(Pattern.quote("}}"))

        val openingCount = openPattern.findAll(input = text).count()

        val closingCount = closingPattern.findAll(input = text).count()

        val textIsImbalanced = openingCount != closingCount

        if (textIsImbalanced) {
            throw IllegalStateException(
                """ALL HANDLEBARS MUST BE OPENED AND CLOSED CORRECTLY
                    |The amount of {{ must be equal to the amount of }}
                    |{{ = $openingCount & }} = $closingCount
                """.trimMargin()
            )
        }

        val input = ParserContext(
            text = text,
            Unit,
        )

        return when (val result = parser.parse(input = input)) {

            is Parser.Result.Failure -> {
                emptyList()
            }

            is Parser.Result.Success -> {
                cachedMappings[text] = result.match.value
                result.match.value
            }

        }
    }

}