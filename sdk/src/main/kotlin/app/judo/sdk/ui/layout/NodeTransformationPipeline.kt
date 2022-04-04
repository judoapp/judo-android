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

import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.data.*
import app.judo.sdk.core.data.ExperienceTree
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.extensions.*
import app.judo.sdk.core.extensions.findNearestAncestor
import app.judo.sdk.core.extensions.resolve
import app.judo.sdk.core.extensions.traverse
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.lang.Keyword

internal class NodeTransformationPipeline(
    val environment: Environment,
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

    fun transformScreenNodesForLayout(
        nodes: List<Node>,
        requestedImages: List<RequestedImageDimensions>,
        screenID: String,
        actionTargetData: Any? = null,
        experienceTree: ExperienceTree? = null,
        userInfo: Map<String, Any> = environment.profileService.userInfo,
        defaultInterpolator: Interpolator? = null
    ): NodesTransformInfo {
        experienceTree?.experience?.urlQueryParameters?.let {
            urlParams = it
        }
        val screenNodes = filterNodesForScreen(screenID, nodes)
        val screenDirectChildren = (screenNodes.find { it.id == screenID } as? Screen)?.getChildNodeIDs()
        val swipeToRefresh = (screenNodes.any { screenDirectChildren?.contains(it.id) == true && it is ScrollContainer } && screenNodes.any { it is DataSource })

        val canCache = (actionTargetData == null && nodes.any { it is Collection })

        modifyActionsForDataSource(screenNodes, userInfo)
        addDataFromPreviousScreenData(screenID = screenID, targetData = actionTargetData, experienceTree = experienceTree, userInfo)
        val nonCollectionConditionalsResolved = resolveNonCollectionConditionals(screenID, screenNodes, actionTargetData, userInfo, defaultInterpolator)
        val dataSourcesRemoved = removeDataSources(nonCollectionConditionalsResolved)
        val collectionsRemoved = removeCollections(dataSourcesRemoved, screenID, userInfo)
        val conditionalsRemoved = removeConditionals(screenID, collectionsRemoved.first, collectionsRemoved.second)
        val addedImageSizes = addImageSizesForImagesWithoutSize(requestedImages, conditionalsRemoved.first)
        val imageWithoutSizesRemoved = removeImagesWithoutSizes(addedImageSizes)
        val imagesWithoutSizes = getImagesWithoutSizes(addedImageSizes).map { it.interpolatedImageURL }.toSet()
        val interpolationRequiredRemoved = removeAnyNodesRequiringInterpolation(imageWithoutSizesRemoved)
        val implicitStacksAdded = addImplicitStacksForScrollContainers(interpolationRequiredRemoved)
        return NodesTransformInfo(implicitStacksAdded, imagesWithoutSizes, conditionalsRemoved.second, swipeToRefresh, canCache)
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

        val nestedIDs = mutableMapOf<String, List<String>>()
        dataSources.forEach {
            if (it.id in ids) {
                val idToReplace = it.id
                nestedIDs[idToReplace] = getDataSourceChildIDs(dataSources, it.childIDs)
            }
        }
        val modifiedIds = ids.toMutableList()
        nestedIDs.forEach { (idToReplace, ids) ->
            val replaceIndex = modifiedIds.indexOf(idToReplace)
            modifiedIds.removeAt(replaceIndex)
            modifiedIds.addAll(replaceIndex, ids)
        }
        return modifiedIds
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
            is WebView -> {
                when (node.source) {
                    is WebViewSource.HTML -> {
                        false
                    }
                    is WebViewSource.URL -> {
                        node.source.value.requiresInterpolation()
                    }
                }
            }
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

    private fun modifyActionsForDataSource(nodes: List<Node>, userInfo: Map<String, Any>) {
        // sort data sources by depth and attach DAOs to descendants with actions
        val dataSources = nodes.filterIsInstance<DataSource>()

        dataSources.forEach { dataSource ->
            val directChildren = dataSource.childIDs
            val descendantIDs = directChildren.flatMap { directChild -> getChildNodeIDs(directChild, nodes) }
            val dataContext = dataContextOf(
                Keyword.USER.value to userInfo,
                Keyword.DATA.value to dataSource.data,
                Keyword.URL.value to urlParams
            )
            descendantIDs.forEach { descendantID ->
                val descendantNode = nodes.find { it.id == descendantID }!!
                if (descendantNode is Actionable) {
                    descendantNode.action = copyActionToActionWithDataContext(descendantNode.action, dataContext)
                }
            }
        }
    }

    private fun copyActionToActionWithDataContext(action: Action?, dataContext: DataContext): Action? {
        return when (action) {
            is Action.OpenURL -> {
                action.copy().apply {
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }
            }
            is Action.PerformSegue -> {
                action.copy().apply {
                    this.data = dataContext[Keyword.DATA.value]
                }
            }
            is Action.PresentWebsite -> {
                action.copy().apply {
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }
            }
            is Action.Close -> action
            is Action.Custom -> action
            null -> null
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
        userInfo: Map<String, Any>
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

                    if (node is Actionable) {
                        node.action = copyActionToActionWithDataContext(node.action, dataContext)
                    }
                }
            }
        }
    }

    private fun resolveNonCollectionConditionals (
        screenID: String,
        nodes: List<Node>,
        actionTargetData: Any?,
        userInfo: Map<String, Any>,
        defaultInterpolator: Interpolator?
    ): List<Node> {
        if (nodes.any { it is Conditional}) {
            val screenNode: Screen = (nodes.find { it.id == screenID } as Screen?)!!

            val nodeMap = nodes.associateBy { it.id }
            val root = NodeTree(screenNode)
            val childNodes = screenNode.childIDs.mapNotNull { id -> nodeMap[id] }
            val trunk: NodeTree = root.insertNode(nodeMap, childNodes)

            val containersToIDsToRemove = mutableListOf<Pair<String, String>>()

            // removes branches from the tree for conditionals that resolve to false
            val pruned = trunk.prune { tree ->
                val node = tree.value
                if (node is Conditional) {
                    val nearestCollection: Collection? = tree.findNearestAncestor { it is Collection } as? Collection
                    if (nearestCollection == null) {
                        val dataSourceAncestor = (tree.findNearestAncestor { it is DataSource } as? DataSource)

                        val dataContext = if (dataSourceAncestor != null) {
                            dataContextOf(
                                Keyword.USER.value to userInfo,
                                Keyword.DATA.value to dataSourceAncestor.data,
                                Keyword.URL.value to urlParams
                            )
                        } else {
                            dataContextOf(
                                Keyword.USER.value to userInfo,
                                Keyword.DATA.value to actionTargetData,
                                Keyword.URL.value to urlParams
                            )
                        }

                        val resolvedConditions = node.conditions.resolve(dataContext, defaultInterpolator ?: InterpolatorImpl(
                            dataContext = dataContext
                        ))

                        if (!resolvedConditions) {
                            val parentID = tree.parent?.value?.id
                            if (parentID != null) containersToIDsToRemove.add(parentID to node.id)
                        }

                        return@prune resolvedConditions
                    }
                }
                return@prune true
            }
            val prunedList = pruned?.flatten() ?: emptyList()

            val modifiablePrunedList = prunedList.toMutableList()

            // remove conditional ids from parents for false conditionals
            containersToIDsToRemove.forEach { (containerID, conditionalID) ->
                val containerNode = prunedList.find { it.id == containerID }
                containerNode?.let {
                    when(it) {
                        is HStack -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                        is VStack -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                        is ScrollContainer -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                        is ZStack -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                        is Screen -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                        is AppBar -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                        is Carousel -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                        is DataSource -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID).apply {
                                interpolator = it.interpolator
                                data = it.data
                            })
                        }
                        is Conditional -> {
                            modifiablePrunedList.remove(it)
                            modifiablePrunedList.add(it.copy(childIDs = it.childIDs - conditionalID))
                        }
                    }
                }
            }

            return modifiablePrunedList
        }
        return nodes
    }

    private fun getConditionalChildIDs(conditionals: List<Conditional>, ids: List<String>): MutableList<String> {

        val nestedIDs = mutableMapOf<String, List<String>>()
        conditionals.forEach {
            if (it.id in ids) {
                val idToReplace = it.id
                nestedIDs[idToReplace] = getConditionalChildIDs(conditionals, it.childIDs)
            }
        }
        val modifiedIds = ids.toMutableList()
        nestedIDs.forEach { (idToReplace, ids) ->
            val replaceIndex = modifiedIds.indexOf(idToReplace)
            modifiedIds.removeAt(replaceIndex)
            modifiedIds.addAll(replaceIndex, ids)
        }
        return modifiedIds
    }

    private fun removeConditionals(
        screenID: String,
        nodes: List<Node>,
        collectionChildIDs: List<String>,
    ): Pair<List<Node>, List<String>> {
        if (nodes.any { it is Conditional }) {
            val screenNode: Screen = (nodes.find { it.id == screenID } as Screen?)!!

            val modifiedNodes = nodes.toMutableList()
            val conditionals = nodes.filterIsInstance<Conditional>()
            val conditionalIDs = conditionals.map { it.id }

            val modifiedCollectionChildIDs = collectionChildIDs.toMutableList()

            conditionalIDs.forEach { conditionalID ->
                if (conditionalID in collectionChildIDs) {
                    val conditionalChildIDs = getConditionalChildIDs(conditionals, conditionals.find { it.id == conditionalID }?.childIDs ?: emptyList())
                    val collectionChildIndex = modifiedCollectionChildIDs.indexOf(conditionalID)
                    modifiedCollectionChildIDs.addAll(collectionChildIndex, conditionalChildIDs)
                    modifiedCollectionChildIDs.remove(conditionalID)
                }
            }

            nodes.forEach {
                if (it is NodeContainer
                    && conditionalIDs.any { conditionalID -> conditionalID in it.getChildNodeIDs() }
                    && it !in conditionals
                ) {
                    val childIDs = it.getChildNodeIDs()
                    val theConditionalsChildIDs = getConditionalChildIDs(conditionals, childIDs)

                    when (it) {
                        is HStack -> {
                            modifiedNodes.remove(it)
                            modifiedNodes.add(it.copy(childIDs = theConditionalsChildIDs - conditionalIDs))
                        }
                        is VStack -> {
                            modifiedNodes.remove(it)
                            modifiedNodes.add(it.copy(childIDs = theConditionalsChildIDs - conditionalIDs))
                        }
                        is ScrollContainer -> {
                            modifiedNodes.remove(it)
                            modifiedNodes.add(it.copy(childIDs = theConditionalsChildIDs - conditionalIDs))
                        }
                        is ZStack -> {
                            modifiedNodes.remove(it)
                            modifiedNodes.add(it.copy(childIDs = theConditionalsChildIDs - conditionalIDs))
                        }
                        is Screen -> {
                            modifiedNodes.remove(it)
                            modifiedNodes.add(it.copy(childIDs = theConditionalsChildIDs - conditionalIDs))
                        }
                        is Carousel -> {
                            modifiedNodes.remove(it)
                            modifiedNodes.add(it.copy(childIDs = theConditionalsChildIDs - conditionalIDs))
                        }
                        is AppBar -> {
                            modifiedNodes.remove(it)
                            modifiedNodes.add(it.copy(childIDs = theConditionalsChildIDs - conditionalIDs))
                        }
                    }
                }
            }
            modifiedNodes.removeAll(conditionals)

            return modifiedNodes to modifiedCollectionChildIDs
        }
        return nodes to collectionChildIDs
    }

    private fun copy(
        data: Any?,
        baseID: String,
        nodeToCopy: Node,
        nodes: List<Node>,
        userInfo: Map<String, Any>
    ): List<Node> {

        val dataContext = dataContextOf(
            Keyword.USER.value to userInfo,
            Keyword.DATA.value to data,
            Keyword.URL.value to urlParams
        )

        return when (nodeToCopy) {
            is Icon -> {
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = copyActionToActionWithDataContext(nodeToCopy.action, dataContext)
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
                val action = copyActionToActionWithDataContext(nodeToCopy.action, dataContext)
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
                val action = copyActionToActionWithDataContext(nodeToCopy.action, dataContext)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action,
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
                if (nodeToCopy.conditions.resolve(dataContext)) {
                    val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                    val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes, userInfo) }
                    val childIDs = children.map { "${baseID}-${it.id}" }

                    val copiedNode = nodeToCopy.copy(
                        id = "${baseID}-${nodeToCopy.id}",
                        childIDs = childIDs,
                    )

                    listOf(copiedNode) + copiedDescendants
                } else {
                    listOf()
                }
            }
            is Collection -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }

                val copiedChildren = mutableListOf<Node>()

                // load
                nodeToCopy.items = dataContext.arrayFromKeyPath(nodeToCopy.keyPath)

                nodeToCopy.filter(userInfo, urlParams)

                nodeToCopy.sort(userInfo, urlParams)

                nodeToCopy.limit()

                val directCopiedIDs = mutableListOf<String>()

                nodeToCopy.items?.forEachIndexed { index, nodeToCopyData ->
                    children.forEach { collectionChild ->
                        val copies = copy(
                            nodeToCopyData,
                            "${baseID}-${nodeToCopy.id}-${index}",
                            collectionChild,
                            nodes,
                            userInfo
                        )
                        copiedChildren.addAll(copies)
                        copies.firstOrNull()?.id?.let { directCopiedIDs.add(it) }
                    }
                }

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    childIDs = directCopiedIDs,
                )

                listOf(copiedNode) + copiedChildren
            }
            is ZStack -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes, userInfo) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = copyActionToActionWithDataContext(nodeToCopy.action, dataContext)

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
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes, userInfo) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = copyActionToActionWithDataContext(nodeToCopy.action, dataContext)

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
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes, userInfo) }
                listOf(nodeToCopy.copy(id = "${baseID}-${nodeToCopy.id}", childIDs = copiedDescendants.map { it.id })) + copiedDescendants
            }
            is ScrollContainer -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes, userInfo) }
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
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes, userInfo) }
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
                val action = copyActionToActionWithDataContext(nodeToCopy.action, dataContext)

                val copiedNode = nodeToCopy.copy(
                    id = "${baseID}-${nodeToCopy.id}",
                    background = backgroundNode,
                    overlay = overlayNode,
                    mask = mask,
                    action = action
                ).apply {
                    translator = nodeToCopy.translator
                    typeface = nodeToCopy.typeface
                    interpolator = InterpolatorImpl(dataContext = dataContext)
                }

                listOf(copiedNode)
            }
            is HStack -> {
                val children = nodeToCopy.getChildNodeIDs().mapNotNull { childID -> nodes.find { node -> childID == node.id } }
                val copiedDescendants = children.flatMap { copy(data, baseID, it, nodes, userInfo) }
                val childIDs = children.map { "${baseID}-${it.id}" }
                val backgroundNode = copyBackground("${baseID}-${nodeToCopy.id}", nodeToCopy.background)
                val overlayNode = copyOverlay("${baseID}-${nodeToCopy.id}", nodeToCopy.overlay)
                val mask = copyMask("${baseID}-${nodeToCopy.id}", nodeToCopy.mask)
                val action = copyActionToActionWithDataContext(nodeToCopy.action, dataContext)

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
            else -> throw IllegalStateException("${nodeToCopy.typeName}")
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

    private fun getFlattenedChildIDs(nodeID: String, nodes: Map<String, Node>): List<String> {
        return when(val node = nodes[nodeID]) {
            is NodeContainer -> listOf(node.id) + node.getChildNodeIDs().flatMap { getFlattenedChildIDs(it, nodes) }
            is Node -> listOf(node.id)
            else -> emptyList()
        }
    }

    private fun removeCollections(nodes: List<Node>, screenNodeID: String, userInfo: Map<String, Any>): Pair<List<Node>, List<String>> {
        // remove collections by copying the children for each DAO and attaching the children to
        // the collection parent
        val modifiedNodes = nodes.toMutableList()
        val collections = nodes.filterIsInstance<Collection>()

        if (collections.isEmpty()) return modifiedNodes to emptyList()

        val collectionIDs = collections.map { it.id }
        val nodesWithoutCarouselAncestors = getNodeIDsWithoutCarouselAncestors(screenNodeID, nodes)

        // gather collections with collection ancestors
        val collectionsWithCollectionAncestors = mutableListOf<String>()
        val nodesMap = nodes.associateBy { it.id }
        collections.forEach {
            val flattenedChildIDs = it.getChildNodeIDs().flatMap { childID -> getFlattenedChildIDs(childID, nodesMap) }
            collectionsWithCollectionAncestors.addAll(
                flattenedChildIDs.filter { flattenedChildID -> flattenedChildID in collectionIDs }
            )
        }

        // copy nodes in collection
        collections.filter { it.id !in collectionsWithCollectionAncestors }.forEach {
            val collectionChildIDs = it.childIDs

            val collectionChildren = collectionChildIDs.mapNotNull { collectionChildID -> nodesMap[collectionChildID] }
            val copiedNodes = mutableListOf<Node>()
            val directCopiedNodeID = mutableListOf<String>()

            it.items?.forEachIndexed { index, data ->
                collectionChildren.forEach { collectionChild ->
                    val copies = copy(
                        data,
                        "${it.id}-${index}",
                        collectionChild,
                        modifiedNodes,
                        userInfo
                    )
                    copies.firstOrNull()?.id?.let { directCopiedNodeID.add(it) }
                    copiedNodes.addAll(copies)
                }
            }

            modifiedNodes.addAll(copiedNodes)
            modifiedNodes.remove(it)
            modifiedNodes.removeAll(collectionChildren)

            // remove all direct copied nodes
            val copiedNodeIDsToRemove = collectionChildIDs.flatMap { collectionChildID -> getFlattenedChildIDs(collectionChildID, nodesMap) }
            modifiedNodes.removeIf { it.id in copiedNodeIDsToRemove }

            val collection = it.copy(childIDs = directCopiedNodeID - collectionChildIDs)
            modifiedNodes.add(collection)
        }

        val modifiedCollections = modifiedNodes.filterIsInstance<Collection>()
        // this is in order to determine which collection child nodes can be lazily loaded

        val directCopyIDs = modifiedCollections.filter {
            it.id in nodesWithoutCarouselAncestors && it.id in collectionIDs && it.id !in collectionsWithCollectionAncestors
        }.flatMap {
            getCollectionChildIDs(modifiedCollections, it.childIDs)
        }

        // get new ids because collections might have generated new collections
        val modifiedCollectionIDs = modifiedCollections.map { it.id }

        // remove collections and attach collection child nodes to collection parent
        modifiedNodes.toMutableList().forEach {
            if (it is NodeContainer && it !in modifiedCollections && it.getChildNodeIDs().any { childNodeID -> childNodeID in modifiedCollectionIDs }) {

                val childIDs = (modifiedNodes.find { modifiedNode -> modifiedNode.id == it.id } as? NodeContainer)?.getChildNodeIDs()
                val newChildIDs = getCollectionChildIDs(modifiedCollections, childIDs ?: emptyList())

                when (it) {
                    is HStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                    is VStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                    is ScrollContainer -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                    is ZStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                    is Screen -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                    is Carousel -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                    is Conditional -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                    is Collection -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = newChildIDs))
                    }
                }
            }
        }
        modifiedNodes.removeAll(modifiedNodes.filterIsInstance<Collection>())

        return modifiedNodes to directCopyIDs
    }

    private fun getCollectionChildIDs(collections: List<Collection>, ids: List<String>): MutableList<String> {

        val nestedIDs = mutableMapOf<String, List<String>>()
        collections.forEach {
            if (it.id in ids) {
                val idToReplace = it.id
                nestedIDs[idToReplace] = getCollectionChildIDs(collections, it.childIDs)
            }
        }
        val modifiedIds = ids.toMutableList()
        nestedIDs.forEach { (idToReplace, ids) ->
            val replaceIndex = modifiedIds.indexOf(idToReplace)
            modifiedIds.removeAt(replaceIndex)
            modifiedIds.addAll(replaceIndex, ids)
        }
        return modifiedIds
    }


}

internal data class NodesTransformInfo(val nodes: List<Node>, val imagesWithoutSizes: Set<String> = emptySet(), val collectionNodeIDs: List<String>, val swipeToRefresh: Boolean = false, val canCache: Boolean = false)

internal data class NodesForScreenInfo(
    val nodes: List<Node>,
    val swipeToRefresh: Boolean = false,
    val collectionNodeIDs: List<String>,
    val canCache: Boolean = false,

    // By including these two details, then when NodesForScreenInfo values are compared
    // by distinctUntilChanged() in ScreenFragment, refreshes can be properly handled and not
    // erroneously screened out.
    val completedDataSources: Set<DataSource>,
    val refreshRequestSequenceNumber: Int
)

internal data class RequestedImageDimensions(val url: String, val width: Int, val height: Int)
