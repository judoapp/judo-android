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

import androidx.compose.ui.unit.Constraints
import junit.framework.TestCase.assertEquals
import org.junit.Test

class PackedIntrinsicsTest {
    @Test
    fun packingAndUnpackingScalars() {
        val value = Int.pack(42, 69)
        assertEquals("0x802A0045", String.format("0x%08X", value))
        assertEquals(42, value.hiValue())
        assertEquals(69, value.loValue())
    }

    @Test
    fun packingOptionalHi() {
        val value = Int.pack(null, 69)
        assertEquals("0x00000045", String.format("0x%08X", value))
        assertEquals(null, value.hiValue())
        assertEquals(69, value.loValue())
    }

    @Test
    fun packingZeroHi() {
        val value = Int.pack(0, 69)
        assertEquals(0, value.hiValue())
        assertEquals(69, value.loValue())
    }

    @Test
    fun packingAndUnpackingInfinities() {
        val infinityInLo = Int.pack(42, Constraints.Infinity)
        assertEquals("0x802AFFFF", String.format("0x%08X", infinityInLo))
        assertEquals(42, infinityInLo.hiValue())
        assertEquals(Constraints.Infinity, infinityInLo.loValue())

        val infinityInHi = Int.pack(Constraints.Infinity, 69)
        assertEquals("0xFFFF0045", String.format("0x%08X", infinityInHi))
        assertEquals(0x7FFF0045, infinityInHi.setSignBit(false))
        assertEquals(Constraints.Infinity, infinityInHi.hiValue())
        assertEquals(69, infinityInHi.loValue())
    }
    @Test
    fun twoInfinitiesPackToMinus1() {
        val packed = Int.pack(Constraints.Infinity, Constraints.Infinity)
        assertEquals(-1, packed)
    }
    @Test
    fun packHighestFiniteInLo() {
        val packed = Int.pack(42, Int.GREATEST_FINITE)
        assertEquals(42, packed.hiValue())
        assertEquals(Int.GREATEST_FINITE, packed.loValue())
    }

    @Test
    fun packHighestFiniteInHi() {
        val packed = Int.pack(Int.GREATEST_FINITE, 69)
        assertEquals(Int.GREATEST_FINITE, packed.hiValue())
        assertEquals(69, packed.loValue())
    }

    @Test
    fun intRangeBehavesLikeExpected() {
        val range = IntRange(0, Int.MAX_VALUE)
        assertEquals(0, range.first)
        // is last inclusive? yes.
        assertEquals(Int.MAX_VALUE, range.last)
    }
}
