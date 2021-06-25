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

import app.judo.sdk.api.models.Screen
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.utils.TestJSON
import org.junit.Assert

import org.junit.Before
import org.junit.Test

internal class TheSourceAndParentBuilder {

    @Before
    fun setUp() {
    }

    @Test
    fun `Builds the correct source trees`() {
        // Arrange
        val experience = JsonParser.parseExperience(TestJSON.data_source_experience_single_screen)!!

        val input = experience.nodes<Screen>().first { it.id == experience.initialScreenID }

        val builder = ParentChildExtractor(experience = experience)

        // Act
        val actual = builder.extract(input)

        actual.forEach {
            println(it)
        }

//        println(actual)

        // Assert
        Assert.assertTrue(actual.isNotEmpty())
    }

}