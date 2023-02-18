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

package app.judo.sdk.compose.vendor.accompanist.navigation_animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

/**
 * Navigator that navigates through [Composable]s. Every destination using this Navigator must
 * set a valid [Composable] by setting it directly on an instantiated [Destination] or calling
 * [composable].
 */
@ExperimentalAnimationApi
@Navigator.Name("animatedComposable")
public class AnimatedComposeNavigator : Navigator<AnimatedComposeNavigator.Destination>() {
    internal val transitionsInProgress get() = state.transitionsInProgress

    internal val backStack get() = state.backStack

    internal val isPop = mutableStateOf(false)

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.forEach { entry ->
            state.pushWithTransition(entry)
        }
        isPop.value = false
    }

    override fun createDestination(): Destination {
        return Destination(this, content = { })
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
        isPop.value = true
    }

    internal fun markTransitionComplete(entry: NavBackStackEntry) {
        state.markTransitionComplete(entry)
    }

    /**
     * NavDestination specific to [AnimatedComposeNavigator]
     */
    @ExperimentalAnimationApi
    @NavDestination.ClassType(Composable::class)
    public class Destination(
        navigator: AnimatedComposeNavigator,
        internal val content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) : NavDestination(navigator)

    internal companion object {
        internal const val NAME = "animatedComposable"
    }
}
