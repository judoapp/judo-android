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

package app.judo.sdk.core.cache

import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.KeyValueCacheRobot
import app.judo.sdk.utils.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
internal class KeyValueCacheTests : AbstractRobotTest<KeyValueCacheRobot>() {

    override fun robotSupplier(): KeyValueCacheRobot {
        return KeyValueCacheRobot()
    }

    @Test
    fun `Strings can be put into the cache`() {
        // Arrange
        val expected = true

        // Act
        val actual = robot.createString("aKey" to "value")

        // Assert
        expected shouldEqual actual
    }

    @Test
    fun `Strings can be retrieved from the cache by a String key`() {
        // Arrange
        val expected = "value"
        robot.createString("aKey" to expected)

        // Act
        val actual = robot.retrieveString("aKey")

        // Assert
        expected shouldEqual actual
    }

}