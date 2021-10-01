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

package app.judo.sdk.core.struct

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TheTreeNode {

    @Test
    fun `Can add branches from a value`() {

        val expected = 2

        val tree = TreeNode(0, 1, 2)

        val actual = tree.branches.size

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Can add other TreeNodes as branches`() {
        val expected = 2

        val tree = TreeNode(0, 1, 2)

        val actual = tree.branches.size

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun forEach() {
        val expected = listOf(0, 1, 3, 2)

        val branch1 = TreeNode(1, 3)
        val branch2 = TreeNode(2)

        val tree = TreeNode(0, branch1, branch2)

        val actual = mutableListOf<Int>()

        tree.forEach { actual.add(it.value) }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun forEachSuspending() = runBlockingTest {
        val expected = listOf(0, 1, 3, 2)

        val branch1 = TreeNode(1, 3)
        val branch2 = TreeNode(2)

        val tree = TreeNode(0, branch1, branch2)

        val actual = mutableListOf<Int>()

        tree.forEachSuspending { actual.add(it.value) }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun forEachLevel() {

        val expected = listOf(0, 1, 2, 3)

        val branch1 = TreeNode(1, 3)
        val branch2 = TreeNode(2)

        val tree = TreeNode(0, branch1, branch2)

        val actual = mutableListOf<Int>()

        tree.forEachLevel { actual.add(it.value) }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun map() {
        val expected = TreeNode("0", "1")

        val tree = TreeNode(0, 1)

        val actual = tree.map { it.value.toString() }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun mapSuspending() = runBlockingTest {
        val expected = TreeNode("0", "1")

        val tree = TreeNode(0, 1)

        val actual = tree.mapSuspending { it.value.toString() }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun clone() {
        val expected = TreeNode(1, 2)

        val tree = TreeNode(0, 1)

        val actual = tree.clone { it.value + 1 }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun prune() {
        val expected = TreeNode(1, 3)

        val actual = TreeNode(1, 2, 3)

        actual.prune { it.value == 2 }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun pruneLevel() {
        val expected = TreeNode(1, 3)

        val actual = TreeNode(1, 2, 3)

        actual.prune { it.value == 2 }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toList() {
        val expected = listOf(1, 2, 3)

        val tree = TreeNode(1, 2, 3)

        val actual = tree.toList()

        Assert.assertEquals(expected, actual)
    }

}