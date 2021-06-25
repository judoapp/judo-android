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

package app.judo.sdk.core.data.resolvers

import android.content.Context
import android.content.res.Configuration
import app.judo.sdk.api.models.Appearance
import app.judo.sdk.api.models.Gradient
import app.judo.sdk.api.models.GradientVariants
import app.judo.sdk.ui.extensions.isDarkMode

internal class GradientResolver(private val context: Context, private val appearance: Appearance) {
    fun resolveGradient(gradientVariants: GradientVariants): Gradient {
        val darkMode = context.isDarkMode(appearance)

        return when {
            darkMode -> gradientVariants.darkMode ?: gradientVariants.darkModeHighContrast ?: gradientVariants.default
            else -> gradientVariants.default
        }
    }
}