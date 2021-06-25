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

import android.content.Context
import kotlin.math.roundToInt

internal data class Dp(val value: Float) : Comparable<Dp> {

    operator fun plus(other: Dp) = Dp(value = this.value + other.value)

    operator fun minus(other: Dp) = Dp(value = this.value - other.value)
    operator fun unaryMinus() = Dp(-value)

    operator fun div(other: Float): Dp = Dp(value = value / other)
    operator fun div(other: Int): Dp = Dp(value = value / other)
    operator fun div(other: Dp): Float = value / other.value

    operator fun times(other: Float): Dp = Dp(value = value * other)
    operator fun times(other: Int): Dp = Dp(value = value * other)

    override operator fun compareTo(other: Dp): Int = value.compareTo(other.value)

    override fun toString() = "$value.dp"
}

internal inline val Int.dp: Dp
    get() = Dp(value = this.toFloat())

internal inline val Double.dp: Dp
    get() = Dp(value = this.toFloat())

internal inline val Float.dp: Dp
    get() = Dp(value = this)


internal operator fun Double.div(other: Dp) = Dp(this.toFloat() / other.value)
internal operator fun Int.div(other: Dp) = Dp(this / other.value)
internal operator fun Float.times(other: Dp) = Dp(this * other.value)
internal operator fun Double.times(other: Dp) = Dp(this.toFloat() * other.value)
internal operator fun Int.times(other: Dp) = Dp(this * other.value)

internal fun Dp.toPx(context: Context): Float = value * context.resources.displayMetrics.density

internal fun Dp.toIntPx(context: Context): Int {
    val px = toPx(context)
    return px.roundToInt()
}

internal fun Dp.toSptoPx(context: Context): Float =
    Dp(value / context.resources.displayMetrics.scaledDensity).toPx(context)
