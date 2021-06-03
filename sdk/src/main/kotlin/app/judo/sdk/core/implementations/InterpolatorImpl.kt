package app.judo.sdk.core.implementations

import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.core.data.JsonDAO
import app.judo.sdk.core.environment.Environment.RegexPatterns
import app.judo.sdk.core.lang.FunctionName.DATE
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.lang.Keyword.DATA
import app.judo.sdk.core.lang.Keyword.USER
import app.judo.sdk.core.lang.Token
import app.judo.sdk.core.lang.Tokenizer
import app.judo.sdk.core.lang.TokenizerImpl
import app.judo.sdk.core.log.Logger

internal class InterpolatorImpl(
    override var jsonDAO: JsonDAO? = null,
    override val userDataSupplier: UserDataSupplier = UserDataSupplier { emptyMap() },
    private val tokenizer: Tokenizer = TokenizerImpl(),
    private val loggerSupplier: () -> Logger? = { null },
) : Interpolator {

    companion object {
        private const val TAG = "InterpolatorImpl"
    }

    override fun interpolate(theTextToInterpolate: String): String? {

        val logger = loggerSupplier()

        val expressionPattern = Regex(RegexPatterns.HANDLE_BAR_EXPRESSION_PATTERN)

        val theTextDoesNotContainHandleBarExpressions =
            !theTextToInterpolate.contains(expressionPattern)

        if (theTextDoesNotContainHandleBarExpressions) {
            return theTextToInterpolate
        }

        val tokens = tokenizer.tokenize(theTextToInterpolate)

        logger?.v(TAG, "Mapping: $tokens")

        return tokens.map { theToken ->

            val userData = userDataSupplier.supplyUserData()

            val dao = jsonDAO

            when (theToken) {

                is Token.HandleBarExpression -> {

                    when (theToken.keyword) {

                        USER -> {

                            val key = theToken.keys.lastOrNull() ?: return null

                            val data = userData[key] ?: return null

                            val newData = executeFunction(theToken, data, logger)

                            theToken.copy(
                                value = newData
                            )

                        }

                        DATA -> {

                            val data =
                                dao?.findValueByKeys(theToken.keys) ?: return null

                            val newData = executeFunction(theToken, data, logger)
                            theToken.copy(
                                value = newData
                            )

                        }

                    }

                }

                is Token.RegularText -> {
                    theToken
                }
            }
        }.joinToString(separator = "") { it.value }
    }

    private fun executeFunction(
        theToken: Token.HandleBarExpression,
        data: String,
        logger: Logger?
    ): String {
        return when (theToken.functionName) {
            DATE -> {
                // TODO: 2021-05-26 - Finish This Feature https://github.com/judoapp/judo-android-develop/issues/233
//                try {
//
//                    val formatter =
//                        SimpleDateFormat(
//                            theToken.functionArgument,
//                            Locale.getDefault()
//                        )
//
//                    val splits = data.split("T")
//
//                    val dateString = splits[0]
//                    val timeString = splits[1]
//
//                    val date = Date.parse(data)
//
//                    val formattedDateAndTime = formatter.format(data)
//
//
////                    val dateFormatter = SimpleDateFormat.getDateInstance()
////                    val timeFormatter = SimpleDateFormat.getTimeInstance()
//
//                    dateString
//
//                } catch (error: Throwable) {
//                    logger?.e(
//                        tag = TAG,
//                        message = "Failed to format the given date: $data, with the given format: ${theToken.functionArgument}",
//                        error
//                    )
//                    data
//                }
                data

            }

            null -> {
                data
            }
        }

    }

}