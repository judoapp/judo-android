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

package app.judo.sdk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.data.*
import app.judo.sdk.core.data.resolvers.resolveJson
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.extensions.*
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.lang.Keyword
import app.judo.sdk.core.services.DataSourceService
import app.judo.sdk.core.services.ImageService
import app.judo.sdk.ui.events.ExperienceRequested
import app.judo.sdk.ui.layout.NodeTransformationPipeline
import app.judo.sdk.ui.layout.NodesForScreenInfo
import app.judo.sdk.ui.layout.RequestedImageDimensions
import app.judo.sdk.ui.models.ExperienceState
import app.judo.sdk.ui.models.ExperienceState.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private typealias SideEffect = suspend Environment.(
    tree: NodeTree,
    node: Node,
    urlParams: Map<String, String>
) -> Unit

internal class ExperienceViewModel(
    private val environment: Environment,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val nodeTransformationPipeline: NodeTransformationPipeline = NodeTransformationPipeline(
        environment
    ),
    private val jsonResolver: (json: String) -> Any? = ::resolveJson
) : ViewModel() {

    companion object {
        private const val TAG = "ExperienceViewModel"
    }

    private var userInfoOverride: HashMap<String, Any>? = null
    private var experienceKey: String? = null

    private val backingStateFlow = MutableStateFlow<ExperienceState>(Empty)

    private val backingExperienceTree = MutableStateFlow<ExperienceTree?>(null)

    val stateFlow: StateFlow<ExperienceState> = backingStateFlow

    val eventFlow: SharedFlow<Any> = environment.eventBus.eventFlow

    private val backingNodesFlow = MutableSharedFlow<List<Node>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val loadInterpolator: SideEffect = { tree, node, urlParams ->

        if (node is SupportsInterpolation && node !is DataSource) {

            val closestDataSource = tree.findNearestAncestor { it is DataSource } as? DataSource

            val parentDataContext = dataContextOf(
                Keyword.USER.value to getUserInfo(),
                Keyword.DATA.value to closestDataSource?.data,
                Keyword.URL.value to urlParams
            )

            node.interpolator = InterpolatorImpl(
                tokenizer = environment.tokenizer,
                dataContext = parentDataContext,
            )

        }

    }

    fun informScreenViewed(screen: Screen) {
        val experience = backingExperienceTree.value?.experience ?: return

        val dataContext = dataContextOf(
            Keyword.USER.value to getUserInfo(),
            Keyword.DATA.value to actionTargets[screen.id],
            Keyword.URL.value to experience.url?.urlParams()
        )

        val event = Event.ScreenViewed(experience, screen, dataContext)

        publishEvent(event)
    }
    
    private fun getUserInfo(): Map<String, Any> {
        return userInfoOverride ?: environment.profileService.userInfo
    }

    private val loadDataSource: SideEffect = { tree, node, urlParams ->
        if (node is DataSource) {

            val nearestDataSource = tree.findNearestAncestor { it is DataSource } as? DataSource

            val dataContext = dataContextOf(
                Keyword.USER.value to getUserInfo(),
                Keyword.DATA.value to nearestDataSource?.data,
                Keyword.URL.value to urlParams
            )

            val interpolator = InterpolatorImpl(
                tokenizer = environment.tokenizer,
                dataContext = dataContext
            )

            node.headers.forEach {
                it.interpolator = interpolator
            }

            node.interpolator = interpolator

            val json = loadDataSourceJsonFromService(node, environment.dataSourceService)

            node.data = json?.let(jsonResolver)

        }
    }

    private val loadCollectionValues: SideEffect = { tree, node, urlParams ->
        if (node is Collection) {

            val nearestDataSource: DataSource? =
                tree.findNearestAncestor { it is DataSource } as? DataSource

            val dataContext = dataContextOf(
                Keyword.USER.value to getUserInfo(),
                Keyword.DATA.value to nearestDataSource?.data,
                Keyword.URL.value to urlParams
            )

            val items = dataContext.arrayFromKeyPath(node.keyPath)

            node.items = items

        }
    }

    private val limitCollectionValues: SideEffect = { _, node, _ ->

        if (node is Collection) {

            val items = node.items
            val limit = node.limit

            if (limit != null && items != null && items.isNotEmpty()) {

                val index = limit.startAt.dec()

                node.items = items
                    .subList(
                        index, items.lastIndex
                    )
                    .take(limit.show)

            }

        }
    }

    private val resolveCollectionForDirectConditionalValues: SideEffect = { tree, node, urlParams ->

        if (node is Conditional) {
            (tree.parent?.value as? Collection)?.let { collection ->
                collection.items = collection.items?.filter { value ->
                    val dataContext = dataContextOf(
                        Keyword.USER.value to getUserInfo(),
                        Keyword.DATA.value to value,
                        Keyword.URL.value to urlParams
                    )
                    return@filter node.conditions.resolve(dataContext)
                }
            }
        }
    }

    private val filterCollectionValues: SideEffect = { _, node, urlParams ->

        if (node is Collection) {

            if (node.filters.isNotEmpty())
                node.items = node.items?.filter { value ->

                    val dataContext = dataContextOf(
                        Keyword.USER.value to getUserInfo(),
                        Keyword.DATA.value to value,
                        Keyword.URL.value to urlParams
                    )

                    return@filter node.filters.resolve(dataContext)

                }

        }

    }

    private val sortCollectionValues: SideEffect = { _, node, urlParams ->

        if (node is Collection) {

            val userInfo = getUserInfo()

            node.sortDescriptors.forEach { sortDescriptor ->

                node.items = node.items?.sortedWith { o1, o2 ->

                    val v1 = dataContextOf(
                        Keyword.USER.value to userInfo,
                        Keyword.DATA.value to o1,
                        Keyword.USER.value to urlParams
                    ).fromKeyPath(sortDescriptor.keyPath)

                    val v2 = dataContextOf(
                        Keyword.USER.value to userInfo,
                        Keyword.DATA.value to o2,
                        Keyword.USER.value to urlParams
                    ).fromKeyPath(sortDescriptor.keyPath)

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

    }

    init {

        stateFlow
            .filterIsInstance<RetrievedTree>()
            .map { (experienceTree, screenId) ->
                experienceTree to (screenId ?: experienceTree.experience.initialScreenID)
            }
            .onEach { (experienceTree, _) ->
                backingExperienceTree.emit(experienceTree)
            }
            .map { (experienceTree, screenId) ->

                loadDataSourcesForScreen(environment, experienceTree, screenId)

            }
            .flowOn(environment.ioDispatcher)
            .onEach(backingNodesFlow::emit)
            .launchIn(viewModelScope)

    }

    fun nodesFlowForScreen(screenId: String): Flow<NodesForScreenInfo> {
        return backingNodesFlow
            .onSubscription {
                imagesToRequest.clear()
                screenVisited.add(screenId)
            }
            .combine(requestedImageFlow) { nodes, requestedImages -> nodes to requestedImages }
            .flowOn(dispatcher)
            .map { (nodes, requestedImages) ->
                nodeTransformationPipeline.transformScreenNodesForLayout(
                    nodes,
                    requestedImages,
                    screenId,
                    actionTargets[screenId],
                    backingExperienceTree.value
                )
            }
            .flowOn(ioDispatcher)
            .onEach {
                if (it.imagesWithoutSizes.isNotEmpty()) imagesToRequest.addAll(it.imagesWithoutSizes)
            }
            .filter { it.nodes.isNotEmpty() }
            .map {
                NodesForScreenInfo(
                    it.nodes,
                    it.swipeToRefresh,
                    it.collectionNodeIDs,
                    it.canCache
                )
            }
    }

    private val requestedImageFlow = MutableStateFlow<List<RequestedImageDimensions>>(emptyList())

    private val imagesToRequest = mutableSetOf<String>()

    /**
     * Map of ScreenID -> Data Context
     */
    private val actionTargets = mutableMapOf<String, Any?>()

    private val screenVisited = mutableSetOf<String>()
    val screenLayoutCache = mutableMapOf<String, NodesForScreenInfo>()

    fun readyToUpdate(screenId: String, nodesForScreenInfo: NodesForScreenInfo?) {
        nodesForScreenInfo?.let {
            screenLayoutCache.put(screenId, nodesForScreenInfo)
        }
        requestImages(imagesToRequest)
    }

    private fun requestImages(images: Set<String>) {
        viewModelScope.launch(Dispatchers.Main) {
            val imageSet = images.toMutableSet()
            withContext(Dispatchers.Main) {
                imageSet.map {
                    environment.imageService.getImageAsync(ImageService.Request(it)).await()
                }.map {
                    it.drawable?.let { drawable ->
                        RequestedImageDimensions(
                            it.request.url,
                            drawable.intrinsicWidth,
                            drawable.intrinsicHeight
                        )
                    }
                }.run {
                    requestedImageFlow.emit(this.filterNotNull() + requestedImageFlow.value)
                }
            }
        }
    }

    fun refreshNodes(screenId: String) {
        viewModelScope.launch {
            backingExperienceTree.value?.let { tree ->
                val nodes = loadDataSourcesForScreen(
                    environment,
                    experienceTree = tree,
                    screenId = screenId,
                )
                backingNodesFlow.emit(nodes)
            }
        }
    }

    private fun initializeFromUrl(
        originalUrl: String?,
        url: String,
        screenId: String?,
        ignoreCache: Boolean = false
    ) {
        viewModelScope.launch(dispatcher) {

            environment.logger.d(
                tag = TAG,
                data = "Initializing:\nURL: $url\nIgnoring cache: $ignoreCache\nScreen ID: $screenId"
            )

            environment.experienceTreeRepository.retrieveExperienceTree(
                aURL = url,
                ignoreCache = ignoreCache
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        environment.logger.d(
                            tag = TAG,
                            data = "Retrieved experience: ${resource.data.experience.id}\nScreen ID: $screenId"
                        )

                        setExperience(
                            experienceTree = resource.data.apply {
                                experience.url = originalUrl
                            },
                            screenId = screenId
                        )
                    }

                    is Resource.Error -> {
                        environment.logger.e(
                            tag = TAG,
                            message = "Loading experience failed: ${resource.error.message}",
                            error = resource.error
                        )

                        backingStateFlow.value = Error(resource.error)
                    }

                    is Resource.Loading -> {
                        environment.logger.d(
                            tag = TAG,
                            data = "Loading experience from: $url"
                        )

                        backingStateFlow.value = Loading
                    }
                }
            }
        }
    }

    private suspend fun executeSideEffects(
        environment: Environment,
        trunk: NodeTree,
        urlParams: Map<String, String>,
        vararg sideEffects: SideEffect
    ): NodeTree {
        trunk.traverseSuspending { tree ->
            sideEffects.forEach { environment.it(tree, tree.value, urlParams) }
        }
        return trunk
    }

    private suspend fun loadDataSourcesForScreen(
        environment: Environment,
        experienceTree: ExperienceTree,
        screenId: String,
    ): List<Node> {

        val tree: Tree<Node>? = experienceTree.screenNodes[screenId]?.trunk

        val urlParams = experienceTree.experience.url?.urlParams() ?: emptyMap()

        tree?.run {

            val sideEffects = arrayOf(
                loadDataSource,
                loadInterpolator,
                loadCollectionValues,
                filterCollectionValues,
                sortCollectionValues,
                limitCollectionValues,
                resolveCollectionForDirectConditionalValues
            )

            executeSideEffects(
                environment = environment,
                trunk = this,
                urlParams = urlParams,
                sideEffects = sideEffects
            )
        }

        return experienceTree.screenNodes.flatMap { it.value.trunk.flatten() }
    }

    private suspend fun loadDataSourceJsonFromService(
        dataSource: DataSource,
        dataService: DataSourceService
    ): String? {

        val headers =
            dataSource.headers.associate { header ->
                header.interpolatedKey to header.interpolatedValue
            }

        val url = dataSource.interpolatedURL

        val body = dataSource.interpolatedHttpBody

        val result: DataSourceService.Result =
            when (dataSource.httpMethod) {

                HttpMethod.GET -> {
                    dataService.getData(
                        url = url,
                        headers = headers
                    )
                }

                HttpMethod.PUT -> {
                    dataService.putData(
                        url = url,
                        headers = headers,
                        body = body
                    )
                }

                HttpMethod.POST -> {
                    dataService.postData(
                        url = url,
                        headers = headers,
                        body = body
                    )
                }

            }

        return when (result) {

            is DataSourceService.Result.Failure -> {
                environment.logger.e(
                    TAG,
                    result.error.message
                )
                null
            }

            is DataSourceService.Result.Success -> {
                result.body
            }

        }

    }

    private fun initializeFromMemory(experienceKey: String, screenId: String? = null) {
        environment.logger.d(
            tag = TAG,
            data = "Loading experience from memory: $experienceKey\nScreen ID: $screenId"
        )

        val experienceTree = environment.experienceTreeRepository.retrieveTreeById(experienceKey)
        if (experienceTree != null) {
            this.experienceKey = experienceKey
            setExperience(experienceTree, screenId)
        } else {
            backingStateFlow.value = Error(
                error = ExperienceError.ExperienceNotFoundError(
                    message = ErrorMessages.EXPERIENCE_NOT_IN_MEMORY
                )
            )
        }
    }

    fun getNodes(): List<Node> {
        return (backingStateFlow.value as? RetrievedTree)
            ?.experienceTree
            ?.experience
            ?.nodes
            ?: emptyList()
    }

    fun getAppearance(): Appearance {
        return (backingStateFlow.value as? RetrievedTree)
            ?.experienceTree
            ?.experience
            ?.appearance
            ?: Appearance.AUTO
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Node> getNodeByID(nodeID: String): T? {
        return getNodes().find { it.id == nodeID } as? T
    }

    fun onAction(action: Action, screen: Screen, node: Node) {
        val experience = backingExperienceTree.value?.experience ?: return

        val dataContext = dataContextOf(
            Keyword.USER.value to getUserInfo(),
            Keyword.DATA.value to actionTargets[screen.id],
            Keyword.URL.value to experience.url?.urlParams()
        )

        val event = Event.ActionReceived(experience, screen, node, action, dataContext)

        publishEvent(action)
        publishEvent(event)
    }

    fun onEvent(event: Any) {
        environment.logger.d(
            tag = TAG,
            data = "Event received: ${event.javaClass.simpleName}"
        )

        (event as? ExperienceRequested)?.run {
            if (loadFromMemory && experienceKey != null) {
                userInfoOverride = userInfo
                initializeFromMemory(experienceKey, screenId)
            } else {
                experienceURLForRequest?.let { experienceURLForRequest ->
                    userInfoOverride = userInfo
                    initializeFromUrl(event.experienceURL, experienceURLForRequest, screenId)
                }
            }
        } ?: publishEvent(event)
    }

    private fun publishEvent(event: Any) {
        viewModelScope.launch(dispatcher) {
            environment.logger.d(
                tag = TAG,
                data = "Publishing event: ${event.javaClass.simpleName}"
            )

            if (event is Action.PerformSegue && event.data != null) {
                event.data?.let {
                    actionTargets.put(event.screenID, it)
                }
            }

            if (event is Action.PerformSegue && event.screenID !in screenVisited) {
                refreshNodes(event.screenID)
            }

            environment.eventBus.publish(event)
        }
    }

    private fun setExperience(experienceTree: ExperienceTree, screenId: String? = null) {
        environment.logger.d(
            tag = TAG,
            data = "Retrieved experience: ${experienceTree.experience.id}\nScreen ID: $screenId"
        )

        viewModelScope.launch {
            backingStateFlow.emit(RetrievedTree(experienceTree, screenId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        experienceKey?.let(environment.experienceRepository::remove)
    }
}
