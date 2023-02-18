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

package app.judo.sdk.compose.ui.layers

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.judo.sdk.R
import app.judo.sdk.compose.model.nodes.*
import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.TrackScreenEvent
import app.judo.sdk.compose.ui.layers.stacks.ZStackLayer
import app.judo.sdk.compose.ui.modifiers.ActionModifier
import app.judo.sdk.compose.ui.values.getComposeColor
import app.judo.sdk.compose.ui.utils.rememberSystemBarController

/**
 * Starting layer for a [Screen]. This is the starting point of the experience and any further screens accessible through navigation.
 *
 * It offers up a [Surface] that fills the max size and starts creating the [Screen]'s children.
 * This is necessary as we can only place children inside its parents layout size. So while we want everything to be measured correctly,
 * we also need to have the option to place things outside the parent layout size, such as when a layer is offset.
 * The surface filling the whole screen achieves exactly that for its children (the whole Experience, basically).
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun ScreenLayer(node: Screen, appearance: Appearance) {
    val systemBarController = rememberSystemBarController()

    val isDarkTheme = Environment.LocalIsDarkTheme.current
    val trackScreenCallback = Environment.LocalTrackScreenHandler.current
    val localData = Environment.LocalData.current
    val urlParameters = Environment.LocalUrlParameters.current
    val experienceId = Environment.LocalExperienceId.current
    val experienceName = Environment.LocalExperienceName.current

    val backgroundColor = remember { node.androidStatusBarBackgroundColor.getComposeColor(isDarkTheme) }

    LaunchedEffect(true) {
        systemBarController.setStatusBarColor(backgroundColor)
        systemBarController.setStatusBarIconTint(node.androidStatusBarStyle, appearance)
    }

    LaunchedEffect(key1 = trackScreenCallback) {
        trackScreenCallback?.let {
            val event = TrackScreenEvent(
                screen = TrackScreenEvent.NodeDetails(
                    node
                ),
                data = localData,
                urlParameters = urlParameters,
                experienceID = experienceId,
                experienceName = experienceName
            )
            trackScreenCallback.invoke(event)
        }
    }

    CompositionLocalProvider(
        Environment.LocalScreen provides node
    ) {
        Scaffold(
            topBar = {
                (node.children.firstOrNull { it is AppBar } as? AppBar)?.let {
                    JudoAppBar(
                        appBar = it,
                        buttonColor = remember { it.buttonColor.getComposeColor(isDarkTheme) },
                        backgroundColor = remember { it.backgroundColor.getComposeColor(isDarkTheme) },
                        screenBackground = backgroundColor
                    )
                }
            }
        ) {
            Surface(color = node.backgroundColor.getComposeColor()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    ZStackLayer {
                        Children(children = node.children)
                    }
                }
            }
        }
    }
}

@Composable
private fun JudoAppBar(
    appBar: AppBar,
    buttonColor: Color,
    backgroundColor: Color,
    screenBackground: Color
) {
    val stringTable = Environment.LocalExperienceModel.current?.localizations

    TopAppBar(
        title = {
            TextLayer(
                text = stringTable?.resolve(appBar.title) ?: appBar.title,
                textColor = appBar.titleColor,
                font = appBar.titleFont,
                lineLimit = 1
            )
        },
        elevation = 0.dp,
        navigationIcon = {
            if (!appBar.hideUpIcon) {
                // Only show back button if the current screen has another under it in the stack.
                val navigateUpAction = Environment.LocalNavigateUp.current

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.judo_screen_layer_navigation_back_button),
                    tint = buttonColor,
                    modifier = Modifier.clickable { navigateUpAction?.invoke() }
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            val itemsOverflow = appBar.children.size > 3
            val itemsFilteredByPriority = appBar.children.sortedBy { node ->
                when ((node as MenuItem).showAsAction) {
                    MenuItemVisibility.ALWAYS -> 0
                    MenuItemVisibility.IF_ROOM -> 1
                    MenuItemVisibility.NEVER -> 2
                }
            }

            if (itemsOverflow) {
                ActionItem(itemsFilteredByPriority[0] as MenuItem, appBar.buttonColor)
                ActionItem(itemsFilteredByPriority[1] as MenuItem, appBar.buttonColor)
                DropdownActionItem(itemsFilteredByPriority, appBar.buttonColor)
            } else {
                itemsFilteredByPriority.forEach { item ->
                    ActionItem(item as MenuItem, appBar.buttonColor)
                }
            }
        },
        // The color "beneath" the app bar should be the same as the screen.
        modifier = Modifier.background(color = screenBackground)
    )
}

@Composable
private fun DropdownActionItem(children: List<Node>, buttonColor: ColorReference) {
    var showingOverflowDropdown by remember { mutableStateOf(false) }

    ActionItem(
        item = MenuItem(
            id = "",
            title = stringResource(id = R.string.judo_screen_layer_appbar_dropdown_title),
            showAsAction = MenuItemVisibility.ALWAYS,
            iconMaterialName = "more_vert"
        ),
        buttonColor = buttonColor,
        onClick = { showingOverflowDropdown = !showingOverflowDropdown }
    )

    DropdownMenu(
        expanded = showingOverflowDropdown,
        onDismissRequest = { showingOverflowDropdown = false }
    ) {
        children.subList(fromIndex = 2, toIndex = children.size).forEach { dropDownItem ->
            val menuItem = dropDownItem as MenuItem

            ActionModifier(action = menuItem.action, modifier = Modifier) {
                DropdownMenuItem(onClick = { }) {
                    Text(menuItem.title)
                }
            }
        }
    }
}

@Composable
private fun ActionItem(item: MenuItem, buttonColor: ColorReference, onClick: (() -> Unit)? = null) {
    IconButton(onClick = { onClick?.invoke() }) {
        val iconNode = Icon(
            id = "",
            icon = NamedIcon(item.iconMaterialName),
            color = buttonColor,
            pointSize = 24,
            action = item.action
        )

        IconLayer(iconNode)
    }
}

@Preview
@Composable
fun AppBarSimple() {
    val appBar = AppBar(
        id = "test_appBar",
        hideUpIcon = false,
        buttonColor = ColorReference.SystemColor(colorName = "black"),
        title = "Judo Appbar!",
        titleColor = ColorReference.SystemColor(colorName = "black"),
        titleFont = Font.Document("", ""),
        backgroundColor = ColorReference.SystemColor(colorName = "white")
    )
    JudoAppBar(appBar = appBar, screenBackground = Color.Transparent, buttonColor = Color.Black, backgroundColor = Color.Black)
}
