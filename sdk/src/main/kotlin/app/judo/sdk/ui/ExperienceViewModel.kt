package app.judo.sdk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.*
import app.judo.sdk.core.data.JsonDAO
import app.judo.sdk.core.data.JsonDAOImpl
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.services.DataSourceService
import app.judo.sdk.core.utils.ParentChildExtractor
import app.judo.sdk.core.utils.ScreenInterpolatorLoader
import app.judo.sdk.core.utils.SourceAndParent
import app.judo.sdk.ui.events.ExperienceRequested
import app.judo.sdk.ui.layout.ExperienceNodeTransformer
import app.judo.sdk.ui.models.ExperienceState
import app.judo.sdk.ui.models.ExperienceState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

internal class ExperienceViewModel(
    private val environment: Environment,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val experienceNodeTransformer: ExperienceNodeTransformer = ExperienceNodeTransformer(),
    private val jsonDAOSupplier: (json: String) -> JsonDAO = { json -> JsonDAOImpl(json = json) }
) : ViewModel() {

    companion object {
        private const val TAG = "ExperienceViewModel"
    }

    private val backingStateFlow = MutableStateFlow<ExperienceState>(Empty)

    val stateFlow: StateFlow<ExperienceState> = backingStateFlow

    val eventFlow: SharedFlow<Any> = environment.eventBus.eventFlow

    private val backingNodesFlow = MutableSharedFlow<List<Node>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val dataLoaderJob = stateFlow.filterIsInstance<Retrieved>().map { (experience, screenId) ->
        loadDataSourcesForScreen(
            experience = experience,
            screenId = screenId ?: experience.initialScreenID,
        )
    }.onEach(backingNodesFlow::emit).launchIn(viewModelScope)

    fun nodesFlowForScreen(screenId: String): Flow<List<Node>> {
        return backingNodesFlow.map { nodes -> experienceNodeTransformer.transformScreenNodesForLayout(screenId, nodes) }.distinctUntilChanged()
    }

    fun refreshNodes(screenId: String) {
        viewModelScope.launch {
            (stateFlow.value as? Retrieved)?.experience?.let { experience ->

                val nodes = loadDataSourcesForScreen(
                    experience = experience,
                    screenId = screenId,
                )
                backingNodesFlow.emit(nodes)
            }
        }
    }

    private fun initializeFromUrl(url: String, screenId: String?, ignoreCache: Boolean = false) {
        viewModelScope.launch(dispatcher) {

            environment.logger.d(
                tag = TAG,
                data = "Initializing:\nURL: $url\nIgnoring cache: $ignoreCache\nScreen ID: $screenId"
            )

            environment.experienceRepository.retrieveExperience(
                aURL = url,
                ignoreCache = ignoreCache
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        environment.logger.d(
                            tag = TAG,
                            data = "Retrieved experience: ${resource.data.id}\nScreen ID: $screenId"
                        )

                        val experience = resource.data

                        launch {
                            loadDataSourcesForScreen(
                                experience = experience,
                                screenId = screenId ?: experience.initialScreenID
                            )
                        }

                        setExperience(
                            experience = experience,
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

    private suspend fun loadDataSourcesForScreen(experience: Experience, screenId: String) : List<Node> {
       return withContext(ioDispatcher) {

            try {

                val dataService = environment.dataSourceService

                val extractor = ParentChildExtractor(experience)

                val screen = experience.nodes<Screen>().first { it.id == screenId }

                val dataSourcesAndParents: List<SourceAndParent> = extractor.extract(screen)

                dataSourcesAndParents.map { (dataSource, parent) ->
                    async {

                        if (parent == null) {

                            loadDataSource(dataSource, dataService)

                        } else {

                            loadDataSource(parent, dataService)

                            val interpolator = InterpolatorImpl(
                                jsonDAO = parent.jsonDAO,
                                loggerSupplier = {
                                    environment.logger
                                },
                                userDataSupplier = {
                                    environment.userDataSupplier.supplyUserData()
                                }
                            )

                            dataSource.interpolator = interpolator

                            dataSource.headers.forEach {
                                it.interpolator = interpolator
                            }

                            loadDataSource(dataSource, dataService)

                        }

                    }

                }.awaitAll()

                ScreenInterpolatorLoader(
                    experience = experience,
                    screenId = screenId,
                    loggerSupplier = {
                        environment.logger
                    },
                    userDataSupplier = {
                        environment.userDataSupplier.supplyUserData()
                    }
                ).load()

                experience.nodes
            } catch (error: Throwable) {
                environment.logger.e(TAG, null, error)
                emptyList()
            }

            experience.nodes

        }
    }

    private suspend fun loadDataSource(
        dataSource: DataSource,
        dataService: DataSourceService
    ) {

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

        val dao: JsonDAO? = when (result) {

            is DataSourceService.Result.Failure -> {
                environment.logger.e(
                    TAG,
                    result.error.message
                )
                null
            }

            is DataSourceService.Result.Success -> {
                jsonDAOSupplier(result.body)
            }

        }

        dataSource.apply {
            jsonDAO = dao
        }

    }

    private fun initializeFromMemory(experienceKey: String, screenId: String? = null) {
        environment.logger.d(
            tag = TAG,
            data = "Loading experience from memory: $experienceKey\nScreen ID: $screenId"
        )

        val experience = environment.experienceRepository.retrieveById(experienceKey)
        if (experience != null) {
            setExperience(experience, screenId)
        } else {
            backingStateFlow.value = Error(
                error = ExperienceError.ExperienceNotFoundError(
                    message = ErrorMessages.EXPERIENCE_NOT_IN_MEMORY
                )
            )
        }
    }

    fun getNodes(): List<Node> {
        return (backingStateFlow.value as? Retrieved)?.experience?.nodes ?: emptyList()
    }

    fun getAppearance(): Appearance {
        return (backingStateFlow.value as? Retrieved)?.experience?.appearance ?: Appearance.AUTO
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Node> getNodeByID(nodeID: String): T {
        return getNodes().find { it.id == nodeID } as T
    }

    fun onEvent(event: Any) {

        environment.logger.i(
            tag = TAG,
            data = "Event received: $event"
        )

        (event as? ExperienceRequested)?.run {
            if (loadFromMemory && experienceKey != null) {
                initializeFromMemory(experienceKey, screenId)
            } else {
                experienceURLForRequest?.let {
                    initializeFromUrl(it, screenId)
                }
            }
        } ?: publishEvent(event)

    }

    private fun publishEvent(event: Any) {
        viewModelScope.launch(dispatcher) {
            environment.logger.i(
                tag = TAG,
                data = "Publishing event: $event"
            )

            environment.eventBus.publish(event)

            if (event is Action.PerformSegue) refreshNodes(event.screenID)
        }
    }

    private fun setExperience(experience: Experience, screenId: String? = null) {
        environment.logger.d(
            tag = TAG,
            data = "Retrieved experience: ${experience.id}\nScreen ID: $screenId"
        )

        val modifiedExperienceForLayout =
            experienceNodeTransformer.transformExperienceNodesForLayout(experience)

        ScreenInterpolatorLoader(
            experience = experience,
            screenId = screenId ?: experience.initialScreenID,
            loggerSupplier = {
                environment.logger
            },
            userDataSupplier = {
                environment.userDataSupplier.supplyUserData()
            }
        ).load()

        viewModelScope.launch {
            backingStateFlow.emit(Retrieved(modifiedExperienceForLayout, screenId))
        }
    }


}