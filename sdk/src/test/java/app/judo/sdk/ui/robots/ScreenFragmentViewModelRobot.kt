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

package app.judo.sdk.ui.robots

import app.judo.sdk.api.models.Experience
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.dataContextOf
import app.judo.sdk.core.lang.Keyword
import app.judo.sdk.core.robots.AbstractTestRobot
import app.judo.sdk.core.services.ProfileService
import app.judo.sdk.json.FileNames
import app.judo.sdk.ui.ScreenFragmentViewModel
import app.judo.sdk.ui.models.messages.GetExperienceForScreenRequest
import app.judo.sdk.utils.TestJSON
import app.judo.sdk.utils.TestJSONLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScreenFragmentViewModelRobot : AbstractTestRobot() {

    private lateinit var scope: CoroutineScope

    lateinit var experience: Experience

    lateinit var conditionalExperience: Experience

    lateinit var collectionExperience: Experience

    lateinit var typefaceExperience: Experience

    lateinit var navTestExperience: Experience

    lateinit var scrollContainerTestExperience: Experience

    lateinit var viewModel: ScreenFragmentViewModel

    override fun onSetUp() {

        val profileService = backingEnvironment.profileService

        backingEnvironment.profileService = object : ProfileService by profileService {
            override val userInfo: Map<String, Any>
                get() = profileService.userInfo.toMutableMap().apply {
                    put("isPremium", false)
                }.toMap()
        }

        viewModel = ScreenFragmentViewModel(environment = environment) {
            listOf("fr")
        }

        scope = CoroutineScope(testCoroutineDispatcher)

        collectionExperience =
            JsonParser.parseExperience(TestJSONLoader.load(fileName = FileNames.CollectionTestExperience))!!

        typefaceExperience =
            JsonParser.parseExperience(TestJSONLoader.load(fileName = FileNames.TypefaceTestExperience))!!

        experience =
            JsonParser.parseExperience(TestJSONLoader.load(fileName = FileNames.DataSourceExperience))!!

        conditionalExperience = JsonParser.parseExperience(TestJSON.conditional_test_experience)!!

        navTestExperience =
            JsonParser.parseExperience(TestJSONLoader.load(FileNames.NavTestExperience))!!

        scrollContainerTestExperience =
            JsonParser.parseExperience(TestJSONLoader.load(FileNames.ScrollContainerTestExperience))!!

        environment.eventBus.eventFlow.filterIsInstance<GetExperienceForScreenRequest>()
            .onEach { request ->
                if (experience.screenIDs.contains(request.screenID)) {
                    val response = GetExperienceForScreenRequest.Response(
                        experience = experience,
                        dataContext = dataContextOf(
                            Keyword.USER.value to environment.profileService.userInfo
                        )
                    )

                    request.response.complete(response)
                }
            }.launchIn(scope)

        environment.eventBus.eventFlow.filterIsInstance<GetExperienceForScreenRequest>()
            .onEach { request ->
                if (conditionalExperience.screenIDs.contains(request.screenID)) {

                    val response = GetExperienceForScreenRequest.Response(
                        experience = conditionalExperience,
                        dataContext = dataContextOf(
                            Keyword.USER.value to environment.profileService.userInfo
                        )
                    )

                    request.response.complete(response)
                }
            }.launchIn(scope)

        environment.eventBus.eventFlow.filterIsInstance<GetExperienceForScreenRequest>()
            .onEach { request ->
                if (collectionExperience.screenIDs.contains(request.screenID)) {

                    val response = GetExperienceForScreenRequest.Response(
                        experience = collectionExperience,
                        dataContext = dataContextOf(
                            Keyword.USER.value to environment.profileService.userInfo
                        )
                    )

                    request.response.complete(response)
                }
            }.launchIn(scope)

        environment.eventBus.eventFlow.filterIsInstance<GetExperienceForScreenRequest>()
            .onEach { request ->
                if (typefaceExperience.screenIDs.contains(request.screenID)) {

                    val response = GetExperienceForScreenRequest.Response(
                        experience = typefaceExperience,
                        dataContext = dataContextOf(
                            Keyword.USER.value to environment.profileService.userInfo
                        )
                    )

                    request.response.complete(response)
                }
            }.launchIn(scope)

        environment.eventBus.eventFlow.filterIsInstance<GetExperienceForScreenRequest>()
            .onEach { request ->
                if (navTestExperience.screenIDs.contains(request.screenID)) {

                    val response = GetExperienceForScreenRequest.Response(
                        experience = navTestExperience,
                        dataContext = dataContextOf(
                            Keyword.USER.value to environment.profileService.userInfo
                        )
                    )

                    request.response.complete(response)
                }
            }.launchIn(scope)

        environment.eventBus.eventFlow.filterIsInstance<GetExperienceForScreenRequest>()
            .onEach { request ->
                if (scrollContainerTestExperience.screenIDs.contains(request.screenID)) {

                    val response = GetExperienceForScreenRequest.Response(
                        experience = scrollContainerTestExperience,
                        dataContext = dataContextOf(
                            Keyword.USER.value to environment.profileService.userInfo
                        )
                    )

                    request.response.complete(response)
                }
            }.launchIn(scope)

    }

}
