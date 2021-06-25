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

import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Node
import app.judo.sdk.api.models.NodeContainer
import app.judo.sdk.api.models.Screen
import app.judo.sdk.core.extensions.NodeTree

internal class ExperienceTree(
    val experience: Experience
) {

    data class ScreenNode(
        val screen: Screen,
        val trunk: NodeTree
    )

    val screenNodes: Map<String, ScreenNode> =
        experience.nodes<Screen>().associate { screen ->

            val nodeMap = experience.nodes.associateBy { it.id }

            val root = NodeTree(screen)

            val childNodes = screen.childIDs.mapNotNull { id -> nodeMap[id] }

            val trunk: NodeTree = root.insertNode(nodeMap, childNodes)

            screen.id to ScreenNode(screen = screen, trunk = trunk)
        }

    override fun equals(other: Any?): Boolean {
        return (other as? ExperienceTree)?.experience == experience
    }

    override fun hashCode(): Int {
        return experience.hashCode()
    }

    override fun toString(): String {
        return experience.toString()
    }
}

internal fun NodeTree.insertNode(
    nodeMap: Map<String, Node>,
    nodes: List<Node> = emptyList()
): NodeTree {
    if (nodes.isEmpty())
        return this

    val head = nodes.first()

    val next = Tree(value = head, parent = this)

    children.add(next)

    if (head is NodeContainer) {
        val childNodes = head.getChildNodeIDs().mapNotNull { id -> nodeMap[id] }
        next.insertNode(
            nodeMap = nodeMap,
            nodes = childNodes
        )
    }

    return insertNode(nodeMap, nodes.drop(1))
}