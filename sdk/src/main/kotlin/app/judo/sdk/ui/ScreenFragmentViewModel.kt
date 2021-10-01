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

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.judo.sdk.api.events.Event
import app.judo.sdk.api.models.Screen
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.extensions.*
import app.judo.sdk.core.implementations.TranslatorImpl
import app.judo.sdk.core.struct.TreeNode
import app.judo.sdk.ui.models.messages.GetExperienceForScreenRequest
import app.judo.sdk.ui.state.Renderable
import app.judo.sdk.ui.state.ScreenFragmentState
import app.judo.sdk.ui.state.ScreenFragmentState.Empty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

internal class ScreenFragmentViewModel(
    private val environment: Environment = Environment.current,
    private val preferredLanguagesSupplier: () -> List<String> = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listOf(Locale.getDefault().toLanguageTag())
        } else {
            emptyList()
        }
    }
) : ViewModel() {

    companion object {
        private const val TAG = "ScreenFragmentViewModel"
    }

    private var onViewedHandler: () -> Unit = {}

    private var _state: TreeNode<Renderable>? = null

    private val _viewState = MutableStateFlow<ScreenFragmentState>(Empty)

    val viewState: StateFlow<ScreenFragmentState> = _viewState.asStateFlow()

    fun load(screenID: String) {

        if (viewState.value is ScreenFragmentState.Loaded) return

        environment.logger.v(TAG, "Loading: $screenID")

        _viewState.value = ScreenFragmentState.Loading

        viewModelScope.launch(environment.ioDispatcher) {

            val message = GetExperienceForScreenRequest(
                screenID = screenID,
            )

            environment.logger.v(TAG, "Sending: $message")

            environment.eventBus.publish(message)

            val response = message.response.await()

            val (experience, context, authorizersOverride) = response

            environment.logger.v(TAG, "Received Experience: ${experience.id}")

            val typefaceMap =
                environment.fontResourceService.getTypefacesFor(fonts = experience.fonts)


            val modifiedNodes = experience.nodes.addImplicitStacksForScrollContainers(screenID = screenID)

            val nodesMap = modifiedNodes.associateBy { it.id }

            val screen = nodesMap[screenID] as Screen

            _state = modifiedNodes
                .toModelTree(
                    nodesMap = nodesMap,
                    rootNodeID = screenID
                ).map { modelTree ->
                    modelTree.value.toRenderable()
                }.also { renderTree ->

                    val translator = TranslatorImpl(
                        theTranslationMap = experience.localization,
                        theUsersPreferredLanguagesSupplier = preferredLanguagesSupplier
                    )

                    try {

                        renderTree.loadDataContext(
                            context = context,
                            environment = environment,
                            authorizersOverride = authorizersOverride
                        )

                        renderTree.expandCollections()

                        renderTree.translateText(translator = translator)

                        renderTree.interpolateValues(environment.interpolator)

                        renderTree.pruneConditionals()

                        renderTree.sortCollections()

                        renderTree.limitCollections()

                        renderTree.setTypefaces(typefaceMap)

                        renderTree.setImageGetters(environment.imageService)

                        renderTree.setActionHandlers { node, action, dataContext ->

                            viewModelScope.launch(environment.defaultDispatcher) {

                                val event = Event.ActionReceived(
                                    experience = experience,
                                    screen = screen,
                                    node = node,
                                    action = action,
                                    dataContext = dataContext
                                )

                                environment.eventBus.publish(
                                    event
                                )

                            }

                        }

                        onViewedHandler = {

                            viewModelScope.launch(environment.defaultDispatcher) {

                                val event = Event.ScreenViewed(
                                    experience = experience,
                                    screen = screen,
                                    dataContext = response.dataContext
                                )

                                environment.eventBus.publish(event)
                            }

                        }

                        _viewState.emit(
                            ScreenFragmentState.Loaded(
                                renderTree.toLayoutTree(),
                                experience.appearance
                            )
                        )

                    } catch (cause: Throwable) {

                        _viewState.emit(
                            ScreenFragmentState.Error(cause = cause)
                        )

                    }

                }

        }
    }

    fun onViewed() {
        onViewedHandler()
    }

}
