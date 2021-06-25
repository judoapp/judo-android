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

package app.judo.sdk.core.services

import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.ImageServiceRobot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
internal class ImageServiceTests : AbstractRobotTest<ImageServiceRobot>() {

    override fun robotSupplier(): ImageServiceRobot {
        return ImageServiceRobot()
    }

    @Test
    fun `given a url when retrieveImages then return correct results`() = runBlocking(robot.testCoroutineDispatcher) {
        // Arrange
        val url = "https://content.judo.app/images/0f29b839b787f9ff5fe427afaf1b2986b5d4be0894518643c5509ef10b142ba6.png"

        // Act
        val results = robot.getImages(url)

        // Assert
        Assert.assertTrue(results.firstOrNull() is ImageService.Result.Success)
    }

}