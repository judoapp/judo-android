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

package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.Condition
import app.judo.sdk.api.models.Predicate
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.dataContextOf
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.lang.TokenizerImpl
import app.judo.sdk.utils.TestJSON
import app.judo.sdk.utils.TestLoggerImpl
import org.json.JSONObject
import org.junit.Assert

import org.junit.Test

class TheConditionResolver {

    @Test
    fun resolve() {

        val condition = JsonParser.moshi.adapter(Condition::class.java).fromJson(TestJSON.condition)!!

        val dataContext = dataContextOf(
            "data" to 1.0
        )

        Assert.assertTrue(condition.resolve(dataContext))

    }

    @Test
    fun doubleEquality() {

        val condition = Condition("data", Predicate.EQUALS, 1.0)

        val dataContext = dataContextOf(
            "data" to 1.0
        )

        Assert.assertTrue(condition.resolve(dataContext))

    }

    @Test
    fun `should match a does-not-equal predicate on a double`() {

        val condition = Condition("data", Predicate.DOES_NOT_EQUAL, 42.0)

        val dataContext = dataContextOf(
            "data" to 1.0
        )

        Assert.assertTrue(condition.resolve(dataContext))

    }

    @Test
    fun `does not match a string with a double`() {
        val condition = Condition("data", Predicate.EQUALS, "1.0")

        val dataContext = dataContextOf(
            "data" to 1.0
        )

        Assert.assertFalse(condition.resolve(dataContext))
    }

    @Test
    fun `matches a greater-than predicate on a double`() {
        val condition = Condition("data", Predicate.IS_GREATER_THAN, 0.5)

        val dataContext = dataContextOf(
            "data" to 1.0
        )

        Assert.assertTrue(condition.resolve(dataContext))
    }

    @Test
    fun `does not match a greater-than predicate on disparate data types`() {
        val condition = Condition("data", Predicate.IS_GREATER_THAN, 0.5)

        val dataContext = dataContextOf(
            "data" to "1.0"
        )

        Assert.assertFalse(condition.resolve(dataContext))
    }

    @Test
    fun `interpolates the right hand side value`() {
        val condition = Condition("data", Predicate.EQUALS, """{{ user.firstName }}""")

        val userData = mapOf(
            "firstName" to "Jane"
        )

        val dataContext = dataContextOf(
            "data" to "Jane",
            "user" to userData
        )

        val interpolator = InterpolatorImpl(
            tokenizer = TokenizerImpl(),
            loggerSupplier = { TestLoggerImpl() },
            dataContext = dataContext
        )

        val rhs = interpolator.interpolate(
            theTextToInterpolate = condition.value as String,
            dataContext = dataContext
        )

        Assert.assertTrue(
            condition.resolve(
                dataContext, interpolator
            )
        )
    }

    @Test
    fun `IS_SET and IS_NOT_SET check for multiple null types`() {
        val userData = mapOf(
            "aJsonNull" to JSONObject.NULL,
            "aKotlinNull" to null,
            "aRealValue" to 42
        )

        val dataContext = dataContextOf(
            "user" to userData
        )

        val interpolator = InterpolatorImpl(
            tokenizer = TokenizerImpl(),
            loggerSupplier = { TestLoggerImpl() },
            dataContext = dataContext
        )

        // IS SET

        Assert.assertFalse(
            Condition("user.aJsonNull", Predicate.IS_SET, null).resolve(
                dataContext, interpolator
            )
        )

        Assert.assertFalse(
            Condition("user.aKotlinNull", Predicate.IS_SET, null).resolve(
                dataContext, interpolator
            )
        )

        Assert.assertFalse(
            Condition("user.aStringNull", Predicate.IS_SET, null).resolve(
                dataContext, interpolator
            )
        )

        Assert.assertTrue(
            Condition("user.aRealValue", Predicate.IS_SET, null).resolve(
                dataContext, interpolator
            )
        )

        // IS NOT SET

        Assert.assertTrue(
            Condition("user.aJsonNull", Predicate.IS_NOT_SET, null).resolve(
                dataContext, interpolator
            )
        )

        Assert.assertTrue(
            Condition("user.aKotlinNull", Predicate.IS_NOT_SET, null).resolve(
                dataContext, interpolator
            )
        )

        Assert.assertTrue(
            Condition("user.aStringNull", Predicate.IS_NOT_SET, null).resolve(
                dataContext, interpolator
            )
        )

        Assert.assertFalse(
            Condition("user.aRealValue", Predicate.IS_NOT_SET, null).resolve(
                dataContext, interpolator
            )
        )
    }
}