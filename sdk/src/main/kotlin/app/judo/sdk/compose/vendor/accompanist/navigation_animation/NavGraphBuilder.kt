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

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import androidx.navigation.get

/**
 * Add the [Composable] to the [NavGraphBuilder]
 *
 * @param route route for the destination
 * @param arguments list of arguments to associate with destination
 * @param deepLinks list of deep links to associate with the destinations
 * @param enterTransition callback to determine the destination's enter transition
 * @param exitTransition callback to determine the destination's exit transition
 * @param popEnterTransition callback to determine the destination's popEnter transition
 * @param popExitTransition callback to determine the destination's popExit transition
 * @param content composable for the destination
 */
@ExperimentalAnimationApi
public fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (
        AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = enterTransition,
    popExitTransition: (
        AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = exitTransition,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    addDestination(
        AnimatedComposeNavigator.Destination(
            provider[AnimatedComposeNavigator::class],
            content
        ).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
            enterTransition?.let { enterTransitions[route] = enterTransition }
            exitTransition?.let { exitTransitions[route] = exitTransition }
            popEnterTransition?.let { popEnterTransitions[route] = popEnterTransition }
            popExitTransition?.let { popExitTransitions[route] = popExitTransition }
        }
    )
}

/**
 * Construct a nested [NavGraph]
 *
 * @param startDestination the starting destination's route for this NavGraph
 * @param route the destination's unique route
 * @param arguments list of arguments to associate with destination
 * @param deepLinks list of deep links to associate with the destinations
 * @param enterTransition callback to define enter transitions for destination in this NavGraph
 * @param exitTransition callback to define exit transitions for destination in this NavGraph
 * @param popEnterTransition callback to define pop enter transitions for destination in this
 * NavGraph
 * @param popExitTransition callback to define pop exit transitions for destination in this NavGraph
 * @param builder the builder used to construct the graph
 *
 * @return the newly constructed nested NavGraph
 */
@ExperimentalAnimationApi
public fun NavGraphBuilder.navigation(
    startDestination: String,
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (
        AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = enterTransition,
    popExitTransition: (
        AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = exitTransition,
    builder: NavGraphBuilder.() -> Unit
) {
    navigation(startDestination, route, arguments, deepLinks, builder).apply {
        enterTransition?.let { enterTransitions[route] = enterTransition }
        exitTransition?.let { exitTransitions[route] = exitTransition }
        popEnterTransition?.let { popEnterTransitions[route] = popEnterTransition }
        popExitTransition?.let { popExitTransitions[route] = popExitTransition }
    }
}
