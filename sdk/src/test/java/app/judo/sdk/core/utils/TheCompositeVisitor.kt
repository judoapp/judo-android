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

package app.judo.sdk.core.utils

import app.judo.sdk.api.models.Visitor
import app.judo.sdk.utils.TestExperience
import org.junit.Assert.assertEquals
import org.junit.Test

class TheCompositeVisitor {


    @Test
    fun `Composes other visitors correctly`() {
        // Arrange

        val expected = listOf("visited 1", "visited 2")

        val v1 = object : Visitor<String> {
            override fun getDefault(): String {
                return "visited 1"
            }
        }

        val v2 = object : Visitor<String> {
            override fun getDefault(): String {
                return "visited 2"
            }
        }

        val input = TestExperience()

        // Act
        val composite = visitorsOf(v1, v2)

        val actual = input.accept(composite)

        // Assert
        assertEquals(expected, actual)
    }

}