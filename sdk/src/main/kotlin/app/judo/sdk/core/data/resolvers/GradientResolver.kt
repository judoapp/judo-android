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