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

internal class HandleBarParser<U> : AbstractParser<U, Token.HandleBarExpression>() {

    private val twoLeftBrackets: Parser<Token.HandleBarExpression, String> =
        StringParser("{{")

    private val maybeWhitespace: Parser<Token.HandleBarExpression, String> =
        WhitespaceParser()

    // Puts the function name and argument into the state then maps the value back to a String
    private val maybeAFunctionName: Parser<Token.HandleBarExpression, String> =
        MapParser(MaybeParser(MapStateParser(FunctionName.values().toList()
            .map<FunctionName, Parser<Token.HandleBarExpression, Pair<String, FunctionName>>> { functionName ->
                MapParser(StringParser(functionName.value)) { value ->
                    value to functionName
                }
            }.reduce { acc, parser ->
                OrParser(acc, parser)
            }) { oldState, (value, functionName) ->

            oldState.copy(
                functionName = functionName
            ) to value

        })
        ) { functionName ->
            functionName ?: ""
        }

    // Puts the function name and argument into the state then maps the value back to a String
    private val aKeyword: Parser<Token.HandleBarExpression, String> =
        MapStateParser(Keyword.values().toList()
            .map<Keyword, Parser<Token.HandleBarExpression, Pair<String, Keyword>>> { functionName ->
                MapParser(StringParser(functionName.value)) { value ->
                    value to functionName
                }
            }.reduce { acc, parser ->
                OrParser(acc, parser)
            }) { oldState, (value, functionName) ->

            oldState.copy(
                keyword = functionName
            ) to value

        }

    private val dotParser = CharParser<Token.HandleBarExpression>('.')

    private val maybeADot: Parser<Token.HandleBarExpression, String> =
        MapParser(MaybeParser(dotParser)) {
            "${it ?: ""}"
        }

    private val identifierParser = IdentifierParser<Token.HandleBarExpression>(
        '.', ' ', '}',
    )

    private val maybeADotSeparatedListOfKeys = MapStateParser(
        SeparatorParser(
            parser = identifierParser,
            separatorParser = dotParser
        )
    ) { state, keys ->
        state.copy(
            keys = keys
        ) to keys.joinToString(".")
    }

    // TODO: 2021-05-23 Replace with StringLiteralParser
    private val quoteParser = CharParser<Token.HandleBarExpression>('"')

    private val anyCharParser = IdentifierParser<Token.HandleBarExpression>(
        listOf('"')
    )

    private val maybeAFunctionArgument: Parser<Token.HandleBarExpression, String> = MapParser(
        MaybeParser(
            MapStateParser(
                BetweenParser(
                    quoteParser,
                    anyCharParser,
                    quoteParser
                )
            ) { oldState, argument ->

                val newState = oldState.copy(
                    functionArgument = argument
                )

                val newValue = "\"$argument\""

                newState to newValue
            }
        )
    ) { argument ->
        argument ?: ""
    }

    private val twoRightBrackets = StringParser<Token.HandleBarExpression>("}}")

    private val expressionSequence: Parser<Token.HandleBarExpression, List<String>> =
        SequenceParser(
            twoLeftBrackets,
            maybeWhitespace,
            maybeAFunctionName,
            maybeWhitespace,
            aKeyword,
            maybeADot,
            maybeADotSeparatedListOfKeys,
            maybeWhitespace,
            maybeAFunctionArgument,
            maybeWhitespace,
            twoRightBrackets
        )

    private val expressionStringParser = MapParser(expressionSequence) {
        it.joinToString("")
    }

    override fun parseNonEmptyInput(input: ParserContext<U>): Result<U, Token.HandleBarExpression> {

        val innerContext = with(input) {
            ParserContext(
                text = text,
                state = Token.HandleBarExpression("", position = position),
                position = position,
                line = line
            )
        }

        return when (val result = expressionStringParser.parse(innerContext)) {

            is Failure -> {
                Failure(
                    Parser.Error(
                        context = input,
                        message = result.error.message
                    )
                )
            }

            is Success -> {

                val context = result.match.context
                val state = context.state

                Success(
                    Parser.Match(
                        value = state.copy(value = result.match.value, position = input.position),
                        input.copy(
                            position = context.position,
                            line = context.line
                        )
                    )
                )
            }

        }
    }

}