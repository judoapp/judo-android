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

package app.judo.sdk.utils

import app.judo.sdk.api.models.Image
import app.judo.sdk.api.models.ResizingMode


class ImageFactory(
    private val idFactory: IDFactory = IDFactory()
) {

    var image1URL = "https://content.judo.app/image1"
    var image2URL = "https://content.judo.app/image2"
    var image3URL = "https://content.judo.app/image3"

    fun makeImage1(): Image {
        return Image(
            id = idFactory.image1Id,
            imageURL = image1URL,
            resolution = 1f,
            resizingMode = ResizingMode.ORIGINAL,
            imageWidth = 100,
            imageHeight = 100,
        )
    }

    fun makeImage2(): Image {
        return Image(
            id = idFactory.image2Id,
            imageURL = image2URL,
            resolution = 1f,
            resizingMode = ResizingMode.ORIGINAL,
            imageWidth = 100,
            imageHeight = 100,
        )
    }

    fun makeImage3(): Image {
        return Image(
            id = idFactory.image3Id,
            imageURL = image3URL,
            resolution = 1f,
            resizingMode = ResizingMode.ORIGINAL,
            imageWidth = 100,
            imageHeight = 100,
        )
    }

}
