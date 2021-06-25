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

package app.judo.sdk.core.data

import app.judo.sdk.api.models.DataSource
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Node
import app.judo.sdk.core.extensions.*
import app.judo.sdk.utils.TestJSON
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class TheExperienceTree {

    private lateinit var experience: Experience

    @Before
    fun setUp() {
        experience = JsonParser.parseExperience(TestJSON.data_source_experience_single_screen)!!
//        println(experience.nodes.joinToString("\n"))
    }

    @Test
    fun `Constructs valid groupings of screens and their Node Trees`() = runBlocking {
        // Arrange
        val experienceTree = ExperienceTree(experience)

        // Act
        val forest: List<Tree<Node>> = experienceTree.screenNodes.values.map { screenNode ->
            screenNode.trunk
        }

        val sourceTrees = mutableListOf<Tree<DataSource>>()

        forest.map { trunk ->
            async {
                sourceTree(trunk)?.let(sourceTrees::add)
            }
        }.awaitAll()

        sourceTrees.forEach {
            printTree(it)
        }

        println("\n\n\n")
        forest.forEach { trunk ->
            trunk.traverse { node ->
                println(node)
            }
        }

        // Assert
        assertTrue(experienceTree.screenNodes.isNotEmpty())
    }

    private suspend fun sourceTree(
        root: Tree<Node>
    ): Tree<DataSource>? {

        var sourceTree: Tree<DataSource>? = null

        root.root().traverse { node ->

            val value = node.value

            if (value is DataSource) {
                sourceTree = if (sourceTree == null) {
                    Tree(value)
                } else {

                    val newTree = Tree(
                        value,
                        sourceTree
                    )

                    sourceTree!!.children.add(
                        newTree
                    )

                    newTree
                }
            }
        }

        return sourceTree?.root()
    }

    private fun printTree(nodeTree: Tree<DataSource>) {
        if (nodeTree.parent?.value is DataSource) {
            println("  ${nodeTree.value::class.simpleName}: ${nodeTree.value.id};")
            println("  Descendant of: ${nodeTree.parent.value.id};")
        } else {
            println("${nodeTree.value::class.simpleName}: ${nodeTree.value.id};")
            if (nodeTree.children.isNotEmpty())
                println("Parent of:")
        }
        nodeTree.children.forEach { printTree(it) }
    }

    @Test
    fun `Is a Functor`() {
        // Arrange
        val tree: Tree<Int> = Tree(
            1,
            null,
            mutableListOf(
                Tree(2),
                Tree(3)
            )
        )

        val a = tree.flatten().joinToString()
        println(a)
        val b = tree.map { it.value }.flatten().joinToString()
        println(b)

        Assert.assertEquals(a, b)

        val square = { i: Int -> i * i }

        val stringify = { i: Int -> "$i" }

        val label = { i: String -> "String: $i" }

        val squareTree: Mapper<Int, Int> = { square(it.value) }

        val stringifyTree: Mapper<Int, String> = { stringify(it.value) }

        val labelTree: Mapper<String, String> = { label(it.value) }

        // Act
        val actual1 =
            tree.map { it.value * it.value }.map { "${it.value}" }.map { "String: ${it.value}" }
                .flatMap { node -> node.value }
                .joinToString()

        println("ACTUAL 1: $actual1")

        val actual2 = tree.map { label(stringify(square(it.value))) }
            .flatMap { node -> node.value }
            .joinToString()

        println("ACTUAL 2: $actual2")

        val actual3 = tree.map(squareTree).map(stringifyTree).map(labelTree)
            .flatMap { node -> node.value }
            .joinToString()

        println("ACTUAL 3: $actual3")

        // Assert
        Assert.assertEquals(actual1, actual2)
        Assert.assertEquals(actual1, actual3)
        Assert.assertEquals(actual2, actual3)
    }

}