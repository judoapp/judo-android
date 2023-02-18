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

package app.judo.sdk.compose.model.values

import org.junit.Assert.*
import org.junit.Test

class ConditionTest {
    val nullString: String? = null
    val helloString: String = "Hello"
    val goodbyeString: String = "Goodbye"
    val trueString: String = "true"
    val falseString: String = "false"
    val nullNumber: Number? = null
    val num64: Number = 64
    val num38: Number = 38.52
    val num0: Number = 0
    val num1: Number = 1
    val num2: Number = 2

    /* Testing Predicate.EQUALS */

    @Test
    fun stringPredicateEqualEqual() {
        assertTrue("Given strings must be equal.", isSatisifed(helloString, helloString, Predicate.EQUALS))
    }

    @Test
    fun stringPredicateEqualNotEqual() {
        assertFalse("Given strings must not be equal.", isSatisifed(goodbyeString, helloString, Predicate.EQUALS))
    }

    @Test
    fun stringPredicateEqualNullLhs() {
        assertFalse("Given strings must not be equal.", isSatisifed(nullString, helloString, Predicate.EQUALS))
    }

    @Test
    fun stringPredicateEqualNullRhs() {
        assertFalse("Given strings must not be equal.", isSatisifed(goodbyeString, nullString, Predicate.EQUALS))
    }

    @Test
    fun numPredicateEqualEqual() {
        assertTrue("Given numbers must be equal.", isSatisifed(num64, num64, Predicate.EQUALS))
    }

    @Test
    fun numPredicateEqualNotEqual() {
        assertFalse("Given numbers must not be equal.", isSatisifed(num0, num38, Predicate.EQUALS))
    }

    @Test
    fun numPredicateEqualNullLhs() {
        assertFalse("Given numbers must not be equal.", isSatisifed(nullString, helloString, Predicate.EQUALS))
    }

    @Test
    fun numPredicateEqualNullRhs() {
        assertFalse("Given numbers must not be equal.", isSatisifed(goodbyeString, nullString, Predicate.EQUALS))
    }

    @Test
    fun differingPredicateEqual() {
        assertFalse("Differing types must not be equal.", isSatisifed(nullString, num64, Predicate.EQUALS))
    }

    @Test
    fun nullPredicateEqual() {
        assertFalse("null predicates must not be equal.", isSatisifed(nullString, null, Predicate.EQUALS))
    }

    /* Testing Predicate.DOES_NOT_EQUAL */

    @Test
    fun stringPredicateNotEqualEqual() {
        assertFalse("Given strings must not be not equal.", isSatisifed(helloString, helloString, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun stringPredicateNotEqualNotEqual() {
        assertTrue("Given strings must be not equal.", isSatisifed(goodbyeString, helloString, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun stringPredicateNotEqualNullLhs() {
        assertTrue("Given strings must be not equal.", isSatisifed(nullString, helloString, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun stringPredicateNotEqualNullRhs() {
        assertTrue("Given strings must be not equal.", isSatisifed(goodbyeString, nullString, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun numPredicateNotEqualEqual() {
        assertFalse("Given numbers must not be not equal.", isSatisifed(num64, num64, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun numPredicateNotEqualNotEqual() {
        assertTrue("Given numbers must be not equal.", isSatisifed(num0, num38, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun numPredicateNotEqualNullLhs() {
        assertTrue("Given numbers must be not equal.", isSatisifed(nullString, helloString, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun numPredicateNotEqualNullRhs() {
        assertTrue("Given numbers must be not equal.", isSatisifed(num1, nullString, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun differingPredicateNotEqual() {
        assertTrue("Differing types must be not equal.", isSatisifed(nullString, num64, Predicate.DOES_NOT_EQUAL))
    }

    @Test
    fun nullPredicateNotEqual() {
        assertTrue("null predicates must be not equal.", isSatisifed(nullString, null, Predicate.DOES_NOT_EQUAL))
    }

    /* Testing Predicate.IS_GREATER_THAN */

    @Test
    fun numPredicateGreaterThanGreaterThan() {
        assertTrue("Lhs must be greater than rhs.", isSatisifed(num2, num1, Predicate.IS_GREATER_THAN))
    }

    @Test
    fun numPredicateGreaterThanLessThan() {
        assertFalse("Lhs must not be greater than rhs.", isSatisifed(num2, num38, Predicate.IS_GREATER_THAN))
    }

    @Test
    fun numPredicateGreaterThanEqual() {
        assertFalse("Lhs must not be greater than rhs.", isSatisifed(num0, num0, Predicate.IS_GREATER_THAN))
    }

    @Test
    fun numPredicateGreaterThanNullLhs() {
        assertFalse("Lhs must not be greater than rhs.", isSatisifed(nullNumber, num0, Predicate.IS_GREATER_THAN))
    }

    @Test
    fun numPredicateGreaterThenNullRhs() {
        assertFalse("Lhs must not be greater than rhs.", isSatisifed(num64, nullNumber, Predicate.IS_GREATER_THAN))
    }

    @Test
    fun differingPredicateGreaterThan() {
        assertFalse("Lhs must not be greater than rhs.", isSatisifed(goodbyeString, num1, Predicate.IS_GREATER_THAN))
    }

    @Test
    fun nullPredicateGreaterThan() {
        assertFalse("null predicates must not be greater than.", isSatisifed(nullNumber, null, Predicate.IS_GREATER_THAN))
    }

    /* Testing Predicate.IS_LESS_THAN */

    @Test
    fun numPredicateLessThanGreaterThan() {
        assertFalse("Lhs must not be less than rhs.", isSatisifed(num2, num1, Predicate.IS_LESS_THAN))
    }

    @Test
    fun numPredicateLessThanLessThan() {
        assertTrue("Lhs must be less than rhs.", isSatisifed(num2, num64, Predicate.IS_LESS_THAN))
    }

    @Test
    fun numPredicateLessThanEqual() {
        assertFalse("Lhs must not be less than rhs.", isSatisifed(num64, num64, Predicate.IS_LESS_THAN))
    }

    @Test
    fun numPredicateLessThanNullLhs() {
        assertFalse("Lhs must not be less than rhs.", isSatisifed(nullNumber, num64, Predicate.IS_LESS_THAN))
    }

    @Test
    fun numPredicateLessThenNullRhs() {
        assertFalse("Lhs must not be less than rhs.", isSatisifed(num2, nullNumber, Predicate.IS_LESS_THAN))
    }

    @Test
    fun differingPredicateLessThan() {
        assertFalse("Lhs must not be less than rhs.", isSatisifed(num2, helloString, Predicate.IS_LESS_THAN))
    }

    @Test
    fun nullPredicateLessThan() {
        assertFalse("null predicates must not be less than.", isSatisifed(nullNumber, null, Predicate.IS_LESS_THAN))
    }

    /* Testing Predicate.IS_SET */

    @Test
    fun predicateSetNull() {
        assertFalse("Value must not be set.", isSatisifed(nullNumber, null, Predicate.IS_SET))
    }

    @Test
    fun predicateSetNotNull() {
        assertTrue("Value must be set.", isSatisifed(goodbyeString, null, Predicate.IS_SET))
    }

    /* Testing Predicate.IS_NOT_SET */

    @Test
    fun predicateNotSetNull() {
        assertFalse("Value must not be not set.", isSatisifed(helloString, null, Predicate.IS_NOT_SET))
    }

    @Test
    fun predicateNotSetNotNull() {
        assertTrue("Value must be not set.", isSatisifed(nullString, null, Predicate.IS_NOT_SET))
    }

    /* Testing Predicate.IS_TRUE */

    @Test
    fun predicateTrueTrueString() {
        assertTrue("Value must be true for the string 'true'", isSatisifed(trueString, null, Predicate.IS_TRUE))
    }

    @Test
    fun predicateTrueFalseString() {
        assertFalse("Value must be false if not the string 'true'", isSatisifed(falseString, null, Predicate.IS_TRUE))
    }

    @Test
    fun predicateTrueTrueBoolean() {
        assertTrue("Value must be true for the value 'true'", isSatisifed(true, null, Predicate.IS_TRUE))
    }

    @Test
    fun predicateTrueFalseBoolean() {
        assertFalse("Value must be false if not the value 'true'", isSatisifed(false, null, Predicate.IS_TRUE))
    }

    @Test
    fun predicateTrueNumber() {
        assertFalse("Value must be false if not the string 'true'", isSatisifed(num1, null, Predicate.IS_TRUE))
    }

    @Test
    fun predicateTrueNull() {
        assertFalse("Value must be false if not the string 'true'", isSatisifed(null, null, Predicate.IS_TRUE))
    }

    /* Testing Predicate.IS_FALSE */

    @Test
    fun predicateFalseTrueString() {
        assertFalse("Value must be false if not the string 'false'", isSatisifed(trueString, null, Predicate.IS_FALSE))
    }

    @Test
    fun predicateFalseFalseString() {
        assertTrue("Value must be true for the string 'false'", isSatisifed(falseString, null, Predicate.IS_FALSE))
    }

    @Test
    fun predicateFalseTrueBoolean() {
        assertFalse("Value must be false if not the value 'false'", isSatisifed(true, null, Predicate.IS_FALSE))
    }

    @Test
    fun predicateFalseFalseBoolean() {
        assertTrue("Value must be true for the value 'false'", isSatisifed(false, null, Predicate.IS_FALSE))
    }

    @Test
    fun predicateFalseNumber() {
        assertFalse("Value must be false if not the string 'false'", isSatisifed(num0, null, Predicate.IS_FALSE))
    }

    @Test
    fun predicateFalseNull() {
        assertFalse("Value must be false if not the string 'false'", isSatisifed(null, null, Predicate.IS_FALSE))
    }
}