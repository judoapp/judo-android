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

import android.content.Intent
import app.judo.sdk.api.data.UserInfoSupplier
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Visitor
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_KEY
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_URL
import app.judo.sdk.core.environment.Environment.Keys.IGNORE_CACHE
import app.judo.sdk.core.environment.Environment.Keys.LOAD_FROM_MEMORY
import app.judo.sdk.core.environment.Environment.Keys.SCREEN_ID
import app.judo.sdk.core.implementations.EnvironmentImpl
import app.judo.sdk.core.robots.AbstractTestRobot
import app.judo.sdk.ui.ExperienceViewModel
import app.judo.sdk.ui.events.ExperienceRequested
import app.judo.sdk.ui.models.ExperienceState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.junit.Assert

@ExperimentalCoroutinesApi
internal class ExperienceViewModelRobot : AbstractTestRobot() {

    private val scope = CoroutineScope(testCoroutineDispatcher)

    private val experienceStates = mutableListOf<ExperienceState>()

    private val model by lazy {
        ExperienceViewModel(
            environment = environment,
            dispatcher = environment.mainDispatcher,
            ioDispatcher = environment.ioDispatcher,
        )
    }

    private var stateJob: Job? = null

    override fun onSetUp() {
        super.onSetUp()
        stateJob = scope.launch {
            model.stateFlow.collect { judoState ->
                experienceStates += judoState
            }
        }
    }

    override fun onTearDown() {
        super.onTearDown()
        scope.cancel()
    }

    suspend fun assertThatExperienceStateAtPositionEquals(
        position: Int,
        state: ExperienceState,
    ) {
        if (state is ExperienceState.RetrievedTree) {
            delay(500)
            val stateInList = experienceStates[position] as? ExperienceState.RetrievedTree
            val expected = state.experienceTree.experience.id to state.screenId
            val actual = stateInList?.experienceTree?.experience?.id to stateInList?.screenId
            Assert.assertEquals(expected, actual)
        } else {
            Assert.assertEquals(state, experienceStates[position])
        }
    }

    fun initializeExperienceFromURL(url: String) {

        val intent = Intent().apply {
            putExtra(EXPERIENCE_URL, url)
            putExtra(IGNORE_CACHE, true)
        }

        model.onEvent(
            event = ExperienceRequested(
                intent
            )
        )

    }

    fun loadExperienceIntoMemory(experience: Experience) {
        environment.experienceRepository.put(experience)
    }

    fun initializeExperienceFromMemory(judoKey: String, screenId: String? = null) {
        val intent = Intent().apply {
            putExtra(LOAD_FROM_MEMORY, true)
            putExtra(EXPERIENCE_KEY, judoKey)
            putExtra(SCREEN_ID, screenId)
        }

        model.onEvent(
            event = ExperienceRequested(
                intent
            )
        )
    }

    suspend fun inspectExperienceAt(position: Int, visitor: Visitor<*>) {
        delay(2000)
        val experience = (experienceStates[position] as? ExperienceState.RetrievedTree)
            ?.experienceTree?.experience

        experience?.let(visitor::visit)
    }

    fun setUserInfoSupplierTo(supplier: UserInfoSupplier) {
        (environment as? EnvironmentImpl)?.userInfoSupplier = supplier
    }

}
