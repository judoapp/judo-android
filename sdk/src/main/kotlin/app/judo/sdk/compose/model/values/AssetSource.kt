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

import app.judo.sdk.compose.ui.data.Interpolator
import com.squareup.moshi.JsonClass
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

internal sealed class AssetSource {

    /**
     * [assetName] is the name of an asset file contained in one of two folders:
     * - The `/images` folder if the AssetSource belongs to an Image node.
     * - The `/media` folder if the AssetSource belongs to an Audio or Video node.
     */
    @JsonClass(generateAdapter = true)
    data class FromFile(
        val assetName: String
    ) : AssetSource()

    /**
     * [url] is a remote URL pointing to an image, video or audio file.
     */
    @JsonClass(generateAdapter = true)
    data class FromURL(
        val url: String
    ) : AssetSource()

    companion object {
        val AssetSourcePolyAdapterFactory: PolymorphicJsonAdapterFactory<AssetSource> =
            PolymorphicJsonAdapterFactory.of(AssetSource::class.java, "__caseName")
                .withSubtype(FromFile::class.java, AssetSourceType.FROM_FILE.code)
                .withSubtype(FromURL::class.java, AssetSourceType.FROM_URL.code)
    }
}

internal enum class AssetSourceType(val code: String) {
    FROM_FILE("fromFile"),
    FROM_URL("fromURL")
}

internal fun AssetSource.interpolatedSource(interpolator: Interpolator): AssetSource? {
    return when (this) {
        is AssetSource.FromURL -> {
            val interpolatedUrl = interpolator.interpolate(url) ?: return null
            AssetSource.FromURL(interpolatedUrl)
        }
        is AssetSource.FromFile -> {
            // We do not interpolate asset filenames
            this
        }
    }
}

