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

package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.Node
import app.judo.sdk.core.data.Tree

// region TYPES
internal typealias NodeTree = Tree<Node>

internal typealias SideEffect<T> = (Tree<T>) -> Unit

internal typealias SuspendingSideEffect<T> = suspend (Tree<T>) -> Unit

internal typealias Mapper<A, B> = (Tree<A>) -> B

internal typealias Predicate<A> = Mapper<A, Boolean>
// endregion TYPES

// region TRAVERSALS

/**
 * Traverses the tree in a depth first fashion.
 *
 * And applying the given [SideEffect] to each tree.
 */
internal fun <T> Tree<T>.traverse(sideEffect: SideEffect<T>) {

    sideEffect(this)
    children.forEach { it.traverse(sideEffect) }

}

/**
 * Traverses the tree in a depth first and suspending fashion.
 *
 * And applying the given [SuspendingSideEffect] to each tree.
 */
internal suspend fun <A> Tree<A>.traverseSuspending(sideEffect: SuspendingSideEffect<A>) {

    sideEffect(this)
    children.forEach { it.traverseSuspending(sideEffect) }

}

// endregion TRAVERSALS

// region OPERATIONS
internal fun <A, B> Tree<A>.map(mapper: Mapper<A, B>): Tree<B> {

    val mappedValue = mapper(this)

    val result = Tree(value = mappedValue)

    val newChildren = children.map { child ->
        Tree(
            value = mapper(child),
            parent = result,
            children = child.children.map { grandChild ->
                grandChild.map(mapper)
            }.toMutableList()
        )
    }

    result.children.addAll(newChildren)

    return result

}

/**
 * Prunes branches that do not match the given predicate.
 *
 * For a non-destructive version of [Tree.copy] can be called first.
 */
internal fun <A> Tree<A>.prune(predicate: Predicate<A>): Tree<A>? {

    if (!predicate(this)) return null

    val result = Tree(value)

    val newChildren = children.mapNotNull loop@{ child ->
        if (!predicate(child)) return@loop null

        Tree(
            value = child.value,
            parent = result,
            children = child.children.mapNotNull { grandChild ->
                grandChild.prune(predicate)
            }.toMutableList()
        )
    }

    result.children.addAll(newChildren)

    return result

}

/**
 * Maps each value in the tree to a [List] in a depth first fashion.
 */
internal fun <A, B> Tree<A>.flatMap(transform: Mapper<A, B>): List<B> {

    val result = mutableListOf<B>()

    traverse { tree ->
        result.add(transform(tree))
    }

    return result.toList()
}

/**
 * Maps each value in the tree to a [List] in a depth first fashion.
 */
internal fun <A> Tree<A>.flatten(): List<A> {
    return flatMap { element -> element.value }
}

/**
 * Returns the first node in the tree that has no parent AKA the Root.
 */
internal tailrec fun <A> Tree<A>.root(): Tree<A> {
    if (parent == null) {
        return this
    }

    return parent.root()
}

/**
 * Returns a duplicate of the tree. The duplicate is done in a depth first order.
 */
internal fun <A> Tree<A>.copy() = map { it.value }

/**
 * Searches up the tree for the first parent matching the [Predicate].
 */
internal tailrec fun <A> Tree<A>.findNearestAncestor(predicate: (A) -> Boolean): A? {
    val parent = parent ?: return null
    val parentValue = parent.value
    if (parentValue != null && predicate(parentValue)) return parentValue
    return parent.findNearestAncestor(predicate)
}
// endregion OPERATIONS