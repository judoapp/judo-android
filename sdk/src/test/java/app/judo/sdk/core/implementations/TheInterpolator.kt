package app.judo.sdk.core.implementations

import app.judo.sdk.core.data.JsonDAO
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.lang.TokenizerImpl
import app.judo.sdk.utils.TestLoggerImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

internal class TheInterpolator {

    private val userData: MutableMap<String, String> = mutableMapOf()

    private lateinit var jsonDAO: JsonDAO

    private lateinit var interpolator: Interpolator

    @Before
    fun setUp() {

        jsonDAO = object : JsonDAO {

            override fun findValueByKeys(keys: List<String>): String? {
                return when (keys) {

                    listOf("user", "first_name") -> {
                        "Jane"
                    }

                    listOf("user", "last_name") -> {
                        "Doe"
                    }

                    listOf("location") -> {
                        "beach"
                    }

                    listOf("date") -> {
//                        "2021/05/22"
                        "2021-05-23T15:15:56.372081"
                    }

                    else -> {
                        null
                    }

                }
            }

            override fun findArrayByKey(key: String): List<JsonDAO> {
                TODO("Not yet implemented")
            }

        }

        val userDataSupplier = {
            userData.toMap()
        }

        val loggerSupplier = {
            TestLoggerImpl()
        }

        interpolator = InterpolatorImpl(
            tokenizer = TokenizerImpl(),
            jsonDAO = jsonDAO,
            userDataSupplier = userDataSupplier,
            loggerSupplier = loggerSupplier
        )

    }

    @Test
    fun `Interpolates data based handle bar expressions`() {
        // Arrange
        val expected = "Hello, Jane Doe"

        val input = "Hello, {{ data.user.first_name }} {{ data.user.last_name }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Interpolates user based handle bar expressions`() {
        // Arrange
        val expected = "Hello, Jane Doe"

        userData["first_name"] = "Jane"

        userData["last_name"] = "Doe"

        val input = "Hello, {{ user.first_name }} {{ user.last_name }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Interpolates any keyword based handle bar expressions`() {
        // Arrange
        val expected = "Hello, Jane Doe! How was your day at the beach?"

        userData["first_name"] = "Jane"

        userData["last_name"] = "Doe"

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! How was your day at the {{    data.location     }}?"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Ignore("Not Implemented Yet")
    fun `Formats any dates that have a format string`() {
        // Arrange
        val expected = "Hello, Jane Doe! Your last login was on: 2021-May-22"

        userData["first_name"] = "Jane"

        userData["last_name"] = "Doe"

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! Your last login was on: {{  date data.date  \"YYYY-MMM-dd\"   }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

}