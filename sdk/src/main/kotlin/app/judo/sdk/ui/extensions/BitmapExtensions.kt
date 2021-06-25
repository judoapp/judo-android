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
