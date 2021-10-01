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

package app.judo.sdk.ui.state

import app.judo.sdk.core.data.arrayFromKeyPath
import app.judo.sdk.core.data.emptyDataContext
import app.judo.sdk.core.extensions.*
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.ui.robots.TreeNodeExtensionsRobot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TheTreeNodeExtensions : AbstractRobotTest<TreeNodeExtensionsRobot>() {

    override fun robotSupplier(): TreeNodeExtensionsRobot {
        return TreeNodeExtensionsRobot()
    }

    @Test
    fun `Can load DataContexts from the network`() = runBlocking(robot.testCoroutineDispatcher) {
        // Arrange
        val experience = robot.experience

        val modelTree = experience.nodes.toModelTree(
            rootNodeID = experience.initialScreenID
        )

        val renderTree = modelTree.map { it.value.toRenderable() }

        // Act

        renderTree.loadDataContext(
            emptyDataContext(),
            robot.environment
        )

        // Assert
        renderTree.forEach {
            if (it.value is DataSourceRenderable) {
                Assert.assertNotNull(it.value.data)
            }
        }
    }

    @Test
    fun `Can expand CollectionModelStates`() = runBlocking(robot.testCoroutineDispatcher) {
        // Arrange

        val expected = 10

        val experience = robot.experience

        val modelTree = experience.nodes.toModelTree(
            rootNodeID = experience.initialScreenID
        )

        val renderTree = modelTree.map { it.value.toRenderable() }

        renderTree.loadDataContext(
            emptyDataContext(),
            robot.environment
        )

        // Act

        renderTree.expandCollections()

        renderTree.forEach {
            if (it.value is CollectionRenderable) {
                val list = it.value.dataContext.arrayFromKeyPath(it.value.node.keyPath)
                println(list)
            }
        }

        // Assert
        renderTree.forEach {
            if (it.value is CollectionRenderable) {

                val actual = it.branches.size

                Assert.assertEquals(expected, actual)

            }
        }

        renderTree.interpolateValues(robot.environment.interpolator)

        val layoutTree = renderTree.toLayoutTree()
        println(layoutTree)


    }


    @Test
    fun `Can transform a RenderTree into a LayoutTree`() {
        // Arrange
        val experience = robot.experience

        val modelTree = experience.nodes.toModelTree(
            rootNodeID = experience.initialScreenID
        )

        val renderTree = modelTree.map { it.value.toRenderable() }

        // Act

        val actual = renderTree.toLayoutTree()

        println(actual)

        // Assert
        Assert.assertNotNull(actual)
    }

}