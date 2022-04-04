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

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.models.*
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
    private val nodeTransformationPipeline: NodeTransformationPipeline = NodeTransformationPipeline(
        environment
    ),
    private val jsonResolver: (json: String) -> Any? = ::resolveJson
) : ViewModel() {

    companion object {
        private const val TAG = "ExperienceViewModel"
    }

    private var userInfoOverride: HashMap<String, Any>? = null
    private var authorizersOverride: List<Authorizer>? = null
    private var experienceKey: String? = null
    private var isConfigurationChanging = false

    private val backingStateFlow = MutableStateFlow<ExperienceState>(Empty)

    private val backingExperienceTree = MutableStateFlow<ExperienceTree?>(null)

    val stateFlow: StateFlow<ExperienceState> = backingStateFlow

    val eventFlow: SharedFlow<Any> = environment.eventBus.eventFlow

    private val backingNodesFlow = MutableSharedFlow<List<Node>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Any active timers for refreshing polling datasources.
     */
    private val pollingDataSourceTimers: HashSet<CountDownTimer> = HashSet()

    private fun loadInterpolatorEffect(screenId: String): SideEffect = { tree, node, urlParams ->
        val closestDataSource = tree.findNearestAncestor { it is DataSource } as? DataSource

        val previousScreenData = this@ExperienceViewModel.actionTargets[screenId]

        val parentDataContext = dataContextOf(
            Keyword.USER.value to getUserInfo(),
            Keyword.DATA.value to (closestDataSource?.data ?: previousScreenData),
            Keyword.URL.value to urlParams
        )

        if (node is SupportsInterpolation && node !is DataSource) {
            node.interpolator = InterpolatorImpl(
                dataContext = parentDataContext
            )
        }

        if (node is Actionable) {
            val action = node.action
            when(action) {
                is Action.OpenURL -> {
                    action.interpolator = InterpolatorImpl(
                        dataContext = parentDataContext
                    )
                }
                is Action.PresentWebsite -> {
                    action.interpolator = InterpolatorImpl(
                        dataContext = parentDataContext
                    )
                }
            }
        }
    }

    fun informScreenViewed(screen: Screen) {
        val experience = backingExperienceTree.value?.experience ?: return

        val dataContext = dataContextOf(
            Keyword.USER.value to getUserInfo(),
            Keyword.DATA.value to actionTargets[screen.id],
            Keyword.URL.value to experience.urlQueryParameters
        )

        val event = Event.ScreenViewed(
            experience,
            screen,
            dataContext,
            actionTargets[screen.id],
            experience.urlQueryParameters ?: emptyMap(),
            getUserInfo()
        )

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
                dataContext = dataContext
            )

            node.headers.forEach {
                it.interpolator = interpolator
            }

            node.interpolator = interpolator

            val json = loadDataSourceJsonFromService(node, environment.dataSourceService)

            // track some out-of-band state to indicate this data source has been loaded:
            this@ExperienceViewModel.completedDataSources.add(node)

            node.data = json?.let(jsonResolver)

        }
    }

    private val loadCollectionValues: SideEffect = { tree, node, urlParams ->
        if (node is app.judo.sdk.api.models.Collection) {

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

        if (node is app.judo.sdk.api.models.Collection) {

            node.limit()

        }
    }

    private val resolveCollectionForDirectConditionalValues: SideEffect = { tree, node, urlParams ->

        if (node is Conditional) {
            (tree.parent?.value as? app.judo.sdk.api.models.Collection)?.let { collection ->
                collection.items = collection.items?.filter { value ->
                    val dataContext = dataContextOf(
                        Keyword.USER.value to getUserInfo(),
                        Keyword.DATA.value to value,
                        Keyword.URL.value to urlParams
                    )
                    return@filter node.conditions.resolve(dataContext, InterpolatorImpl(
                        dataContext = dataContext
                    ))
                }
            }
        }
    }

    private val filterCollectionValues: SideEffect = { _, node, urlParams ->

        if (node is app.judo.sdk.api.models.Collection) {

            node.filter(getUserInfo(), urlParams)

        }

    }

    private val sortCollectionValues: SideEffect = { _, node, urlParams ->

        if (node is app.judo.sdk.api.models.Collection) {

            val userInfo = getUserInfo()

            node.sort(userInfo, urlParams)
        }

    }

    private fun schedulePollingDataSourcesEffect(screenId: String): SideEffect = { _, node, urlParams ->
        if (node is DataSource) {
            // set up a timer for this node and add it to the set.
            node.pollInterval?.let { pollInterval ->
                val timer = object : CountDownTimer(pollInterval * 1000L, pollInterval * 1000L) {
                    override fun onTick(millisUntilFinished: Long) {
                        /* no-op */
                    }

                    override fun onFinish() {
                        this@ExperienceViewModel.refreshNodes(screenId, true)
                    }
                }.start()

                this@ExperienceViewModel.pollingDataSourceTimers.add(timer)
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
                renderNodes(environment, experienceTree, screenId)
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
            .flowOn(environment.mainDispatcher)
            .map { (nodes, requestedImages) ->
                nodeTransformationPipeline.transformScreenNodesForLayout(
                    nodes = nodes,
                    requestedImages = requestedImages,
                    screenID = screenId,
                    actionTargetData = actionTargets[screenId],
                    experienceTree = backingExperienceTree.value,
                    userInfo = getUserInfo()
                )
            }
            .flowOn(environment.ioDispatcher)
            .onEach {
                if (it.imagesWithoutSizes.isNotEmpty()) imagesToRequest.addAll(it.imagesWithoutSizes)
            }
            .filter { it.nodes.isNotEmpty() }
            .map {
                val nodesForScreenInfo = NodesForScreenInfo(
                    it.nodes,
                    it.swipeToRefresh,
                    it.collectionNodeIDs,
                    it.canCache,
                    // using HashSet() constructor to ensure a copy is made.
                    HashSet(this@ExperienceViewModel.completedDataSources),
                    this@ExperienceViewModel.refreshRequestSequence
                )
                nodesForScreenInfo
            }
    }

    private val requestedImageFlow = MutableStateFlow<List<RequestedImageDimensions>>(emptyList())

    private val imagesToRequest = mutableSetOf<String>()

    // These two bits of state are used to detect when an emitted list of nodes represents new data
    // and should be submitted to layout, allowing refresh to work correctly.
    internal val completedDataSources = mutableSetOf<DataSource>()
    internal var refreshRequestSequence = 0

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
        refreshNodes(screenId, false)
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

    fun refreshNodes(screenId: String, requestedByUser: Boolean = false) {
        viewModelScope.launch {
            if(requestedByUser) {
                this@ExperienceViewModel.completedDataSources.clear()
                this@ExperienceViewModel.refreshRequestSequence++

            }
            backingExperienceTree.value?.let { tree ->
                val nodes = renderNodes(
                    environment,
                    experienceTree = tree,
                    screenId = screenId,
                    isRefresh = true
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
        viewModelScope.launch(environment.mainDispatcher) {

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
                                experience.urlQueryParameters = originalUrl?.urlParams()
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

    private suspend fun renderNodes(
        environment: Environment,
        experienceTree: ExperienceTree,
        screenId: String,
        isRefresh: Boolean = false
    ): List<Node> {

        val tree: Tree<Node>? = experienceTree.screenNodes[screenId]?.trunk

        val urlParams = experienceTree.experience.urlQueryParameters ?: emptyMap()

        // cancel any previous timers.
        this.pollingDataSourceTimers.forEach { it.cancel() }
        this.pollingDataSourceTimers.clear()

        tree?.run {

            val sideEffects = arrayOf(
                loadInterpolatorEffect(screenId),
                loadCollectionValues,
                filterCollectionValues,
                sortCollectionValues,
                limitCollectionValues,
                resolveCollectionForDirectConditionalValues
            )

            val refreshSideEffects = arrayOf(
                loadDataSource,
                loadInterpolatorEffect(screenId),
                loadCollectionValues,
                filterCollectionValues,
                sortCollectionValues,
                limitCollectionValues,
                resolveCollectionForDirectConditionalValues,
                schedulePollingDataSourcesEffect(screenId),
            )

            executeSideEffects(
                environment = environment,
                trunk = this,
                urlParams = urlParams,
                sideEffects = if (isRefresh) refreshSideEffects else sideEffects
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

        val request = URLRequest(
            url,
            dataSource.httpMethod,
            HashMap(headers),
            body
        )

        return when (val result: DataSourceService.Result =
            dataService.performRequest(request, authorizersOverride)) {
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

        viewModelScope.launch(environment.mainDispatcher) {
            val experienceTree =
                environment.experienceTreeRepository.retrieveTreeById(experienceKey)

            val authorizersOverride =
                environment.experienceRepository.retrieveAuthorizersOverrideById(experienceKey)
            val urlQueryParams =
                environment.experienceRepository.retrieveUrlQueryParametersById(experienceKey)
            if (experienceTree != null) {
                experienceTree.experience.urlQueryParameters = urlQueryParams
                this@ExperienceViewModel.experienceKey = experienceKey
                this@ExperienceViewModel.authorizersOverride = authorizersOverride
                this@ExperienceViewModel.setExperience(experienceTree, screenId)
            } else {
                backingStateFlow.value = Error(
                    error = ExperienceError.ExperienceNotFoundError(
                        message = ErrorMessages.EXPERIENCE_NOT_IN_MEMORY
                    )
                )
            }
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

    fun onAction(action: Action, screenID: String, node: Node) {
        val experience = backingExperienceTree.value?.experience ?: return
        (backingExperienceTree.value?.experience?.nodes?.find { it.id == screenID } as? Screen)?.let { screen ->
            val dataContext = dataContextOf(
                Keyword.USER.value to getUserInfo(),
                Keyword.DATA.value to actionTargets[screen.id],
                Keyword.URL.value to experience.urlQueryParameters
            )

            val event = Event.ActionReceived(experience, screen, node, action, dataContext)

            publishEvent(action)
            publishEvent(event)

            if(action is Action.Custom) {
                val customActionEvent = Event.CustomActionActivationInternal(
                    node = node,
                    screen = screen,
                    experience = experience,
                    node.metadata,
                    dataContext.data,
                    dataContext.urlParameters,
                    dataContext.userInfo
                )

                publishEvent(customActionEvent)
            }
        }
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
                    initializeFromUrl(event.experienceURL, experienceURLForRequest, screenId, ignoreCache = event.ignoreCache)
                }
            }
        } ?: publishEvent(event)
    }

    private fun publishEvent(event: Any) {
        viewModelScope.launch(environment.mainDispatcher) {
            environment.logger.d(
                tag = TAG,
                data = "Publishing event: ${event.javaClass.simpleName}"
            )

            if (event is Action.PerformSegue && event.data != null) {
                event.data?.let {
                    actionTargets.put(event.screenID, it)
                }
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

    fun onConfigurationChange() {
        isConfigurationChanging = true
    }

    override fun onCleared() {
        // This ViewModel is destroyed on configuration changes.
        // This is caused by the parent fragment this VM is associated to
        // being destroyed and a new one taking it's place.
        // That problem has not been solved at the moment so this is a workaround
        // for that.
        if (!isConfigurationChanging)
            experienceKey?.let(environment.experienceRepository::remove)
        super.onCleared()
    }
}
