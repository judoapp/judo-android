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

package app.judo.sdk.compose.ui.modifiers

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.modifier.ModifierLocalConsumer
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import app.judo.sdk.compose.model.values.Action
import app.judo.sdk.compose.ui.CustomActionActivation
import app.judo.sdk.compose.ui.CustomActionHandler
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.data.Interpolator
import app.judo.sdk.compose.ui.data.makeDataContext
import app.judo.sdk.compose.ui.utils.SimpleMeasurePolicy

@SuppressLint("ModifierParameter")
@Composable
internal fun ActionModifier(
    action: Action?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    val tag = "Judo.ActionModifier"
    val dataContext = makeDataContext(
        userInfo = Environment.LocalUserInfo.current?.invoke() ?: emptyMap(),
        urlParameters = Environment.LocalUrlParameters.current,
        data = Environment.LocalData.current
    )

    val interpolator = Interpolator(
        dataContext
    )

    when (action) {
        is Action.PerformSegue -> {
            val navigateFunction = Environment.LocalNavigateToScreen.current
            val localData = Environment.LocalData.current

            ActionModifierButton(
                onClick = {
                    when (val screen = action.screenID) {
                        is String -> navigateFunction?.invoke(screen, localData)
                        null -> Log.w(tag, "Tried to do $action without a screen ID. Ignoring!")
                    }
                },
                modifier = modifier
            ) {
                content(modifier = Modifier)
            }
        }

        is Action.OpenURL -> {
            val uriHandler = LocalUriHandler.current
            val dismissExperienceFunction = if (action.dismissExperience) Environment.LocalDismissExperience.current else null

            ActionModifierButton(
                onClick = {
                    interpolator.interpolate(action.url)?.let {
                        try {
                            uriHandler.openUri(it)
                        } catch (e: ActivityNotFoundException) {
                            Log.w(tag, "Activity not found to handle URI: $it")
                        }
                        dismissExperienceFunction?.invoke()
                    }
                },
                modifier = modifier
            ) {
                content(modifier = Modifier)
            }
        }

        is Action.PresentWebsite -> {
            val customTabIntent = CustomTabsIntent.Builder().build()
            val context = LocalContext.current

            ActionModifierButton(
                onClick = {
                    interpolator.interpolate(action.url)?.let {
                        try {
                            customTabIntent.launchUrl(context, Uri.parse(it))
                        } catch (e: ActivityNotFoundException) {
                            Log.w(tag, "Unable to present web URL (either bad scheme or missing browser): $it")
                        }
                    }
                },
                modifier = modifier
            ) {
                content(modifier = Modifier)
            }
        }

        is Action.Close -> {
            val dismissExperienceFunction = Environment.LocalDismissExperience.current

            ActionModifierButton(
                onClick = { dismissExperienceFunction?.invoke() },
                modifier = modifier
            ) {
                content(modifier = Modifier)
            }
        }

        is Action.Custom -> {
            val dismissExperienceFunction = if (action.dismissExperience) Environment.LocalDismissExperience.current else null
            val node = Environment.LocalNode.current ?: return
            val screen = Environment.LocalScreen.current ?: return
            val experienceUrl = Environment.LocalExperienceUrl.current
            val urlParameters = Environment.LocalUrlParameters.current
            val experienceId = Environment.LocalExperienceId.current
            val experienceName = Environment.LocalExperienceName.current

            var callback by remember { mutableStateOf<CustomActionHandler?>(null) }

            ActionModifierButton(
                onClick = {
                    dismissExperienceFunction?.invoke()

                    callback?.invoke(
                        CustomActionActivation(
                            experienceUrl = experienceUrl,
                            screen = CustomActionActivation.NodeDetails(screen),
                            node = CustomActionActivation.NodeDetails(node),
                            urlParameters = urlParameters,
                            experienceID = experienceId,
                            experienceName = experienceName
                        )
                    )
                },
                modifier = modifier
                    // this little thing is for getting the modifier local value for CustomActionHandler and
                    // making it available here from a composable.
                    .then(
                        ActionCallbackConsumer {
                            callback = it
                        }
                    )
            ) {
                content(modifier = Modifier)
            }
        }

        else -> content(modifier = modifier)
    }
}

@SuppressLint("ModifierParameter")
@Composable
private fun ActionModifierButton(
    onClick: () -> Unit,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    Layout(
        content = { content(modifier = Modifier) },
        measurePolicy = SimpleMeasurePolicy(),
        modifier = modifier.clickable(
            onClick = onClick,
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple()
        )
    )
}

/**
 * This is a [ModifierLocalConsumer] which allows us to get the
 * [Environment.ModifierLocalCustomActionHandler] from modifier-space into layout space.
 */
private class ActionCallbackConsumer(
    private val valueUpdated: (CustomActionHandler?) -> Unit
) : ModifierLocalConsumer {
    override fun onModifierLocalsUpdated(scope: ModifierLocalReadScope) {
        scope.apply {
            valueUpdated(Environment.ModifierLocalCustomActionHandler.current)
        }
    }
}
