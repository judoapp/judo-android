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

package app.judo.sdk.ui.events

import app.judo.sdk.utils.shouldEqual
import org.junit.Test
import java.util.*

class ExperienceRequestedTests {
    @Test
    fun `handles implicit Intent with experience URL`() {
        // Arrange
        val expected = "https://brand.judo.app/a-Experience"
        val event = ExperienceRequested(experienceURL = expected)

        // Act
        val actual = event.experienceURLForRequest

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `screenID is extracted correctly`() {
        // Arrange
        val expectedScreenID = UUID.randomUUID().toString()
        val expectedExperienceURL = "https://brand.judo.app/a-experience"
        val input = "${expectedExperienceURL}?screenID=$expectedScreenID&foo=thing"
        val event = ExperienceRequested(experienceURL = input)

        // Act
        val actualScreenID = event.screenId
        val actualExperienceURL = event.experienceURLForRequest

        // Assert
        expectedScreenID shouldEqual actualScreenID
        expectedExperienceURL shouldEqual actualExperienceURL
    }

    @Test
    fun `handles implicit Intent with a deep link experience URI`() {
        // Arrange
        val expected = "https://brand.judo.app/a-Experience"
        val event = ExperienceRequested(experienceURL = "brand://brand.judo.app/a-Experience")

        // Act
        val actual = event.experienceURLForRequest

        // Assert
        expected shouldEqual actual
    }
}