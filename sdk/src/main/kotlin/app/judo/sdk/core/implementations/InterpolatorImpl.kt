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

import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.emptyDataContext
import app.judo.sdk.core.data.fromKeyPath
import app.judo.sdk.core.interpolation.ProtoInterpolator
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.lang.Keyword
import app.judo.sdk.core.log.Logger
import java.lang.NumberFormatException
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*
import kotlin.Exception

/**
 * This class handles the interpolation of the Judo expression language.
 * An example of the expected input text is the following expression: `{{ uppercase (suffix (dropFirst "mr. jack reacher" 4) 7) }}`
 * The passed [String] is deserialized and the operations happen outwards from the most nested point.
 */
internal class InterpolatorImpl(
    private val loggerSupplier: () -> Logger? = { null },
    private var dataContext: DataContext = emptyDataContext()
) : Interpolator, ProtoInterpolator {

    companion object {
        internal const val TAG = "Interpolator"

        internal val dateTimeFormatterTypes = listOf(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE,
            DateTimeFormatter.ISO_OFFSET_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
    }

    var logger: Logger? = null

    override fun invoke(theTextToInterpolate: String, dataContext: DataContext): String? =
        interpolate(theTextToInterpolate, dataContext)

    override fun interpolate(theTextToInterpolate: String): String? =
        interpolate(theTextToInterpolate, dataContext)

    /**
     * Interpolates a [String] containing expressions in the Judo expression language.
     * These are created in our Mac application for UI design.
     *
     * Equivalent to the iOS String+interpolation.evaluateExpressions func.
     *
     * @param theTextToInterpolate the [String] to interpolate, e.g. `{{ uppercase (suffix (dropFirst "mr. jack reacher" 4) 7) }}`
     * @param dataContext the [DataContext] relevant to the specified text. It holds information that can be accessed by the expression.
     */
    override fun interpolate(theTextToInterpolate: String, dataContext: DataContext): String? {
        this@InterpolatorImpl.dataContext = dataContext
        this.logger = loggerSupplier()

        // Matches anything with opening and closing brackets, e.g:
        // {{data.int}} {{data.negativeInt}} {{data.double}} {{data.negativeDouble}}
        // Android Studio thinks \\}\\} is redundant, but devices disagree with it.
        @Suppress("RegExpRedundantEscape")
        val initialRegex = "\\{\\{(.*?)\\}\\}".toRegex(option = RegexOption.DOT_MATCHES_ALL)
        var processedExpression = theTextToInterpolate

        try {
            initialRegex.findAll(theTextToInterpolate)
                .also {
                    // Done so expressions without brackets also work.
                    if (it.count() == 0) {
                        evaluateFullExpression(theTextToInterpolate)
                    }
                }
                .forEach {
                    // Ranges are used to replace parts of the expression after processing.
                    // E.g. the `uppercase \"word\"` text simply becomes WORD after processing & replacing.
                    val outerRange = it.getOuterRangeOnParent(processedExpression)
                    val innerRange = it.getInnerRangeOnParent(theTextToInterpolate)
                    val innerRangeExpression = theTextToInterpolate.substring(innerRange)

                    // Checking whether or not there's a nested expression that might need un-nesting and processing itself.
                    // If not, this just returns the expression we sent in.
                    val resultAfterUndoingNesting = getNestedExpression(innerRangeExpression)

                    // The root part might also be an expression, so we process it one last time.
                    val finalEvaluationResult = evaluateFullExpression(resultAfterUndoingNesting)

                    processedExpression = processedExpression.replaceRange(outerRange, finalEvaluationResult)
                }

            return processedExpression
        } catch (exception: Exception) {
            if (exception is StringExpressionException || exception is NumberFormatException) {
                // Log an error if we catch one of the internally handled exceptions to return null.
                // This is done so instead of crashing, invalid interpolation just makes elements disappear.
                logger?.e(
                    tag = TAG,
                    message = exception.message,
                    error = exception
                )
                return null
            }

            throw exception
        }
    }

    /**
     * Function for de-nesting nested expressions. It's recursive, each step going closer to
     * the root of the nesting, depending on how many nested expressions we have.
     *
     * Example of a nested expression is `{{ uppercase (suffix (dropFirst "mr. jack reacher" 4) 7) }}`
     * We want to de-nest and evaluate each command at a time.
     *
     * @param expression the expression to de-nest. It can also not have any nested expressions,
     * which means we just return it as is.
     */
    private fun getNestedExpression(expression: String): String {
        // Regex for taking just what's inside each nesting step, represented by the round brackets.
        val nestedRegex = "\\((.*)\\)".toRegex()
        var currentExpression = expression

        nestedRegex.findAll(currentExpression).forEach {
            val outerRange = it.getOuterRangeOnParent(currentExpression)
            val innerRange = it.getInnerRangeOnParent(currentExpression)

            // We take the inner range (without the outer brackets) and re-call this method.
            // This is done so we get ever closer to the root of the nesting.
            // When returning, each inner step is done before processing its parent.
            currentExpression = currentExpression.substring(innerRange)
            val result = getNestedExpression(currentExpression)

            currentExpression = expression.replaceRange(outerRange, result)
        }

        // Evaluate the current nested expression step, then wrap the result in quotations
        // so it stays as a single value.
        return "\"${evaluateFullExpression(currentExpression)}\""
    }

    /**
     * Evaluates the expression passed in. This can be the full expression, or one of the steps
     * from a nested expression.
     *
     * @param expression the expression to process. This can either be a full expression or one of
     * the steps required in a multi-layer nested expression.
     * @throws [StringExpressionException.InvalidArgument] when expression parsing fails.
     */
    private fun evaluateFullExpression(expression: String): String {
        // This gets each command within the expression.
        // E.g. `suffix (dropFirst "mr. jack reacher" 4) 7` matches `suffix`,
        // then `(dropFirst`, then `\"mr. jack reacher\"`, etc.
        val expressionDividedRegex = "(^\".*?\"\$)|(\".*?\")|([^\\s]+)".toRegex(option = RegexOption.DOT_MATCHES_ALL)

        val expressionParts = expressionDividedRegex.findAll(expression).map { it.value }.toList()

        if (expressionParts.isEmpty()) {
            throw StringExpressionException.InvalidArgument(
                message = "Expression parsing failed; it might not be correctly formatted",
                argument = expression
            )
        }

        return evaluateExpressionCommand(expressionParts)
    }

    /**
     * Evaluates and processes a single specific command.
     * E.g. `uppercase \"lowercase string we want to uppercase]\"`
     *
     * @param arguments the command and related arguments. These can be, for example, the number
     * of characters to drop from a `dropFirst` command.
     */
    private fun evaluateExpressionCommand(arguments: List<String>): String {
        val result: String = when (arguments[0]) {
            "lowercase" -> twoArgumentHelper(arguments) { it.toLowerCase(Locale.getDefault()) }
            "uppercase" -> twoArgumentHelper(arguments) { it.toUpperCase(Locale.getDefault()) }
            "dropFirst" -> threeArgumentHelper(arguments) { value, places -> value.drop(places) }
            "dropLast" -> threeArgumentHelper(arguments) { value, places -> value.dropLast(places) }
            "prefix" -> threeArgumentHelper(arguments) { value, places -> value.take(places) }
            "suffix" -> threeArgumentHelper(arguments) { value, places -> value.takeLast(places) }
            "dateFormat", "date" -> dateFormatHelper(arguments)
            "numberFormat" -> numberFormatHelper(arguments)
            "replace" -> replaceHelper(arguments)
            // It's possible the command is a simple string or context key path.
            else -> getStringValue(arguments[0])
        }

        return result
    }

    private fun twoArgumentHelper(arguments: List<String>, processing: (String) -> String): String {
        ensureArgumentNumberEqual(2, arguments.count(), "twoArgumentHelper")

        val value = getStringValue(arguments[1])
        return processing(value)
    }

    private fun threeArgumentHelper(arguments: List<String>, processing: (String, Int) -> String): String {
        ensureArgumentNumberEqual(3, arguments.count(), "threeArgumentHelper")

        val value = getStringValue(arguments[1])
        val places = arguments[2].toIntOrNull() ?: throw StringExpressionException.ExpectedInteger(where = "threeArgumentHelper")

        return processing(value, places)
    }

    private fun dateFormatHelper(arguments: List<String>): String {
        ensureArgumentNumberEqual(3, arguments.count(), "formatDateHelper")

        // The formatting must be contained within quotation marks.
        // However these additional quotation marks must be removed before we can set
        // the dateFormat on the dateFormatter. If the format is not contained within
        // quotation marks then it is considered to be invalid.
        if (!arguments[2].isEncasedInQuotes()) {
            throw StringExpressionException.InvalidDate(argument = arguments[2])
        }

        // Dates are usually inputted formatted like "2022-02-01 19:46:31+0000".
        // We want them to be in the ISO_OFFSET_DATE_TIME format, which is "2022-02-01T19:46:31+00:00".
        var dateString = getStringValue(arguments[1])
            .replace("\\.\\d+".toRegex(), "")
            .replace(" ", "T")

        // Here we add the : to dates that specify the timezone offset, but do 4 straight digits.
        // I.e. +0000 becomes +00:00 so the LocalDateTime parser is happy.
        // If the inputted date doesn't have an offset, we do nothing as we also support those formats later on.
        if ((dateString.lastIndex - dateString.indexOf("+")) == 4) {
            dateString = StringBuilder(dateString).insert(dateString.lastIndex - 1, ":").toString()
        }

        val dateTime = tryParsingDateOrDateTime(dateString)

        try {
            // Formats passed in can be, among other examples, yyyy-MM-dd, HH:mm:ss, and EEEE, MMM d, yyyy.
            // iOS can handle formats with `aa`, but the Java DateTimeFormat expects a single `a` and throws if double are passed in.
            val desiredFormatString = getStringValue(arguments[2]).replace("aa", "a")

            return DateTimeFormatter
                .ofPattern(getStringValue(desiredFormatString))
                .withZone(ZoneId.systemDefault())
                .format(dateTime)
        } catch (e: Throwable) {
            logger?.e(
                tag = TAG,
                message = "Failed to format the given date: $dateString, with the given format: ${arguments[2]}. Returning unformatted $dateString.",
                error = e
            )
            return dateString
        }
    }

    /**
     * We attempt to parse the date or dateTime with a variety of DateTimeFormatter options.
     * This is to make it as likely as possible that we get a match, as there are a number of
     * different possible input formats for dates and handling them manually wouldn't be feasible.
     *
     * This [TemporalAccessor] is used to format the date in whichever format the user desires.
     */
    private fun tryParsingDateOrDateTime(input: String): TemporalAccessor {
        var result: TemporalAccessor? = null

        for (type in dateTimeFormatterTypes) {
            try {
                result = LocalDateTime.parse(input, type)
                if (result != null) break
            } catch (t: Throwable) {
                logger?.d(
                    tag = TAG,
                    data = "parseDateOrDateTime couldn't parse $input with $type. Skipping."
                )
                continue
            }
        }

        return result ?: throw StringExpressionException.InvalidDate(argument = input)
    }

    private fun numberFormatHelper(arguments: List<String>): String {
        ensureArgumentNumberInRange(2..3, arguments.count())

        val numberValue = getNumberValue(arguments[1])

        // Calling getStringValue to remove superfluous quotes if needed.
        var numberStyle = arguments.getOrElse(2) { "decimal" }

        if (numberStyle.isEncasedInQuotes()) {
            numberStyle = numberStyle.removeQuotationMarks()
        }

        return when (numberStyle) {
            "none" -> formatInteger(numberValue)
            "decimal" -> formatDouble(numberValue)
            "currency" -> formatCurrency(numberValue)
            "percent" -> formatPercent(numberValue)
            // Just use the default if gibberish is passed in.
            else -> formatDouble(numberValue)
        }
    }

    private fun replaceHelper(arguments: List<String>): String {
        ensureArgumentNumberEqual(4, arguments.count(), "replaceHelper")

        val stringToReplaceOn = getStringValue(arguments[1])

        if (!arguments[2].isEncasedInQuotes() || !arguments[3].isEncasedInQuotes()) {
            throw StringExpressionException.InvalidReplaceArguments(argument1 = arguments[2], argument2 = arguments[3])
        }

        val current = getStringValue(arguments[2])
        val replacement = getStringValue(arguments[3])

        return stringToReplaceOn.replace(current, replacement)
    }

    /**
     * Reformats the given value, either through simply removing quotation marks of arguments,
     * or getting dynamic information from the [DataContext] related to the expression (`user.name`
     * being replaced by the name of the specific user, for example).
     *
     * See [DataContext] for more information.
     *
     * @throws [StringExpressionException.UnexpectedValue] if the key path is invalid, or if it
     * contains an invalid value (a value other than [String], [Int], [Double], and [Boolean]).
     */
    private fun getStringValue(keyPathOrStringLiteral: String): String {
        if (keyPathOrStringLiteral.isEncasedInQuotes()) {
            return keyPathOrStringLiteral.removeQuotationMarks()
        }

        // Key paths for the dataContext are formatted with a dividing dot.
        val keyPath: List<String> = keyPathOrStringLiteral.split(".")

        // The first element, if it exists, needs to be one of the specific DataContext keywords
        // for us to process it (i.e. one of "user", "url" or "data".
        if (keyPath.isNotEmpty() && Keyword.values().any { it.value == keyPath[0] }) {
            return when (val result: Any? = dataContext.fromKeyPath(keyPath)) {
                is String -> result.toString()
                // When not part of a numberFormat expression, we want to default to integers with 0 decimals.
                is Int -> formatInteger(result)
                is Double -> formatInteger(result)
                is Boolean -> if (result) "true" else "false"
                else -> throw StringExpressionException.UnexpectedValue(value = keyPathOrStringLiteral)
            }
        } else {
            // It's a string literal.
            return keyPathOrStringLiteral
        }
    }

    /**
     * @throws [StringExpressionException.UnexpectedValue] if the key path is invalid, or if it
     * contains an invalid value (a value other than [String], [Int], and [Double]).
     * @throws [StringExpressionException.InvalidArgument] if [keyPathOrStringLiteral] is not a
     * valid key path, and isn't a simple literal that needs quotation mark removal.
     */
    private fun getNumberValue(keyPathOrStringLiteral: String): Number {
        if (keyPathOrStringLiteral.isEncasedInQuotes()) {
            return keyPathOrStringLiteral.removeQuotationMarks().toDouble()
        }

        val keyPath: List<String> = keyPathOrStringLiteral.split(".")

        if (keyPath.isNotEmpty() && Keyword.values().any { it.value == keyPath[0] }) {
            return when (val result: Any? = dataContext.fromKeyPath(keyPath)) {
                is String -> result.toDouble()
                is Int -> result.toInt()
                is Double -> result.toDouble()
                else -> throw StringExpressionException.UnexpectedValue(value = keyPathOrStringLiteral)
            }
        } else {
            throw StringExpressionException.InvalidArgument(
                message = "Invalid Argument. KeyPath or literal is invalid for getNumberValue method",
                argument = keyPathOrStringLiteral
            )
        }
    }

    private fun formatDouble(numberValue: Number): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.roundingMode = RoundingMode.HALF_DOWN
        formatter.maximumFractionDigits = 3
        return formatter.format(numberValue).toString()
    }

    private fun formatInteger(numberValue: Number): String {
        val formatter = NumberFormat.getIntegerInstance(Locale.getDefault())
        formatter.roundingMode = RoundingMode.HALF_DOWN
        return formatter.format(numberValue).toString()
    }

    private fun formatCurrency(numberValue: Number): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        formatter.maximumFractionDigits = 2
        return formatter.format(numberValue).toString()
    }

    private fun formatPercent(numberValue: Number): String {
        val formatter = NumberFormat.getPercentInstance(Locale.getDefault())
        formatter.roundingMode = RoundingMode.HALF_DOWN
        formatter.maximumFractionDigits = 0
        return formatter.format(numberValue)
    }

    /**
     * Gets the inner range of the expression, i.e. the content inside the brackets.
     * E.g. uppercase (suffix (dropFirst "mr. jack reacher" 4) 7)
     */
    private fun MatchResult.getInnerRangeOnParent(parentString: String): IntRange {
        val innerRangeStart = parentString.indexOf(this.groupValues[1])
        val innerRangeEnd = innerRangeStart + this.groupValues[1].length - 1
        return IntRange(innerRangeStart, innerRangeEnd)
    }

    /**
     * The outer range of the match, i.e. including the brackets around it.
     * This is used so we can replace the matched part of the expression with the result of processing.
     * E.g. {{ uppercase (suffix (dropFirst "mr. jack reacher" 4) 7) }}
     */
    private fun MatchResult.getOuterRangeOnParent(parentString: String): IntRange {
        val outerRangeStart = parentString.indexOf(this.groupValues[0])
        val outerRangeEnd = outerRangeStart + this.groupValues[0].length - 1
        return IntRange(outerRangeStart, outerRangeEnd)
    }

    private fun String.isEncasedInQuotes(): Boolean {
        return this.startsWith("\"") && this.endsWith("\"")
    }

    private fun String.removeQuotationMarks(): String {
        return this.drop(1).dropLast(1)
    }

    private fun ensureArgumentNumberEqual(expected: Number, actual: Int, helperName: String) {
        if (actual != expected) {
            throw StringExpressionException.InvalidArgumentNumber(
                where = helperName,
                expected = expected.toString(),
                actual = actual.toString()
            )
        }
    }

    private fun ensureArgumentNumberInRange(range: IntRange, actual: Int) {
        if (actual !in range) {
            throw StringExpressionException.InvalidArgumentNumber(
                where = "formatNumberHelper",
                expected = range.toString(),
                actual = actual.toString()
            )
        }
    }
}

internal sealed class StringExpressionException(
    exceptionMessage: String
) : Exception(exceptionMessage) {
    class InvalidArgumentNumber(message: String = "Invalid arguments", where: String, expected: String, actual: String) :
        StringExpressionException("$message in $where: expected $expected but found $actual.")

    class InvalidArgument(message: String = "Invalid argument", argument: String, where: String = "") :
        StringExpressionException("$message in $where: $argument")

    class UnexpectedValue(message: String = "Unexpected value", value: String) :
        StringExpressionException("$message: $value")

    class ExpectedInteger(message: String = "Expected an integer", where: String = "") :
        StringExpressionException("$message ${if (where.isNotBlank()) "in $where" else where}")

    class InvalidReplaceArguments(message: String = "Invalid replace arguments", argument1: String, argument2: String) :
        StringExpressionException("$message: $argument1 and/or $argument2. They might not be inside quotations as expected.")

    class InvalidDate(message: String = "Invalid date or format passed in", argument: String) :
        StringExpressionException("$message: $argument")
}
