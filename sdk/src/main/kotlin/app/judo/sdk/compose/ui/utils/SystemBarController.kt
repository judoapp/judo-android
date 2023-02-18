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

package app.judo.sdk.compose.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import app.judo.sdk.compose.model.values.Appearance
import app.judo.sdk.compose.model.values.StatusBarStyle

@Composable
internal fun rememberSystemBarController(
    window: Window? = findWindow(),
): SystemBarController =
    remember(window) { SystemBarController(window) }

@Composable
private fun findWindow(): Window? =
    (LocalView.current.parent as? DialogWindowProvider)?.window
        ?: LocalView.current.context.findWindow()

internal class SystemBarController(
    private val window: Window?
) {
    fun setStatusBarColor(
        color: Color
    ) {
        window?.statusBarColor = color.toArgb()
    }

    @Suppress("DEPRECATION")
    fun setStatusBarIconTint(
        statusBarStyle: StatusBarStyle,
        appearance: Appearance
    ) {
        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.decorView.windowInsetsController?.systemBarsAppearance
                val barAppearance = if (statusBarStyle.isDarkIconTint(
                        it.context,
                        appearance
                    )
                ) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0
                it.decorView.windowInsetsController?.setSystemBarsAppearance(
                    barAppearance,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                it.decorView.systemUiVisibility =
                    if (statusBarStyle.isDarkIconTint(
                            it.context,
                            appearance
                        )
                    ) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
            }
        }
    }
}

private fun StatusBarStyle.isDarkIconTint(context: Context, appearance: Appearance): Boolean =
    when (this) {
        StatusBarStyle.DEFAULT -> !context.isDarkMode(appearance)
        StatusBarStyle.LIGHT -> false
        StatusBarStyle.DARK -> true
        StatusBarStyle.INVERTED -> context.isDarkMode(appearance)
    }

private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

private fun Context.isDarkMode(appearance: Appearance): Boolean =
    when (appearance) {
        Appearance.DARK -> true
        Appearance.LIGHT -> false
        Appearance.AUTO -> resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
