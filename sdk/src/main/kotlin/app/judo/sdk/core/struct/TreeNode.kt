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

/**
 * N-ary Tree
 */
internal data class TreeNode<T>(
    val value: T,
    val branches: MutableList<TreeNode<T>> = mutableListOf()
) {

    constructor(
        value: T,
        vararg branches: T
    ) : this(value, branches.map { TreeNode(it) }.toMutableList())

    constructor(
        value: T,
        vararg branches: TreeNode<T>
    ) : this(value, branches.toMutableList())

    /**
     * Creates a new tree with the [newValue] added as a new branch
     * @param newValue The new object to add
     */
    fun addBranch(
        newValue: T
    ) {
        branches.add(TreeNode(newValue))
    }

    /**
     * Creates a new tree with the [newValue] added as a new branch
     * @param newValue The new object to add
     */
    fun addBranch(
        newValue: TreeNode<T>
    ) {
        branches.add(newValue)
    }

    /**
     * Traverses the tree in a depth first fashion.
     *
     * @param sideEffect Some computation or other work to be done with each tree.
     */
    fun forEach(sideEffect: (TreeNode<T>) -> Unit) {
        sideEffect(this)
        branches.forEach { it.forEach(sideEffect) }
    }

    /**
     * Traverses the tree in a depth first fashion.
     *
     * @param sideEffect Some computation or other work to be done with each tree's value.
     */
    fun forEachValue(sideEffect: (T) -> Unit) {
        sideEffect(value)
        branches.forEach { it.forEachValue(sideEffect) }
    }

    /**
     * Traverses the tree in a depth first fashion.
     *
     * @param sideEffect Some computation or other work to be done with each tree.
     */
    suspend fun forEachSuspending(sideEffect: suspend (TreeNode<T>) -> Unit) {
        sideEffect(this)
        branches.forEach { it.forEachSuspending(sideEffect) }
    }

    /**
     * Traverses the tree level by level.
     *
     * @param sideEffect Some computation or other work to be done with each tree.
     */
    fun forEachLevel(sideEffect: (TreeNode<T>) -> Unit) {

        sideEffect(this)

        if (branches.isEmpty()) return

        val queue = ArrayDeque<TreeNode<T>>()

        branches.forEach(queue::add)

        var tree = queue.removeFirstOrNull()

        while (tree != null) {
            sideEffect(tree)
            tree.branches.forEach(queue::add)
            tree = queue.removeFirstOrNull()
        }
    }

    /**
     * Traverses the tree level by level.
     *
     * @param sideEffect Some computation or other work to be done with each tree.
     */
    suspend fun forEachLevelSuspending(sideEffect: suspend (TreeNode<T>) -> Unit) {

        sideEffect(this)

        if (branches.isEmpty()) return

        val queue = ArrayDeque<TreeNode<T>>()

        branches.forEach(queue::add)

        var tree = queue.removeFirstOrNull()

        while (tree != null) {
            sideEffect(tree)
            tree.branches.forEach(queue::add)
            tree = queue.removeFirstOrNull()
        }
    }

    /**
     * Traverses the tree in a depth first fashion.
     * @param transform The mapping applied to each tree
     * @return A new [TreeNode] of [B]
     *
     */
    fun <B> map(transform: (TreeNode<T>) -> B): TreeNode<B> {
        return TreeNode(
            transform(this),
            branches = branches.map { it.map(transform) }.toMutableList()
        )
    }

    /**
     * Traverses the tree in a depth first fashion.
     * @param transform The mapping applied to each tree
     * @return A new [TreeNode] of [B]
     *
     */
    suspend fun <B> mapSuspending(transform: suspend (TreeNode<T>) -> B): TreeNode<B> {
        return TreeNode(
            transform(this),
            branches = branches.map { it.mapSuspending(transform) }.toMutableList()
        )
    }

    /**
     * Clones this tree to a new tree
     *
     * @param transform An optional transformation that can be applied to the [value].
     *
     * Usually used if [T] needs to be copied too.
     *
     * @return A new [TreeNode] of [T]
     *
     */
    fun clone(transform: (TreeNode<T>) -> T = { it.value }): TreeNode<T> {
        return map(transform)
    }

    /**
     * Traverses the tree in a depth first fashion and removes
     * trees matching the [predicate].
     *
     * @param predicate Determines whether or not a given branch should be removed.
     * @return A new [TreeNode] of [T]
     *
     */
    fun prune(predicate: (TreeNode<T>) -> Boolean) {

        branches.retainAll { !predicate(it) }

        branches.forEach {
            it.prune(predicate)
        }

    }

    /**
     * Flattens the tree into a [List].
     *
     * @return A [List] of [T]
     *
     */
    fun toList(): List<T> {
        val result = mutableListOf<T>()

        forEach { result.add(it.value) }

        return result
    }

}
