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

package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@Suppress("CanSealedSubClassBeObject")
sealed class PageControlStyle {

    @JsonClass(generateAdapter = true)
    class DefaultPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    class LightPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    class DarkPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    class InvertedPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    data class CustomPageControlStyle(
        val normalColor: ColorVariants,
        val currentColor: ColorVariants
    ) : PageControlStyle()

    @JsonClass(generateAdapter = true)
    data class ImagePageControlStyle(
        val normalColor: ColorVariants,
        val currentColor: ColorVariants,
        val normalImage: Image,
        val currentImage: Image
    ) : PageControlStyle()
}