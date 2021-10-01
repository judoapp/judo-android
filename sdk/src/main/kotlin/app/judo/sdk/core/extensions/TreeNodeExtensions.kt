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

import android.graphics.Typeface
import app.judo.sdk.api.models.*
import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.arrayFromKeyPath
import app.judo.sdk.core.data.fromKeyPath
import app.judo.sdk.core.data.resolvers.resolveJson
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.interfaces.Actionable
import app.judo.sdk.core.interfaces.Imagery
import app.judo.sdk.core.interfaces.Typefaceable
import app.judo.sdk.core.lang.Interpolatable
import app.judo.sdk.core.lang.Keyword
import app.judo.sdk.core.lang.Translatable
import app.judo.sdk.core.services.DataSourceService
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.core.struct.TreeNode
import app.judo.sdk.core.utils.Translator
import app.judo.sdk.ui.state.*
import java.util.*

internal typealias ModelTree = TreeNode<Node>
internal typealias RenderTree = TreeNode<Renderable>
internal typealias LayoutTree = TreeNode<ViewState>

internal fun RenderTree.toLayoutTree(): LayoutTree {

    val viewStateBranches = mutableListOf<RenderTree>()

    fun addToViewStateBranches(treeNode: RenderTree) {
        when (treeNode.value) {

            is CollectionRenderable -> {
                treeNode.branches.forEach(::addToViewStateBranches)
            }

            is DataSourceRenderable -> {
                treeNode.branches.forEach(::addToViewStateBranches)
            }

            is ConditionalRenderable -> {
                treeNode.branches.forEach(::addToViewStateBranches)
            }

            else -> {
                viewStateBranches.add(treeNode)
            }

        }
    }

    branches.forEach(::addToViewStateBranches)

    val viewState =
        value.toViewState() ?: throw IllegalStateException("ViewState should not be null")

    return TreeNode(
        viewState,
        viewStateBranches.map { it.toLayoutTree() }.toMutableList()
    )
}

internal suspend fun RenderTree.loadDataContext(
    context: DataContext,
    environment: Environment,
    authorizersOverride: List<Authorizer>? = null
) {

    value.run {
        dataContext = context

        if (this is DataSourceRenderable) {
            data = loadDataSourceJson(node, context, environment, authorizersOverride)
                ?.let(::resolveJson)

            val nextContext = context.toMutableMap().apply {
                putAll(context)
                put(Keyword.DATA.value, data)
            }.toMap()

            branches.forEach { it.loadDataContext(nextContext, environment, authorizersOverride) }

        } else {

            branches.forEach { it.loadDataContext(context, environment, authorizersOverride) }

        }
    }

}

internal fun RenderTree.expandCollections() {

    if (value is CollectionRenderable) {

        // Generate contexts for each piece of data
        val contexts: List<DataContext> =
            value.dataContext.arrayFromKeyPath(value.node.keyPath).map { newData ->
                value.dataContext.toMutableMap().apply {
                    replace(Keyword.DATA.value, newData)
                }.toMap()
            }

        val newBranches: MutableList<RenderTree> = mutableListOf()

        // Copy branches for each context
        contexts.forEach { context ->
            branches.mapTo(newBranches) {
                it.clone { newBranch ->
                    // Associate the data to each new branch
                    // And Copy new nodes with new synthetic IDs
                    val newNodeID = UUID.randomUUID().toString()
                    when (newBranch.value) {
                        is AppBarRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is AudioRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is CarouselRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is CollectionRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is ConditionalRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is DataSourceRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is DividerRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is HStackRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is IconRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is ImageRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is MenuItemRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is PageControlRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is RectangleRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is ScreenRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is ScrollContainerRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is SpacerRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is TextRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is VStackRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is VideoRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is WebViewRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                        is ZStackRenderable -> {
                            newBranch.value.copy(
                                node = newBranch.value.node.copy(
                                    id = newNodeID,
                                ),
                                dataContext = context
                            )
                        }
                    }
                }
            }
        }

        branches.clear()
        branches.addAll(newBranches)

    }

    branches.forEach {
        it.expandCollections()
    }

}

internal fun RenderTree.sortCollections() {
    if (value is CollectionRenderable) {
        value.node.sortDescriptors.forEach { sortDescriptor ->

            branches.sortWith { o1, o2 ->

                val v1 = o1.value.dataContext.fromKeyPath(sortDescriptor.keyPath)

                val v2 = o2.value.dataContext.fromKeyPath(sortDescriptor.keyPath)

                if (sortDescriptor.ascending) {
                    when (v1) {
                        v2 -> {
                            0
                        }
                        null -> {
                            -1
                        }
                        else -> {
                            1
                        }
                    }
                } else {
                    when (v1) {
                        v2 -> {
                            0
                        }
                        null -> {
                            1
                        }
                        else -> {
                            -1
                        }
                    }
                }

            }
        }

    }

    branches.forEach {
        it.sortCollections()
    }

}

internal fun RenderTree.limitCollections() {
    if (value is CollectionRenderable) {

        val limit = value.node.limit

        if (limit != null && branches.isNotEmpty()) {

            val index = limit.startAt.dec()

            val newBranches = branches
                .subList(
                    index, branches.lastIndex
                )
                .take(limit.show)

            branches.clear()
            branches.addAll(newBranches)

        }

    }

    branches.forEach {
        it.limitCollections()
    }
}

internal fun RenderTree.setTypefaces(typeFaces: Map<String, Typeface>) {
    forEachValue { (it as? Typefaceable)?.setTypeface(typeFaces) }
}

internal fun RenderTree.setImageGetters(imageService: ImageService) {
    forEachValue { (it as? Imagery)?.setImageGetters(imageService) }
}

internal fun RenderTree.setActionHandlers(
    actionHandler: (
        node: Node,
        action: Action,
        dataContext: DataContext
    ) -> Unit
) {
    forEachValue { (it as? Actionable)?.setActionHandler(actionHandler) }
}

internal fun RenderTree.translateText(
    translator: Translator
) {
    forEachValue { (it as? Translatable)?.translate(translator) }
}

internal fun RenderTree.interpolateValues(
    interpolator: app.judo.sdk.core.interpolation.ProtoInterpolator
) {
    forEachValue { (it as? Interpolatable)?.interpolate(interpolator) }
}

internal fun RenderTree.pruneConditionals() {
    prune { (value, _) ->
        if (value is ConditionalRenderable) {
            !value.computedConditions.resolve(dataContext = value.dataContext)
        } else false
    }
}

internal suspend fun loadDataSourceJson(
    dataSource: DataSource,
    currentDataContext: DataContext,
    environment: Environment,
    authorizersOverride: List<Authorizer>? = null
): String? {

    val interpolator = environment.interpolator
    val dataService = environment.dataSourceService

    val headers = dataSource.headers.associate { header ->

        val key = interpolator(header.key, currentDataContext) ?: header.key

        val value = interpolator(header.value, currentDataContext) ?: header.value

        key to value

    }

    val interpolatedURL = interpolator(dataSource.url, currentDataContext) ?: dataSource.url

    val interpolatedBody = dataSource.httpBody?.let { interpolator(it, currentDataContext) }

    val result: DataSourceService.Result = dataService.performRequest(
        urlRequest = URLRequest(
            url = interpolatedURL,
            method = dataSource.httpMethod,
            headers = HashMap(headers),
            body = interpolatedBody
        ),
        authorizersOverride = authorizersOverride
    )

    return when (result) {

        is DataSourceService.Result.Failure -> {
            environment.logger.e(
                "DataLoader",
                result.error.message
            )
            null
        }

        is DataSourceService.Result.Success -> {
            result.body
        }

    }

}