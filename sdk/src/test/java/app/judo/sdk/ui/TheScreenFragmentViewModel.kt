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

import android.graphics.Typeface
import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.ui.robots.ScreenFragmentViewModelRobot
import app.judo.sdk.ui.state.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TheScreenFragmentViewModel : AbstractRobotTest<ScreenFragmentViewModelRobot>() {

    override fun robotSupplier(): ScreenFragmentViewModelRobot {
        return ScreenFragmentViewModelRobot()
    }

    private val viewModel get() = robot.viewModel

    private val experience get() = robot.experience

    private val conditionalExperience get() = robot.conditionalExperience
    private val collectionExperience get() = robot.collectionExperience
    private val typefaceExperience get() = robot.typefaceExperience
    private val navTestExperience get() = robot.navTestExperience
    private val scrollContainerTestExperience get() = robot.scrollContainerTestExperience

    @Test
    fun `Start in an Empty state`() = runBlocking(robot.testCoroutineDispatcher) {
        // Arrange
        val expected = ScreenFragmentState.Empty

        // Act
        val actual = viewModel.viewState.value

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Moves to Loading state when load()`() = runBlocking(robot.testCoroutineDispatcher) {
        // Arrange
        val expected = ScreenFragmentState.Loading

        // Act
        viewModel.load(experience.initialScreenID)

        val actual = viewModel.viewState.value

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Emits sourced, translated, and interpolated ViewStates`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = "Bonjour, Evan"

            val state: CompletableDeferred<ScreenFragmentState> = CompletableDeferred()

            // Act
            viewModel.viewState.take(3).onEach {
                if (it is ScreenFragmentState.Loaded || it is ScreenFragmentState.Error)
                    state.complete(it)
                println("ACTUAL STATE: $it")
            }.launchIn(this)

            viewModel.load(experience.initialScreenID)

            var actual = ""

            val fragmentState = state.await()

            if (fragmentState is ScreenFragmentState.Error)
                throw fragmentState.cause

            if (fragmentState is ScreenFragmentState.Loaded)
                fragmentState.stateTree.forEachValue { viewState ->
                    if (viewState is TextViewState && viewState.text == expected)
                        actual = viewState.text
                }

            // Assert
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `Adds Implicit Stacks For ScrollContainers`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = 2

            val state: CompletableDeferred<ScreenFragmentState> = CompletableDeferred()

            // Act
            viewModel.viewState.take(3).onEach {
                if (it is ScreenFragmentState.Loaded || it is ScreenFragmentState.Error)
                    state.complete(it)
                println("ACTUAL STATE: $it")
            }.launchIn(this)

            viewModel.load(scrollContainerTestExperience.initialScreenID)

            val states = mutableListOf<ViewState>()

            val fragmentState = state.await()

            if (fragmentState is ScreenFragmentState.Error)
                throw fragmentState.cause

            if (fragmentState is ScreenFragmentState.Loaded)
                fragmentState.stateTree.forEachValue { viewState ->
                    println(viewState::class.java.simpleName)
                    if (viewState is HStackViewState)
                        states += viewState
                    if (viewState is VStackViewState)
                        states += viewState
                }

            val actual = states.size

            // Assert
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `Prunes Conditionals`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = "Hello User!"

            val state: CompletableDeferred<ScreenFragmentState> = CompletableDeferred()

            // Act
            viewModel.viewState.take(3).onEach {
                if (it is ScreenFragmentState.Loaded || it is ScreenFragmentState.Error)
                    state.complete(it)
                println("ACTUAL STATE: $it")
            }.launchIn(this)

            viewModel.load(conditionalExperience.initialScreenID)

            var actual = ""

            val fragmentState = state.await()

            if (fragmentState is ScreenFragmentState.Error)
                throw fragmentState.cause

            if (fragmentState is ScreenFragmentState.Loaded)
                fragmentState.stateTree.forEachValue { viewState ->
                    if (viewState is TextViewState)
                        actual = viewState.text
                }

            // Assert
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `Limits Collections`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = 3

            val state: CompletableDeferred<ScreenFragmentState> = CompletableDeferred()

            // Act
            viewModel.viewState.take(3).onEach {
                if (it is ScreenFragmentState.Loaded || it is ScreenFragmentState.Error)
                    state.complete(it)
                println("ACTUAL STATE: $it")
            }.launchIn(this)

            viewModel.load(collectionExperience.initialScreenID)

            val values = mutableListOf<String>()

            val fragmentState = state.await()

            if (fragmentState is ScreenFragmentState.Error)
                throw fragmentState.cause

            if (fragmentState is ScreenFragmentState.Loaded)
                fragmentState.stateTree.forEachValue { viewState ->
                    if (viewState is TextViewState)
                        values += viewState.text
                }

            val actual = values.size

            // Assert
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `Sorts Collections`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = listOf(
                "Hello Charles",
                "Hello Eve",
                "Hello Emma",
            )

            val state: CompletableDeferred<ScreenFragmentState> = CompletableDeferred()

            // Act
            viewModel.viewState.take(3).onEach {
                if (it is ScreenFragmentState.Loaded || it is ScreenFragmentState.Error)
                    state.complete(it)
                println("ACTUAL STATE: $it")
            }.launchIn(this)

            viewModel.load(collectionExperience.initialScreenID)

            val actual = mutableListOf<String>()

            val fragmentState = state.await()

            if (fragmentState is ScreenFragmentState.Error)
                throw fragmentState.cause

            if (fragmentState is ScreenFragmentState.Loaded)
                fragmentState.stateTree.forEachValue { viewState ->
                    if (viewState is TextViewState)
                        actual += viewState.text
                }

            // Assert
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `Sets Typefaces`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = 6

            val state: CompletableDeferred<ScreenFragmentState> = CompletableDeferred()

            // Act
            viewModel.viewState.take(3).onEach {
                if (it is ScreenFragmentState.Loaded || it is ScreenFragmentState.Error)
                    state.complete(it)
                println("ACTUAL STATE: $it")
            }.launchIn(this)

            viewModel.load(typefaceExperience.initialScreenID)

            val faces = mutableListOf<Typeface>()

            val fragmentState = state.await()

            if (fragmentState is ScreenFragmentState.Error)
                throw fragmentState.cause

            if (fragmentState is ScreenFragmentState.Loaded)
                fragmentState.stateTree.forEachValue { viewState ->
                    if (viewState is TextViewState)
                        viewState.typeface?.let {
                            faces += it
                        }
                }

            val actual = faces.size

            // Assert
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `Sets Action Handlers`() =
        runBlocking(robot.testCoroutineDispatcher) {
            // Arrange
            val expected = 2

            val state: CompletableDeferred<ScreenFragmentState> = CompletableDeferred()

            // Act
            viewModel.viewState.take(3).onEach {
                if (it is ScreenFragmentState.Loaded || it is ScreenFragmentState.Error)
                    state.complete(it)
                println("ACTUAL STATE: $it")
            }.launchIn(this)

            viewModel.load(navTestExperience.initialScreenID)

            val actions = mutableListOf<() -> Unit>()

            val fragmentState = state.await()

            if (fragmentState is ScreenFragmentState.Error)
                throw fragmentState.cause

            if (fragmentState is ScreenFragmentState.Loaded)
                fragmentState.stateTree.forEachValue { viewState ->
                    if (viewState is TextViewState)
                        viewState.action?.let {
                            actions += it
                        }
                }

            val actual = actions.size

            // Assert
            Assert.assertEquals(expected, actual)
        }

}