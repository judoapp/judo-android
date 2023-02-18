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

package app.judo.sdk.compose.ui.layout

import android.util.Log
import android.util.Size
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import app.judo.sdk.compose.ui.modifiers.judoChildModifierData
import app.judo.sdk.compose.ui.utils.ifInfinity

/**
 * Implements Judo/SwiftUI's measure() interface contract on top of Jetpack Compose's intrinsics
 * system.
 *
 * We abuse Jetpack Compose's intrinsic measurable interface to implement a full
 * Judo/SwiftUI layout pass, and we do so by packing the size's width and height fields into single
 * int values for both the input parameter and the return value. with 32 and 31 (the sign bit is
 * used as a flag to indicate a packed value) bits for each field, there is sufficient space for
 * each dimension.
 *
 * This is done because:
 *
 * a) Compose does not normally allow multiple measurement passes;
 * b) Compose does not allow extending the (Intrinsic)Measurable interface with custom methods;
 * c) It is incorrect and also expensive to implement Judo/SwiftUI layout with the 4 intrinsics
 *    interface methods that give a range for width, height, and do so only with a cross dimension.
 *
 * This means we only need one of the four intrinsic methods for measurement.
 */
internal fun IntrinsicMeasurable.judoMeasure(proposedSize: Size): Size {
    val packedHeightParam = PackedHeight(proposedSize).packedValue

    return this.annotateIntrinsicsCrash {
        this.maxIntrinsicWidth(packedHeightParam).let { packedWidth ->
            PackedWidth(packedWidth).let { (width, height) ->
                Size(
                    width,
                    height
                )
            }
        }
    }
}

/**
 * Call this method to obtain a range of horizontal flexibility for this measurable.
 *
 * Implemented by 'commandeering' `minIntrinsicWidth` in the measurables.  Implementations
 * should use the `mapMaxIntrinsicWidthAsMeasure` method to handle the details of packing the
 * output range into the single returned int.
 */
internal fun IntrinsicMeasurable.judoHorizontalFlex(): IntRange {
    return this.annotateIntrinsicsCrash {
        this.minIntrinsicWidth(0).let { packedRange ->
            IntRange.fromPacked(packedRange)
        }
    }
}

/**
 * Call this method to obtain a range of vertical flexibility for this measurable.
 *
 * Implemented by 'commandeering' `minIntrinsicHeight` in the measurables.  Implementations
 * should use the `mapMaxIntrinsicWidthAsMeasure` method to handle the details of packing the
 * output range into the single returned int.
 */
internal fun IntrinsicMeasurable.judoVerticalFlex(): IntRange {
    return this.annotateIntrinsicsCrash {
        this.minIntrinsicHeight(0).let { packedRange ->
            IntRange.fromPacked(packedRange)
        }
    }
}

/**
 * We abuse Jetpack Compose's intrinsic measurable interface to implement a full Judo/SwiftUI layout
 * pass.  Because the Measurable (intrinsic or otherwise) interface cannot be extended, we do so by
 * commandeering the `maxIntrinsicWidth` method.  The other three intrinsic methods remain available
 * to be commandeered by other purposes, but we do not use the Compose intrinsics system for its
 * originally intended purpose (providing ranges of flexibility for a measurable given the cross
 * dimension.)
 *
 * This helper routine aids with unpacking the input parameter (width) and packing the output
 * parameter (height) such that we now have the full Judo/SwiftUI measurement interface:
 *
 *     measure(proposedSize: Size): Size.
 *
 * This helper method aids the implementation of the Judo/SwiftUI measurement interface proxied
 * through maxIntrinsicWidth() using a packed width parameter and packed return value.
 */
internal fun IntrinsicMeasureScope.mapMaxIntrinsicWidthAsMeasure(height: Int, measure: (Size) -> Size): Int {
    PackedHeight(height).let { (width, height) ->
        return PackedWidth(measure(Size(width, height))).packedValue
    }
}

/**
 * For use in both [IntrinsicMeasurable.minIntrinsicHeight] and
 * [IntrinsicMeasurable.minIntrinsicWidth] to provide [judoVerticalFlex] and [judoHorizontalFlex]
 * respectively.
 *
 * Note - the parameter value to the two intrinsics methods is *unused* under this regime. It will
 * have an undefined value, do not use it.
 */
internal fun IntrinsicMeasureScope.mapMinIntrinsicAsFlex(flex: () -> IntRange): Int {
    return flex().packedValue
}

internal operator fun Size.component1(): Int = width
internal operator fun Size.component2(): Int = height

// Jetpack Compose, like other functional (think "monadic") UI frameworks with a layout concern
// (think SwiftUI, Flutter), strongly requires you to constrain your data flow to their pipeline.
// If you do stuff out-of-band, then the core assumptions the thing was built (namely around caching
// and invalidation) around break down and you'll get horrendous bugs.
//
// Thus, it is necessary to extend the layout interface within the bounds of the existing
// IntrinsicMeasurable interface contract.
//
// Even though we can't extend the IntrinsicMeasurable interface (and clearly values that are passed
// through it are being memoized), there is some opportunity to cheekily extending it inline.
//
// What we ant to do is create a "virtual" method where we turn, for example,
// maxIntrinsicHeight(width) into maxIntrinsicHeight(width, HEIGHT).
//
// We can take a cue from Compose's own Constraints type, which uses clever bit packing to store all
// four constraint fields into a single integer value. We'll do the same.
//
// The width parameter is a signed 32-bit integer. Splitting that into two short values, we have the
// low 16 bits (which can store a value up to 65,535) and the high 15 bits (which can store a value
// up to 32,768). 32,768 is still large enough for the largest pixel size value we're going to
// realistically see on a real device, so we can get away with this.
//
// Negative values are never used normally in the Compose intrinsics system, so we'll use the the
// sign (highest) bit as a flat to indicate that one of these "packed" values is being used. Knowing
// when an intrinsics value is a "packed" one or not is important, so this sign-bit allows us to
// represent a Null case for high value.
//
// Naturally infinities still need to be represented, so we'll represent that in that same way as
// Constraints.Infinity and turn all the bits on each of the short values.
//
// See PackedIntrinsics.kt for the various routines for implementing this, referred to has hiValue
// and loValue.
//
// It is very important that we don't "leak out" packed values by calling the intrinsics on non-Judo
// measurement policies with packed values. If that happens, they usually (and hopefully) crash
// being given an illegal negative value. Strategic error handling and "strip" helpers have been
// added to aid in this.

@JvmInline
value class PackedHeight(
    val packedValue: Int
) {
    init {
        if (!packedValue.isPacked()) {
            throw IllegalArgumentException("Unexpected non-packed value encountered.")
        }
    }

    constructor(
        width: Int,
        height: Int
    ) : this(
        Int.pack(width, height)
    )

    constructor(
        size: Size
    ) : this(size.width, size.height)

    val width: Int
        get() = packedValue.hiValue() ?: throw IllegalArgumentException("Unexpected non-packed value encountered.")

    val height: Int
        get() = packedValue.loValue()

    operator fun component1(): Int = width

    operator fun component2(): Int = height

    companion object {
        val Zero = PackedHeight(0, 0)
    }
}

@JvmInline
value class PackedWidth(
    val packedValue: Int
) {
    init {
        if (!packedValue.isPacked()) {
            throw IllegalArgumentException("Unexpected non-packed value encountered.")
        }
    }

    constructor(
        width: Int,
        height: Int
    ) : this(
        Int.pack(height, width)
    )

    constructor(
        size: Size
    ) : this(size.width, size.height)

    val width: Int
        get() = packedValue.loValue()

    val height: Int
        get() = packedValue.hiValue() ?: throw IllegalArgumentException("Unexpected non-packed value encountered.")

    operator fun component1(): Int = width

    operator fun component2(): Int = height
}

/**
 * Pack an [IntRange] into a Judo packed value.
 */
internal fun IntRange.Companion.fromPacked(packedValue: Int): IntRange =
    IntRange(
        packedValue.hiValue() ?: throw Exception("Unexpected unpacked value used as range"),
        packedValue.loValue()
    )

internal val IntRange.packedValue: Int
    get() = Int.pack(first, last)

/**
 * Pack a 15 bit and a 16 bit short int into a single 32-bit int. Sign bit is turned on to indicate
 * a packed value.
 *
 * Only unsigned values supported, despite java's [Int] being signed.
 */
internal fun Int.Companion.pack(hi: Int?, lo: Int): Int {
    // infinities are all bits but signed turned on, so they should all end up with all
    // bits on in hi and low parts so no special handling needed.
    if (hi != null) {
        return (((hi and 0x7fff) shl 16) + (lo and 0xffff)).setSignBit()
    } else if (lo > -1) {
        return lo
    } else {
        Log.e("pack()", "pack() given unsupported negative number. Did you give an already packed value?")
        return lo
    }
}

internal fun Int.unpack(): Pair<Int?, Int> {
    return Pair(hiValue(), loValue())
}

/**
 * Toggle the sign bit, but leaving the same bit pattern (ie does NOT negate the value ala 2's complement)
 */
internal fun Int.setSignBit(set: Boolean = true): Int {
    // alternatively, the arithmetic to "undo" 2s complement lol:
    //   return Int.MAX_VALUE - (this * -1) + 1
    return if (set) {
        this or 0x80000000.toInt()
    } else {
        this and 0x7fffffff
    }
}

internal fun Int.loValue(): Int {
    // check for less than zero and "negative zero" as a way of checking for sign bit, which is our
    // way of flagging a packed value. if hi and lo are both 0, then the sign-bit still on and we
    // need to know that.
    return if (this.isPacked()) {
        // first unset the sign-bit.
        val nv = this and 0x7fffffff

        val mask = 0x0000ffff
        val value = nv and mask
        if (value == 0xffff) {
            Constraints.Infinity
        } else if (value == 0xfffe) {
            Int.GREATEST_FINITE
        } else {
            value
        }
    } else {
        this
    }
}

private fun Int.isPacked(): Boolean = this < 0 || this == -0

internal fun Int.hiValue(): Int? {
    // because we have to exclude the sign-bit here, we only get 32k value space, but that's fine.
    return if (this < 0 || this == -0) {
        val mask = 0x7fff0000
        val value = (this and mask) shr 16
        return if (value == 0x7fff) {
            Constraints.Infinity
        } else if (value == 0x7ffe) {
            Int.GREATEST_FINITE
        } else {
            value
        }
    } else {
        null
    }
}

/**
 * When using a stock Jetpack Compose composable, it is important to scrub the orthogonal dimension
 * information packed into the constraint value given to intrinsics, otherwise undefined behaviour
 * can result.
 *
 * This means stripping all the hi values out.
 */
@Deprecated("This approximated behaviour by this layout modifier is likely to be wrong. Replace it with appropriate bespoke layout modifier at your usage site.")
internal class StripPackedJudoIntrinsics : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        // same behaviour as SimpleLayout.
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMaxIntrinsicWidthAsMeasure(height) { (proposedWidth, proposedHeight) ->
            // measurable is a non-judo measurable. to conform it as best we can do take the
            // we can use the intrinsics.

            // of course, child may return infinity (although I believe we don't have any cases
            // where this occurs using native Compose measurables.). In that case, best we can do
            // is clamp child's size to the proposed size.

            val maxConstraintVal = 2 shl 13

            val childWidth = measurable.maxIntrinsicWidth(minOf(proposedHeight, maxConstraintVal))
                .ifInfinity { proposedWidth }
            val childHeight = measurable.maxIntrinsicHeight(minOf(proposedWidth, maxConstraintVal))
                .ifInfinity { proposedHeight }

            Size(childWidth, childHeight)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            IntRange(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            IntRange(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        throw IllegalStateException("Only call maxIntrinsicWidth, with packed parameter, on Judo measurables.")
    }
}

fun <R> Measurable.annotateIntrinsicsCrash(eval: () -> R): R {
    return try {
        eval()
    } catch (e: IllegalArgumentException) {
        if (e.message?.contains("maxWidth") == true || e.message?.contains("maxHeight") == true) {
            throw Exception("IllegalArgumentException occurred while measuring intrinsics within stack, likely escape of Judo packed intrinsics. Associated layer ${judoChildModifierData?.debugNode?.description}", e)
        } else {
            throw e
        }
    }
}

fun <R> IntrinsicMeasurable.annotateIntrinsicsCrash(traceName: String? = null, eval: () -> R): R {
    return try {
        eval()
    } catch (e: IllegalArgumentException) {
        val descriptor = traceName ?: judoChildModifierData?.debugNode?.description
        if (descriptor != null) {
            throw Exception(
                "IllegalArgumentException occurred while measuring intrinsics within stack. Possible measurement of non-Judo composable or missing custom intrinsics. Associated layer $descriptor",
                e
            )
        } else {
            throw e
        }
    }
}

// And finally, we need a "greatest finite value" to use that is not going to be conflated with infinity.

internal val Int.Companion.GREATEST_FINITE: Int get() = Int.MAX_VALUE - 1
