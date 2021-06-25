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

import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.utils.TestJSON
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class TheImageURLExtractor {

    private val totalRuns = 10000

    @Before
    fun setUp() {
    }


    @Test
    fun `Extracts all the correct URLs`() {
        // Arrange
        val expected = setOf(
            "https://content.judo.app/images/5bb45475aeb2d39fffe523795268d45295a7de9cbef42d3ac1caea39ca124390.png",
            "https://content.judo.app/images/26bf0c721a51069cde1b8f45ecc100326eb5fb9e968c57c90f34a3f612d12f44.png",
            "https://content.judo.app/images/2cffe92e04748f67bbd63f109daed3457f46d8b0fba0c5496723fbeee597d037.png",
            "https://content.judo.app/images/ddfd9a25b7bcefbb6b51c99329c4dc453e7d9669f0a57dcfbc9114a46780245e.png",
            "https://content.judo.app/images/299f388f41e2f7185a2285908b72a3668c4e316d7fe6b2e5a57a712ffb411875.jpg",
            "https://content.judo.app/images/8bcc6c278903e965ba221711a68278acb274792eed750a133ff1abb0a70d998d.jpg",
        ).toList()

        // Act
        val actual: List<String> = JsonParser.parseExperience(TestJSON.experience)?.let { theExperience ->
            val urlExtractor = ImageURLExtractor()
            val measureTimeMillis = measureTime {
                repeat(totalRuns) {
                    urlExtractor.extract(listOf(theExperience))
                }
            }.inMilliseconds
            println("VISITOR TIME: $measureTimeMillis")
            urlExtractor.extract(listOf(theExperience))
        }?.toList() ?: emptyList()

        // Assert
        Assert.assertEquals(expected, actual)
    }

}