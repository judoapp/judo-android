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
import app.judo.sdk.core.data.dataContextOf
import app.judo.sdk.core.data.resolvers.resolveJson
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.lang.TokenizerImpl
import app.judo.sdk.utils.TestJSON
import app.judo.sdk.utils.TestLoggerImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class TheInterpolator {

    private lateinit var dataContext: DataContext

    private lateinit var interpolator: Interpolator

    @Before
    fun setUp() {

        dataContext = dataContextOf(
            "data" to mapOf(
                "location" to "beach",
                "dateTime" to "2021-06-19T15:54:01Z",
                "dateTimeNoZone" to "2021-06-19T15:54:01",
                "date" to "2021-06-19",
                "time" to "15:54:01",
                "timeZone" to "15:54:01Z",
                "user" to mapOf(
                    "first_name" to "Jane",
                    "last_name" to "Doe",
                )
            ),
            "user" to mapOf(
                "first_name" to "Jane",
                "last_name" to "Doe",
            )
        )

        val loggerSupplier = {
            TestLoggerImpl()
        }

        interpolator = InterpolatorImpl(
            tokenizer = TokenizerImpl(),
            loggerSupplier = loggerSupplier,
            dataContext = dataContext
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

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! How was your day at the {{    data.location     }}?"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Formats ISO 8601 dateTime`() {
        // Arrange
        val expected = "Hello, Jane Doe! Your last login was on: 2021-June-19"

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! Your last login was on: {{  date data.dateTime  \"YYYY-MMMM-dd\"   }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Formats ISO 8601 dateTime with no zone`() {
        // Arrange
        val expected = "Hello, Jane Doe! Your last login was on: 2021-June-19"

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! Your last login was on: {{  date data.dateTimeNoZone  \"YYYY-MMMM-dd\"   }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Formats ISO 8601 date`() {
        // Arrange
        val expected = "Hello, Jane Doe! Your last login was on: 2021-June-19"

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! Your last login was on: {{  date data.date  \"YYYY-MMMM-dd\"   }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Formats ISO 8601 time`() {
        // Arrange
        val expected = "Hello, Jane Doe! Your last login was at: 03:54"

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! Your last login was at: {{  date data.time  \"hh:mm\"   }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Formats ISO 8601 time with zone`() {
        // Arrange
        val expected = "Hello, Jane Doe! Your last login was at: 03:54"

        val input =
            "Hello, {{ user.first_name }} {{ user.last_name }}! Your last login was at: {{  date data.timeZone  \"hh:mm\"   }}"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Interpolates the uppercase helper`() {
        // Arrange
        val expected = "Hello, JANE DOE!"

        val input =
            "Hello, {{ uppercase user.first_name }} {{ uppercase user.last_name }}!"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Interpolates the lowercase helper`() {
        // Arrange
        val expected = "Hello, jane doe!"

        val input =
            "Hello, {{ lowercase user.first_name }} {{ lowercase user.last_name }}!"

        // Act
        val actual = interpolator.interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Interpolates can interpolate just data`() {
        // Arrange

        val data = resolveJson(TestJSON.dummy_api_response)

        val expected = data.toString()

        val input = "{{data}}"

        val context = dataContextOf(
            "data" to data
        )

        // Act
        val actual = InterpolatorImpl(
            tokenizer = TokenizerImpl(),
            dataContext = context
        ).interpolate(theTextToInterpolate = input)

        println(actual)

        // Assert
        Assert.assertEquals(expected, actual)
    }

}