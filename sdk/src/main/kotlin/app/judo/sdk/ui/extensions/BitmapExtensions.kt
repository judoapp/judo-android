package app.judo.sdk.ui.extensions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import app.judo.sdk.api.models.Point

/**
 * Copies a [Bitmap], transforms it's color, then blurs the copy and returns it.
 *
 * @receiver The [Bitmap] to copy.
 * @param shadow The radius of the blur.
 * @param color The color of the shadow. The default is [Color.BLACK].
 */
internal fun Bitmap.mapToShadowAndOffset(radius: Float = 0f): Pair<Bitmap, Point> {

    // Apply a blur mask with the given radius.
    val paint = Paint().apply {
        isAntiAlias = true
        // Passing 0 or less will throw an exception so we don't set the mask in that case.
        if (radius > 0f) maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
    }

    // Get a blurry shadow copy of the bitmap and extract the blurry bitmaps offset from the non blurry bitmap.
    val intArray = IntArray(2)
    return extractAlpha(paint, intArray) to Point(x = intArray[0], y = intArray [1])
}

internal fun Bitmap.toDrawable(resources: Resources) = BitmapDrawable(resources, this)

internal fun Bitmap.scale(width: Int, height: Int, filter: Boolean = true): Bitmap {
    return Bitmap.createScaledBitmap(this, width, height, filter)
}
