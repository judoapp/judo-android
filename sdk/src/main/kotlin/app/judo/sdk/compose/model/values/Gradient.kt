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

package app.judo.sdk.compose.model.values

import com.squareup.moshi.JsonClass

/**
 * Holds the values for a specific gradient. See [GradientReference].
 *
 * @param to A x/y coordinate pair, in a parametric coordinate space, between 0 and 1.
 * That is, it fractionally indicates the starting position in the final size of the gradient.
 * @param from A x/y coordinate pair, in a parametric coordinate space, between 0 and 1.
 * That is, it fractionally indicates the ending position in the final size of the gradient.
 * @param stops Indicates color values that should be interpolated between throughout the gradient.
 */
@JsonClass(generateAdapter = true)
internal data class GradientValue(
    val to: List<Float>,
    val from: List<Float>,
    val stops: List<GradientStop>
)

@JsonClass(generateAdapter = true)
internal data class GradientStop(
    /**
     * A value between 0 and 1, to indicate where along the length of a gradient the stop is positioned.
     */
    val position: Float,

    /**
     * The color that the gradient should pass through at this position.
     */
    val color: ColorValue
)
