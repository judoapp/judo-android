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

package app.judo.sdk.ui.layout

import android.util.Log
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.data.*
import app.judo.sdk.core.data.ExperienceTree
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.extensions.*
import app.judo.sdk.core.extensions.findNearestAncestor
import app.judo.sdk.core.extensions.resolve
import app.judo.sdk.core.extensions.traverse
import app.judo.sdk.core.extensions.urlParams
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.lang.Keyword
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

internal class NodeTransformationPipeline(
    val environment: Environment,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    private var urlParams: Map<String, String> = emptyMap()

    private fun addImplicitStacksForScrollContainers(nodes: List<Node>): List<Node> {
        val modifiedNodes = nodes.toMutableList()
        val scrollContainers = nodes.filterIsInstance<ScrollContainer>()
        scrollContainers.map { scrollContainer ->
            if (scrollContainer.childIDs.count() > 1) {
                val stackNode = if (scrollContainer.axis == Axis.VERTICAL) {
                    VStack(
                        id = "${scrollContainer.id}-vstack",
                        name = null,
                        spacing = 0f,
                        alignment = HorizontalAlignment.CENTER,
                        childIDs = scrollContainer.childIDs
                    )
                } else {
                    HStack(
                        id = "${scrollContainer.id}-hstack",
                        name = null,
                        spacing = 0f,
                        alignment = VerticalAlignment.CENTER,
                        childIDs = scrollContainer.childIDs
                    )
                }
                val newScrollContainer = scrollContainer.copy(childIDs = listOf(stackNode.id))
                modifiedNodes.remove(scrollContainer)
                modifiedNodes.add(newScrollContainer)
                modifiedNodes.add(stackNode)
            }
        }

        return modifiedNodes
    }

    suspend fun transformScreenNodesForLayout(
        nodes: List<Node>,
        requestedImages: List<RequestedImageDimensions>,
        screenID: String,
        actionTargetData: Any? = null,
        experienceTree: ExperienceTree? = null,
        userInfo: Map<String, String> = emptyMap()
    ): NodesTransformInfo {
        return withContext(defaultDispatcher) {

             experienceTree?.experience?.url?.urlParams()?.let {
                urlParams = it
            }
            val screenNodes = filterNodesForScreen(screenID, nodes)
            val screenDirectChildren = (screenNodes.find { it.id == screenID } as? Screen)?.getChildNodeIDs()
            val swipeToRefresh = (screenNodes.any { screenDirectChildren?.contains(it.id) == true && it is ScrollContainer } && screenNodes.any { it is DataSource })

            val canCache = (actionTargetData == null && nodes.any { it is Collection })

            modifyActionsForDataSource(screenNodes)
            addDataFromPreviousScreenData(screenID = screenID, targetData = actionTargetData, experienceTree = experienceTree, userInfo)
            val dataSourcesRemoved = removeDataSources(screenNodes)
            val collectionsRemoved = removeCollections(dataSourcesRemoved, screenID)
            val conditionalsRemoved = removeConditionals(screenID, collectionsRemoved.first, collectionsRemoved.second, userInfo)
            val addedImageSizes = addImageSizesForImagesWithoutSize(requestedImages, conditionalsRemoved.first)
            val imageWithoutSizesRemoved = removeImagesWithoutSizes(addedImageSizes)
            val imagesWithoutSizes = getImagesWithoutSizes(addedImageSizes).map { it.interpolatedImageURL }.toSet()
            val interpolationRequiredRemoved = removeAnyNodesRequiringInterpolation(imageWithoutSizesRemoved)
            val implicitStacksAdded = addImplicitStacksForScrollContainers(interpolationRequiredRemoved)

            NodesTransformInfo(implicitStacksAdded, imagesWithoutSizes, conditionalsRemoved.second, swipeToRefresh, canCache)
        }
    }

    private fun removeDataSources(nodes: List<Node>): List<Node> {
        // remove data sources and attach children to the data source parent
        val modifiedNodes = nodes.toMutableList()
        val dataSources = nodes.filterIsInstance<DataSource>()
        val dataSourceIDs = dataSources.map { it.id }

        nodes.forEach {
            if (it is NodeContainer
                && dataSourceIDs.any { dataSourceID -> dataSourceID in it.getChildNodeIDs() }
                && it !in dataSources
            ) {

                val childIDs = it.getChildNodeIDs()
                val theDataSourcesChildIDs = getDataSourceChildIDs(dataSources, childIDs)

                when (it) {
                    is Conditional -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is HStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is VStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is ScrollContainer -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is ZStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is Screen -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is Carousel -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is AppBar -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                }
            }
        }
        modifiedNodes.removeAll(dataSources)

        return modifiedNodes
    }

    private fun getDataSourceChildIDs(dataSources: List<DataSource>, ids: List<String>): MutableList<String> {
        val dataSourceIds = dataSources.map { it.id }

        val nestedIDs = mutableListOf<String>()
        dataSources.forEach {
            if (it.id in ids) nestedIDs.addAll(getDataSourceChildIDs(dataSources, it.childIDs))
        }
        return (ids.filter { it !in dataSourceIds } + nestedIDs).toMutableList()
    }

    private fun removeImagesWithoutSizes(nodes: List<Node>): List<Node> {
        val imageBackgroundNodes = nodes.filter { it is Backgroundable && (it.background?.node as? Image)?.hasImageDimensions() == false }
        val imageOverlayNodes = nodes.filter { it is Overlayable && (it.overlay?.node as? Image)?.hasImageDimensions() == false }
        val imageNodes = nodes.filterIsInstance<Image>().filter { !it.hasImageDimensions() }

        return nodes.filter { it !in imageNodes + imageOverlayNodes + imageBackgroundNodes }
    }

    private fun getImagesWithoutSizes(nodes: List<Node>): List<Image> {
        val imageBackgroundNodes = nodes.filter { (it is Backgroundable) && (it.background?.node is Image) }.map {
            ((it as Backgroundable).background?.node as Image)
        }
        val imageOverlayNodes = nodes.filter { (it is Overlayable) && (it.overlay?.node is Image) }.map {
            ((it as Overlayable).overlay?.node as Image)
        }

        return (nodes + imageBackgroundNodes + imageOverlayNodes).filterIsInstance<Image>().filter {
            !it.hasImageDimensions()
        }
    }

    private fun String.requiresInterpolation() = this.contains(Regex(Environment.RegexPatterns.HANDLE_BAR_EXPRESSION_PATTERN))

    private fun removeAnyNodesRequiringInterpolation(nodes: List<Node>): List<Node> {
        val modifiedNodes = nodes.toMutableList()

        return modifiedNodes.filterNot { nodeRequiresInterpolation(it)
                    || (it is Overlayable && nodeRequiresInterpolation(it.overlay?.node))
                    || (it is Backgroundable && nodeRequiresInterpolation(it.background?.node))
        }
    }

    private fun nodeRequiresInterpolation(node: Node?): Boolean {
        return when (node) {
            is Image -> node.interpolatedImageURL.requiresInterpolation()
            is WebView -> node.interpolatedURL.requiresInterpolation()
            is Text -> node.interpolatedText.requiresInterpolation()
            is Audio -> node.interpolatedSourceURL.requiresInterpolation()
            is Video -> node.interpolatedSourceURL.requiresInterpolation()
            is AppBar -> node.interpolatedTitle.requiresInterpolation()
            else -> false
        }
    }

    private fun getChildNodeIDs(nodeID: String, nodes: List<Node>): List<String> {
        return when(val node = nodes.find { it.id == nodeID }) {
            is NodeContainer -> listOf(node.id) + node.getChildNodeIDs().flatMap { childID -> getChildNodeIDs(childID, nodes) }
            is Node -> listOf(node.id)
            else -> emptyList()
        }
    }

    private fun filterNodesForScreen(screenID: String, nodes: List<Node>): List<Node> {
        val currentScreenIDs = getChildNodeIDs(screenID, nodes)
        return nodes.filter { it.id in currentScreenIDs }
    }

    private fun modifyActionsForDataSource(nodes: List<Node>) {
        // sort data sources by depth and attach DAOs to descendants with actions
        val dataSources = nodes.filterIsInstance<DataSource>()

        dataSources.forEach { dataSource ->
            val directChildren = dataSource.childIDs
            val descendantIDs = directChildren.flatMap { directChild -> getChildNodeIDs(directChild, nodes) }
            descendantIDs.forEach { descendantID ->
                val descendantNode = nodes.find { it.id == descendantID }!!
                if (descendantNode is Actionable) {
                    descendantNode.action = convertActionToActionWithData(descendantNode.action, dataSource.data)
                }
                if (descendantNode is Backgroundable) {
                    (descendantNode.background?.node as? Actionable)?.let {
                        if (it.action != null) it.action = convertActionToActionWithData(it.action, dataSource.data)
                    }
                }
                if (descendantNode is Overlayable) {
                    (descendantNode.overlay?.node as? Actionable)?.let {
                        if (it.action != null) it.action = convertActionToActionWithData(it.action, dataSource.data)
                    }
                }
            }
        }
    }

    private fun convertActionToActionWithData(action: Action?, data: Any?): Action? {
        return if (action is Action.PerformSegue) {
            action.copy().apply {
                this.data = data
            }
        } else {
            action
        }
    }

    private fun addImageSizesForImagesWithoutSize(requestedImages: List<RequestedImageDimensions>, nodes: List<Node>): List<Node> {
        // add image sizes to images without explicit sizes if the image is in the cache
        val modifiedNodes = nodes.toMutableList()

        val requestedImageURLs = requestedImages.map { it.url }

        val nodesWithBackgroundImages = nodes.filter {
            (it is Backgroundable)
                    && (it.background?.node is Image)
                    && (it.background?.node as? Image)?.hasImageDimensions() != true
                    && (it.background!!.node as Image).interpolatedImageURL in requestedImageURLs
        }
        val nodesWithOverlayImages = nodes.filter {
            (it is Overlayable)
                    && (it.overlay?.node is Image)
                    && (it.overlay?.node as? Image)?.hasImageDimensions() != true
                    && (it.overlay!!.node as Image).interpolatedImageURL in requestedImageURLs
        }

        val imageNodesWithoutDimensions = nodes.filterIsInstance<Image>().filter {
            !it.hasImageDimensions()
        }

        val modifiedNodesWithBackgroundImagesWithDimensions = nodesWithBackgroundImages.map {

            val backgroundNode = ((it as Backgroundable).background!!.node as Image)
            val interpolatedImageURL = backgroundNode.interpolatedImageURL
            val imageDimensions = requestedImages.find { dimensions -> dimensions.url == interpolatedImageURL }!!

            val copiedBackgroundNode = backgroundNode.copy(imageWidth = imageDimensions.width, imageHeight = imageDimensions.height)

            val copiedBackground = it.background!!.copy(node = copiedBackgroundNode)

            when (val node = it) {
                is Rectangle -> node.copy(background = copiedBackground)
                is Image -> node.copy(background = copiedBackground)
                is Audio -> node.copy(background = copiedBackground)
                is Video -> node.copy(background = copiedBackground)
                is Carousel -> node.copy(background = copiedBackground)
                is ZStack -> node.copy(background = copiedBackground)
                is HStack -> node.copy(background = copiedBackground)
                is VStack -> node.copy(background = copiedBackground)
                is WebView -> node.copy(background = copiedBackground)
                is PageControl -> node.copy(background = copiedBackground)
                else -> it
            }
        }

        val modifiedNodesWithOverlayImagesWithDimensions = nodesWithOverlayImages.map {
            val overlayNode = ((it as Overlayable).overlay!!.node as Image)
            val interpolatedImageURL = overlayNode.interpolatedImageURL
            val imageDimensions = requestedImages.find { dimensions -> dimensions.url == interpolatedImageURL }!!

            val copiedOverlayNode = overlayNode.copy(imageWidth = imageDimensions.width, imageHeight = imageDimensions.height)

            val copiedOverlay = it.overlay!!.copy(node = copiedOverlayNode)

            when (val node = it) {
                is Rectangle -> node.copy(overlay = copiedOverlay)
                is Image -> node.copy(overlay = copiedOverlay)
                is Audio -> node.copy(overlay = copiedOverlay)
                is Video -> node.copy(overlay = copiedOverlay)
                is Carousel -> node.copy(overlay = copiedOverlay)
                is ZStack -> node.copy(overlay = copiedOverlay)
                is HStack -> node.copy(overlay = copiedOverlay)
                is VStack -> node.copy(overlay = copiedOverlay)
                is WebView -> node.copy(overlay = copiedOverlay)
                is PageControl -> node.copy(overlay = copiedOverlay)
                else -> it
            }
        }

        val modifiedImageNodesWithDimensions = imageNodesWithoutDimensions.map { image ->
            val interpolatedImageURL = image.interpolatedImageURL
            if (requestedImageURLs.contains(interpolatedImageURL)) {
                val imageDimensions = requestedImages.find { it.url == interpolatedImageURL }!!
                return@map image.copy(imageWidth = imageDimensions.width, imageHeight = imageDimensions.height).apply {
                    interpolator = image.interpolator
                }
            } else {
                return@map image
            }
        }

        modifiedNodes.removeAll(nodesWithOverlayImages)
        modifiedNodes.addAll(modifiedNodesWithOverlayImagesWithDimensions)
        modifiedNodes.removeAll(nodesWithBackgroundImages)
        modifiedNodes.addAll(modifiedNodesWithBackgroundImagesWithDimensions)
        modifiedNodes.removeAll(imageNodesWithoutDimensions)
        modifiedNodes.addAll(modifiedImageNodesWithDimensions)

        return modifiedNodes
    }

    private fun addDataFromPreviousScreenData(
        screenID: String,
        targetData: Any? = null,
        experienceTree: ExperienceTree? = null,
        userInfo: Map<String, String>
    ) {

        if (targetData != null) {

            val trunk = experienceTree?.screenNodes?.get(screenID)?.trunk

            val dataContext = dataContextOf(
                Keyword.USER.value to userInfo,
                Keyword.DATA.value to targetData,
                Keyword.URL.value to urlParams
            )

            trunk?.traverse { tree ->

                val node = tree.value

                val nearestDataSource: DataSource? =
                    tree.findNearestAncestor { it is DataSource } as? DataSource

                if (nearestDataSource == null) {

                    if (node is Collection) {
                        node.items = dataContext.arrayFromKeyPath(node.keyPath)
                    }

                    if (node !is DataSource && node is SupportsInterpolation) {

                        node.interpolator = InterpolatorImpl(
                            dataContext = dataContext
                        )

                    }

                }
            }

        }

    }

    private fun removeConditionals(
        screenID: String,
        nodes: List<Node>,
        collectionChildIDs: List<String>,
        userInfo: Map<String, String>
    ): Pair<List<Node>, List<String>> {
        if (nodes.any { it is Conditional }) {
            val screenNode: Screen = (nodes.find { it.id == screenID } as Screen?)!!

            val nodeMap = nodes.associateBy { it.id }
            val root = NodeTree(screenNode)
            val childNodes = screenNode.childIDs.mapNotNull { id -> nodeMap[id] }
            val trunk: NodeTree = root.insertNode(nodeMap, childNodes)

            // removes branches from the tree for conditionals that resolve to false
            val pruned = trunk.prune { tree ->
                val node = tree.value
                if (node is Conditional) {
                    val nearestCollection: Collection? = tree.findNearestAncestor { it is Collection } as? Collection
                    if (nearestCollection != null) {
                        val dataSourceAncestor = (tree.findNearestAncestor { it is DataSource } as? DataSource)

                        dataSourceAncestor?.let { dataSource ->
                            val dataContext = dataContextOf(
                                Keyword.USER.value to userInfo,
                                Keyword.DATA.value to dataSource.data,
                                Keyword.URL.value to urlParams
                            )
                            return@prune node.conditions.resolve(dataContext)
                        }
                    }
                }
                return@prune true
            }
            val containerIDToConditionalChildren = mutableMapOf<String, List<String>>()
            val modifiedCollectionChildIDs = collectionChildIDs.toMutableList()

            // builds up a map of container ids to the children of conditionals for containers that
            // have conditional children so that we can modify the container child ids to remove the id
            // of the conditional and add the ids of the conditional's children

            // this also checks to see if any conditionals are direct children of collections so
            // we can remove their ids from the list of collection children and add their children to the list
            pruned?.traverse { tree ->
                val node = tree.value
                if (node is NodeContainer) {
                    val conditionals = tree.children.filter { it.value is Conditional }

                    conditionals.forEach { conditional ->
                        val index = tree.children.indexOf(conditional)
                        tree.children.addAll(index, conditional.children)
                        tree.children.remove(conditional)
                        containerIDToConditionalChildren.put(node.id, tree.children.map { it.value.id })
                        if (conditional.value.id in modifiedCollectionChildIDs) {
                            val collectionChildIndex = modifiedCollectionChildIDs.indexOf(conditional.value.id)
                            modifiedCollectionChildIDs.addAll(collectionChildIndex, conditional.children.map { it.value.id })
                            modifiedCollectionChildIDs.remove(conditional.value.id)
                        }
                    }
                }
            }
            val prunedList = pruned?.flatten() ?: emptyList()

            val modifiedNodes = prunedList.toMutableList()

            // modify the list of nodes by copying container nodes with their new child id lists
            // based upon resolving and removing conditionals
            containerIDToConditionalChildren.keys.forEach { containerID ->
                val container = (modifiedNodes.find { it.id == containerID } as? NodeContainer)!!

                containerIDToConditionalChildren[containerID]?.let { newChildList ->
                    when (container) {
                        is HStack -> {
                            modifiedNodes.remove(container)
                            modifiedNodes.add(container.copy(childIDs = newChildList))
                        }
                        is VStack -> {
                            modifiedNodes.remove(container)
                            modifiedNodes.add(container.copy(childIDs = newChildList))
                        }
                        is ScrollContainer -> {
                            modifiedNodes.remove(container)
                            modifiedNodes.add(container.copy(childIDs = newChildList))
                        }
                        is ZStack -> {
                            modifiedNodes.remove(container)
                            modifiedNodes.add(container.copy(childIDs = newChildList))
                        }
                        is Screen -> {
                            modifiedNodes.remove(container)
                            modifiedNodes.add(container.copy(childIDs = newChildList))
                        }
                        is Carousel -> {
                            modifiedNodes.remove(container)
                            modifiedNodes.add(container.copy(childIDs = newChildList))
                        }
                        is AppBar -> {
                            modifiedNodes.remove(container)
                            modifiedNodes.add(container.copy(childIDs = newChildList))
                        }
                    }
                }
            }

            return modifiedNodes.filter { it !is Conditional } to modifiedCollectionChildIDs
        }
        return nodes to collectionChildIDs
    }

    private fun copy(
        data: Any?,
        baseID: String,
        nodeToCopy: Node,
        nodes: List<Node>,
    ): List<Node> {

        val dataContext = dataContextOf(
            Keyword.USER.value to Environment.current.userInfoSupplier.supplyUserInfo(),
            Keyword.DATA.value to data,
            Keyword.URL.value to urlParams
        )

        return when (nodeToCopy) {
            is Icon -> {
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = convertActionToActionWithData(nodeToCopy.action, data)
                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    mask = mask,
                    action = action
                )

                listOf(copiedNode)
            }
            is Rectangle -> {
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = convertActionToActionWithData(nodeToCopy.action, data)
                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action
                )

                listOf(copiedNode)
            }
            is Image -> {
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = convertActionToActionWithData(nodeToCopy.action, data)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action
                ).apply {
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }

                listOf(copiedNode)
            }
            is WebView -> {
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask
                ).apply {
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }

                listOf(copiedNode)
            }
            is Conditional -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes) }
                val childIDs = children.map { "${baseID}-${it.id}" }

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    childIDs = childIDs,
                )

                listOf(copiedNode) + copiedDescendants
            }
            is ZStack -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = convertActionToActionWithData(nodeToCopy.action, data)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    childIDs = childIDs,
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action
                )

                listOf(copiedNode) + copiedDescendants
            }
            is VStack -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = convertActionToActionWithData(nodeToCopy.action, data)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    childIDs = childIDs,
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action
                )

                listOf(copiedNode) + copiedDescendants
            }
            is Screen -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes) }
                listOf(nodeToCopy.copy(id = "${baseID}-${nodeToCopy.id}", childIDs = copiedDescendants.map { it.id })) + copiedDescendants
            }
            is ScrollContainer -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    childIDs = childIDs,
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask
                )

                listOf(copiedNode) + copiedDescendants
            }
            is Divider -> {
                listOf(nodeToCopy.copy(id = "${baseID}-${nodeToCopy.id}"))
            }
            is PageControl -> {
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask
                )

                listOf(copiedNode)
            }
            is Carousel -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    childIDs = childIDs,
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask
                )

                listOf(copiedNode) + copiedDescendants
            }
            is Text -> {
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = convertActionToActionWithData(nodeToCopy.action, data)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action
                ).apply {
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }

                listOf(copiedNode)
            }
            is HStack -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = convertActionToActionWithData(nodeToCopy.action, data)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    childIDs = childIDs,
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action
                )

                listOf(copiedNode) + copiedDescendants
            }
            is Spacer -> {
                listOf(nodeToCopy.copy(id = "${baseID}-${nodeToCopy.id}"))
            }
            is Audio -> {
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask
                ).apply {
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }

                listOf(copiedNode)
            }
            is Video -> {
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask
                ).apply {
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }

                listOf(copiedNode)
            }
            else -> throw IllegalStateException()
        }
    }

    private fun copyBackground(id: String, background: Background?): Background? {
        val backgroundNode: Node? = background?.node?.let {
            when(it) {
                is Rectangle -> it.copy(id = "${id}-${it.id}")
                is Text -> it.copy(id = "${id}-${it.id}")
                is Image -> it.copy(id = "${id}-${it.id}")
                else -> null
            }
        }
        return backgroundNode?.let {
            background.copy(node = backgroundNode, alignment = background.alignment)
        }
    }

    private fun copyOverlay(id: String, overlay: Overlay?): Overlay? {
        val overlayNode: Node? = overlay?.node?.let {
            when(it) {
                is Rectangle -> it.copy(id = "${id}-${it.id}")
                is Text -> it.copy(id = "${id}-${it.id}")
                is Image -> it.copy(id = "${id}-${it.id}")
                else -> null
            }
        }
        return overlayNode?.let {
            overlay.copy(node = overlayNode, alignment = overlay.alignment)
        }
    }

    private fun copyMask(id: String, mask: Node?): Node? {
        return mask?.let {
            when (it) {
                is Rectangle -> it.copy(id = "${id}-${it.id}")
                else -> null
            }
        }
    }

    private fun getNodeIDsWithoutCarouselAncestors(nodeID: String, nodes: List<Node>): List<String> {
        return when(val node = nodes.find { it.id == nodeID }) {
            is Carousel -> emptyList()
            is NodeContainer -> listOf(node.id) + node.getChildNodeIDs().flatMap { getNodeIDsWithoutCarouselAncestors(it, nodes) }
            is Node -> listOf(node.id)
            else -> emptyList()
        }
    }

    private fun removeCollections(nodes: List<Node>, screenNodeID: String): Pair<List<Node>, List<String>> {
        // remove collections by copying the children for each DAO and attaching the children to
        // the collection parent
        val modifiedNodes = nodes.toMutableList()
        val collections = nodes.filterIsInstance<Collection>()
        val collectionIDs = collections.map { it.id }
        val nodesWithoutCarouselAncestors = getNodeIDsWithoutCarouselAncestors(screenNodeID, nodes)

        // copy nodes in collection
        collections.forEach {
            val collectionChildIDs = it.childIDs
            val collectionChildren = modifiedNodes.filter { node -> node.id in collectionChildIDs }
            val copiedNodes = mutableListOf<Node>()
            val directCopiedNodeID = mutableListOf<String>()

            collectionChildren.forEach { collectionChild ->
                it.items?.forEachIndexed { index, data ->
                    val copies = copy(
                        data,
                        "${it.id}-${index}",
                        collectionChild,
                        modifiedNodes
                    )
                    directCopiedNodeID.add(copies.first().id)
                    copiedNodes.addAll(copies)
                }
            }
            modifiedNodes.addAll(copiedNodes)
            modifiedNodes.remove(it)
            modifiedNodes.removeAll(collectionChildren)

            val collection = it.copy(childIDs = directCopiedNodeID - collectionChildIDs)
            modifiedNodes.add(collection)
        }

        val directCopyIDs = modifiedNodes.filterIsInstance<Collection>().filter { it.id in nodesWithoutCarouselAncestors }.flatMap { it.childIDs }

        // remove collections
        nodes.forEach {
            if (it is NodeContainer) {
                val childCollectionIDs = it.getChildNodeIDs().filter { childID -> childID in collectionIDs }
                val collectionChildIDs = modifiedNodes.filterIsInstance<Collection>().filter { collection -> collection.id in childCollectionIDs }.flatMap { collection -> collection.childIDs }

                when (it) {
                    is HStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = it.childIDs + collectionChildIDs - childCollectionIDs))
                    }
                    is VStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = it.childIDs + collectionChildIDs - childCollectionIDs))
                    }
                    is ScrollContainer -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = it.childIDs + collectionChildIDs - childCollectionIDs))
                    }
                    is ZStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = it.childIDs + collectionChildIDs - childCollectionIDs))
                    }
                    is Screen -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = it.childIDs + collectionChildIDs - childCollectionIDs))
                    }
                    is Carousel -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = it.childIDs + collectionChildIDs - childCollectionIDs))
                    }
                }
            }
        }
        modifiedNodes.removeAll(modifiedNodes.filterIsInstance<Collection>())

        return modifiedNodes to directCopyIDs
    }

}

internal data class NodesTransformInfo(val nodes: List<Node>, val imagesWithoutSizes: Set<String> = emptySet(), val collectionNodeIDs: List<String>, val swipeToRefresh: Boolean = false, val canCache: Boolean = false)
internal data class NodesForScreenInfo(val nodes: List<Node>, val swipeToRefresh: Boolean = false, val collectionNodeIDs: List<String>, val canCache: Boolean = false)
internal data class RequestedImageDimensions(val url: String, val width: Int, val height: Int)
