package app.judo.sdk.core.data.resolvers

import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import app.judo.sdk.api.models.Appearance
import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.ui.extensions.convertTo
import app.judo.sdk.ui.extensions.isDarkMode
import kotlin.math.roundToInt

internal class ColorResolver(private val context: Context, private val appearance: Appearance) {

    @ColorInt
    fun resolveForColorInt(color: ColorVariants): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resolveConvertedSRGBColor(color)
        } else {
            resolveSRGBColor(color)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @ColorInt
    private fun resolveConvertedSRGBColor(color: ColorVariants): Int {
        val darkMode = context.isDarkMode(appearance)

        val colorForEnvironment = when {
            darkMode -> color.darkMode ?: color.darkModeHighContrast ?: color.default
            else -> color.default
        }

        val colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
        return Color.valueOf(Color.valueOf(
            colorForEnvironment.red,
            colorForEnvironment.green,
            colorForEnvironment.blue,
            colorForEnvironment.alpha,
            colorSpace
        ).pack().convertTo(ColorSpace.Named.SRGB)).toArgb()
    }

    @ColorInt
    private fun resolveSRGBColor(color: ColorVariants): Int {
        val darkMode = context.isDarkMode(appearance)

        val colorForEnvironment = when {
            darkMode -> color.darkMode ?: color.darkModeHighContrast ?: color.default
            else -> color.default
        }

        return Color.argb(
            (colorForEnvironment.alpha * 255).roundToInt(),
            (colorForEnvironment.red * 255).roundToInt(),
            (colorForEnvironment.green * 255).roundToInt(),
            (colorForEnvironment.blue * 255).roundToInt()
        )
    }
}
