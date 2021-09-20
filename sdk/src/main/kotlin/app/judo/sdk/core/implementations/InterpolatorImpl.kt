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

package app.judo.sdk.core.implementations

import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.emptyDataContext
import app.judo.sdk.core.data.fromKeyPath
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.Environment.RegexPatterns
import app.judo.sdk.core.interpolation.DateHelper
import app.judo.sdk.core.interpolation.LowercaseHelper
import app.judo.sdk.core.interpolation.ReplaceHelper
import app.judo.sdk.core.interpolation.SpaceReplacerHelper
import app.judo.sdk.core.interpolation.UppercaseHelper
import app.judo.sdk.core.lang.HelperName.*
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.lang.Token
import app.judo.sdk.core.lang.Tokenizer
import app.judo.sdk.core.log.Logger
import org.json.JSONObject

internal class InterpolatorImpl(
    private val tokenizer: Tokenizer = Environment.current.tokenizer,
    private val loggerSupplier: () -> Logger? = { null },
    private val dataContext: DataContext = emptyDataContext()
) : Interpolator {

    companion object {
        private const val TAG = "InterpolatorImpl"
    }

    override fun interpolate(theTextToInterpolate: String): String? =
        interpolate(theTextToInterpolate, dataContext)

    override fun interpolate(theTextToInterpolate: String, dataContext: DataContext): String? {

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

            when (theToken) {

                is Token.HandleBarExpression -> {
                    val keyPath = listOf(theToken.keyword.value) + theToken.keys
                    val data = dataContext.fromKeyPath(keyPath) ?: return null
                    if (data == "null") return null
                    if (data == JSONObject.NULL) return null
                    val result = executeHelper(theToken, data, logger)
                    theToken.copy(value = result)
                }

                is Token.RegularText -> {
                    theToken
                }
            }
        }.joinToString(separator = "") { it.value }
    }

    private fun executeHelper(
        theToken: Token.HandleBarExpression,
        data: Any?,
        logger: Logger?
    ): String {

        if (data == null) return ""

        if (theToken.helperName == null) return "$data"

        val arguments = theToken.helperArguments

        val helper: Interpolator.Helper = when (theToken.helperName) {
            DATE -> {
                SpaceReplacerHelper(
                    DateHelper(logger)
                )
            }
            LOWERCASE -> {
                LowercaseHelper()
            }
            UPPERCASE -> {
                UppercaseHelper()
            }
            REPLACE -> {
                ReplaceHelper()
            }
        }

        return helper(data, arguments)

    }

}