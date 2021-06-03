package app.judo.sdk.core.lang

import app.judo.sdk.core.lang.Parser.Result
import app.judo.sdk.core.lang.Parser.Result.Failure
import app.judo.sdk.core.lang.Parser.Result.Success

internal class HandleBarParser<U> : AbstractParser<U, Token.HandleBarExpression>() {

    private data class TokenState(
        val keyword: Keyword = Keyword.values().first(),
        val functionArgument: String? = null,
        val functionName: FunctionName? = null,
        val keys: List<String> = emptyList()
    )

    private val twoLeftBrackets: Parser<TokenState, String> =
        StringParser("{{")

    private val maybeWhitespace: Parser<TokenState, String> =
        WhitespaceParser()

    // Puts the function name and argument into the state then maps the value back to a String
    private val maybeAFunctionName: Parser<TokenState, String> =
        MapParser(MaybeParser(MapStateParser(FunctionName.values().toList()
            .map<FunctionName, Parser<TokenState, Pair<String, FunctionName>>> { functionName ->
                MapParser(StringParser(functionName.value)) { value ->
                    value to functionName
                }
            }.reduce { acc, parser ->
                OrParser(acc, parser)
            }) { oldState, (value, functionName) ->

            oldState.copy(
                functionArgument = value,
                functionName = functionName
            ) to value

        })
        ) { functionName ->
            functionName ?: ""
        }

    // Puts the function name and argument into the state then maps the value back to a String
    private val aKeyword: Parser<TokenState, String> =
        MapStateParser(Keyword.values().toList()
            .map<Keyword, Parser<TokenState, Pair<String, Keyword>>> { functionName ->
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

    private val dotParser = CharParser<TokenState>('.')

    private val maybeADot: Parser<TokenState, String> =
        MapParser(MaybeParser(dotParser)) {
            "${it ?: ""}"
        }

    private val identifierParser = IdentifierParser<TokenState>(
        '.', ' ', '}',
    )

    private val maybeADotSeparatedListOfKeys = MapStateParser(
        Separator1Parser(
            parser = identifierParser,
            separatorParser = dotParser
        )
    ) { state, keys ->
        state.copy(
            keys = keys
        ) to keys.joinToString(".")
    }

    // TODO: 2021-05-23 Replace with StringLiteralParser
    private val quoteParser = CharParser<TokenState>('"')

    private val anyCharParser = IdentifierParser<TokenState>(
        listOf('"')
    )

    private val maybeAFunctionArgument: Parser<TokenState, String> = MapParser(
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

    private val twoRightBrackets = StringParser<TokenState>("}}")

    private val expressionSequence: Parser<TokenState, List<String>> = SequenceParser(
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
                state = TokenState(),
                position = position,
                line = line
            )
        }

        return when (val result = expressionStringParser.parse(innerContext)) {

            is Failure -> {

                val context: ParserContext<U> = with(result.error.context) {
                    ParserContext(
                        text = text,
                        state = input.state,
                        position = position,
                        line = line
                    )
                }

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

                val token = Token.HandleBarExpression(
                    value = result.match.value,
                    keys = state.keys,
                    keyword = state.keyword,
                    functionName = state.functionName,
                    functionArgument = state.functionArgument,
                    position = input.position
                )

                Success(
                    Parser.Match(
                        value = token,
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