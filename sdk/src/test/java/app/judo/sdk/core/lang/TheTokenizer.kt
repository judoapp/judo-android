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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TheTokenizer {

    private lateinit var tokenizer: Tokenizer

    @Before
    fun setUp() {
        tokenizer = TokenizerImpl()
    }

    @Test
    fun `Transforms text into valid tokens`() {
        // Arrange
        val token1 = Token.RegularText(
            value = "Hello, ",
        )

        val token2 = Token.HandleBarExpression(
            value = "{{ data.world }}",
            keys = listOf("world"),
            keyword = Keyword.DATA,
            position = 7
        )

        val token3 = Token.RegularText(
            value = " !",
            position = 23
        )

        val expected: List<Token> = listOf(token1, token2, token3)

        val input = "Hello, {{ data.world }} !"

        // Act
        val actual: List<Token> = tokenizer.tokenize(text = input)

        println("ACTUAL:")
        actual.forEach(::println)

        val actual2: List<Token> = tokenizer.tokenize(text = "}")
        println("ACTUAL2:")
        actual2.forEach(::println)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Imbalanced handlebars throws IllegalStateException`() {
        // Arrange

        val input = "Hello, {{ data.world }"

        // Act

        val actual = try {
            tokenizer.tokenize(text = input)
            null
        } catch (e: IllegalStateException) {
            e
        }

        println("ACTUAL: $actual")

        // Assert
        assertTrue(actual is java.lang.IllegalStateException)
    }

}