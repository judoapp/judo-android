package app.judo.sdk.core.interpolation

import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.log.Logger
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

internal class DateHelper(
    private val logger: Logger? = null
) : Interpolator.Helper {

    companion object {
        private const val TAG = "DateHelper"
    }

    private val dateTimeFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ISO_DATE_TIME
    }

    private val dateFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ISO_DATE
    }

    private val timeFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ISO_TIME
    }

    override fun invoke(data: Any, arguments: List<String>?): String {

        val input = "$data"

        val argument = arguments?.firstOrNull()

        return try {

            val date = getDateTime(input) ?: getDate(input) ?: getTime(input)

            DateTimeFormatter
                .ofPattern(argument)
                .withZone(ZoneId.systemDefault())
                .format(date)

        } catch (error: Throwable) {
            logger?.e(
                tag =
                TAG,
                message = "Failed to format the given date: $input, with the given format: $argument",
                error
            )

            input
        }
    }

    private fun getDateTime(input: String): TemporalAccessor? {
        return try {
            dateTimeFormatter.parseBest(
                input,
                ZonedDateTime::from,
                LocalDateTime::from,
            )
        } catch (e: Throwable) {
            null
        }
    }

    private fun getDate(input: String): TemporalAccessor? {
        return try {
            dateFormatter.parseBest(
                input,
                LocalDate::from,
                LocalDate::from,
            )
        } catch (e: Throwable) {
            null
        }
    }

    private fun getTime(input: String): TemporalAccessor? {
        return try {
            timeFormatter.parseBest(
                input,
                LocalTime::from,
                LocalTime::from,
            )
        } catch (e: Throwable) {
            null
        }
    }

}