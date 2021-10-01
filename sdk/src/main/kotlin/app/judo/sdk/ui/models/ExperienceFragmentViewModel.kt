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

package app.judo.sdk.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.models.Action
import app.judo.sdk.api.models.Authorizer
import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.data.DataContext
import app.judo.sdk.core.data.Resource
import app.judo.sdk.core.data.dataContextOf
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.errors.ErrorMessages
import app.judo.sdk.core.extensions.urlParams
import app.judo.sdk.core.lang.Keyword
import app.judo.sdk.ui.events.ExperienceRequested
import app.judo.sdk.ui.models.messages.GetExperienceForScreenRequest
import app.judo.sdk.ui.state.ExperienceFragmentState
import app.judo.sdk.ui.state.ExperienceFragmentState.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class ExperienceFragmentViewModel(
    private val environment: Environment,
) : ViewModel() {

    companion object {
        private const val TAG = "ExperienceFragmentViewModel"
    }

    private var experienceKey: String? = null
    private var originalURL: String? = null
    private var userInfoOverride: DataContext? = null
    private var authorizersOverride: List<Authorizer>? = null
    private val actionTargets = mutableMapOf<String, Any?>()

    private val backingStateFlow = MutableStateFlow<ExperienceFragmentState>(Empty)

    private val backingExperience = MutableStateFlow<Experience?>(null)

    private lateinit var theScreenExperienceRequestHandler: Job
    private lateinit var theActionReceivedEventHandler: Job

    val stateFlow: StateFlow<ExperienceFragmentState> = backingStateFlow

    val eventFlow: SharedFlow<Any> = environment.eventBus.eventFlow

    init {

        stateFlow
            .filterIsInstance<Retrieved>()
            .map { (experience, screenId) ->
                experience to (screenId ?: experience.initialScreenID)
            }
            .onEach { (experience, _) ->
                backingExperience.emit(experience)
            }
            .flowOn(environment.ioDispatcher)
            .launchIn(viewModelScope)

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

            environment.experienceRepository
                .retrieveExperience(
                    aURL = url,
                    ignoreCache = ignoreCache
                )
                .flowOn(environment.ioDispatcher)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            environment.logger.d(
                                tag = TAG,
                                data = "Retrieved experience: ${resource.data.id}\nScreen ID: $screenId"
                            )

                            setExperience(
                                experience = resource.data,
                                screenId = screenId,
                                originalUrl
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

    private fun initializeFromMemory(experienceKey: String, screenId: String? = null) {
        viewModelScope.launch(environment.ioDispatcher) {
            environment.logger.d(
                tag = TAG,
                data = "Loading experience from memory: $experienceKey\nScreen ID: $screenId"
            )

            val experience = environment.experienceRepository.retrieveById(experienceKey)
            authorizersOverride =
                environment.experienceRepository.retrieveAuthorizersOverrideById(experienceKey)
            if (experience != null) {
                this@ExperienceFragmentViewModel.experienceKey = experienceKey
                setExperience(
                    experience = experience,
                    screenId = screenId,
                    authorizerOverride = authorizersOverride
                )
            } else {
                backingStateFlow.value = Error(
                    error = ExperienceError.ExperienceNotFoundError(
                        message = ErrorMessages.EXPERIENCE_NOT_IN_MEMORY
                    )
                )
            }
        }
    }

    private fun setExperience(
        experience: Experience,
        screenId: String? = null,
        originalURL: String? = null,
        authorizerOverride: List<Authorizer>? = null
    ) {
        environment.logger.d(
            tag = TAG,
            data = "Retrieved experience: ${experience.id}\nScreen ID: $screenId"
        )

        this.originalURL = originalURL

        subscribeToActionReceivedEvents()
        subscribeToExperienceRequests()

        viewModelScope.launch {
            backingStateFlow.emit(Retrieved(experience, screenId))
        }
    }

    private fun subscribeToActionReceivedEvents() {
        if (!::theActionReceivedEventHandler.isInitialized)
            theActionReceivedEventHandler =
                eventFlow.filterIsInstance<Event.ActionReceived>()
                    .onEach { event ->
                        if (event.action is Action.PerformSegue) {
                            actionTargets[event.action.screenID] = event.dataContext[Keyword.DATA.value]
                        }
                    }
                    .flowOn(environment.defaultDispatcher)
                    .launchIn(viewModelScope)
    }

    private fun subscribeToExperienceRequests() {
        if (!::theScreenExperienceRequestHandler.isInitialized)
            theScreenExperienceRequestHandler =
                eventFlow.filterIsInstance<GetExperienceForScreenRequest>()
                    .onEach { request ->
                        val currentState = backingStateFlow.value
                        if (currentState is Retrieved) {
                            if (currentState.experience.screenIDs.contains(request.screenID)) {

                                val response = GetExperienceForScreenRequest.Response(
                                    currentState.experience,
                                    dataContext = dataContextOf(
                                        Keyword.USER.value to environment.profileService.userInfo,
                                        Keyword.URL.value to originalURL?.urlParams(),
                                        Keyword.DATA.value to actionTargets[request.screenID]
                                    ),
                                    authorizersOverride = authorizersOverride
                                )

                                request.response.complete(response)
                            }
                        }
                    }
                    .flowOn(environment.defaultDispatcher)
                    .launchIn(viewModelScope)
    }


    fun onEvent(event: ExperienceRequested) {
        environment.logger.d(
            tag = TAG,
            data = "Event received: ${event.javaClass.simpleName}"
        )

        (event as? ExperienceRequested)?.run {

            val theUserInfo = userInfo ?: environment.profileService.userInfo

            if (loadFromMemory && experienceKey != null) {
                userInfoOverride = theUserInfo
                initializeFromMemory(experienceKey, screenId)
            } else {
                experienceURLForRequest?.let { experienceURLForRequest ->
                    userInfoOverride = theUserInfo
                    initializeFromUrl(
                        originalUrl = event.experienceURL,
                        url = experienceURLForRequest,
                        screenId = screenId
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        experienceKey?.let(environment.experienceRepository::remove)
    }
}
