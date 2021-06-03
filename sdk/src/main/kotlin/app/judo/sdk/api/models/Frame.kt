package app.judo.sdk.api.models

import android.content.Context
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Frame(
    val width: Float? = null,
    val height: Float? = null,
    val minWidth: Float? = null,
    val minHeight: Float? = null,
    val maxWidth: MaxWidth? = null,
    val maxHeight: MaxHeight? = null,
    val alignment: Alignment
) {
    fun toPxFrame(context: Context): Frame {
        val maxHeight = this.maxHeight?.let {
            when (it) {
                is MaxHeight.Finite -> it.copy(value = it.value.dp.toPx(context))
                is MaxHeight.Infinite -> it
            }
        }
        val maxWidth = this.maxWidth?.let {
            when (it) {
                is MaxWidth.Finite -> it.copy(value = it.value.dp.toPx(context))
                is MaxWidth.Infinite -> it
            }
        }
        val minWidth = this.minWidth?.dp?.toPx(context)
        val minHeight = this.minHeight?.dp?.toPx(context)

        return Frame(
            width?.dp?.toPx(context),
            height?.dp?.toPx(context),
            minWidth,
            minHeight,
            maxWidth,
            maxHeight,
            alignment
        )
    }
}

internal fun Frame?.unboundedWidth(): Boolean {
    return this?.maxWidth == null && this?.minWidth == null && this?.width == null
}

internal fun Frame?.unboundedHeight(): Boolean {
    return this?.maxHeight == null && this?.minHeight == null && this?.height == null
}
