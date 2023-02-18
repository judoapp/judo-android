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

package app.judo.sdk.compose.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import app.judo.sdk.compose.data.LocalFolder
import app.judo.sdk.compose.model.values.AssetSource
import app.judo.sdk.compose.model.values.CDNConfiguration
import app.judo.sdk.compose.model.values.DocumentFont

/**
 * Asset contexts provide information on how [AssetSource.FromFile] data is to be obtained.
 *
 * Get a reference to it through the [Environment.LocalAssetContext] composition local.
 */
internal interface AssetContext {
    /**
     * Returns a URL for the given asset source. May be a local file:// URL or a remote https://
     * one.
     */
    fun uriForFileSource(context: Context, type: AssetType, source: AssetSource.FromFile): Uri?

    fun uriForFontSource(context: Context, source: DocumentFont.Source): Uri?

    enum class AssetType {
        // TODO: add media, etc.
        IMAGE,
        MEDIA
    }
}

/**
 * This [AssetContext] provides access to assets from recently loaded Judo ZIP containers.
 *
 * This asset context needs no contextual information about the specific experience
 * being loaded because assets from Judo ZIP containers are unpacked into a single shared temp
 * directory. For now we rely on the content-addressed filenames assigned to assets by the Judo app
 * to discriminate between them.
 */
internal object UnpackedTempfilesZipContext : AssetContext {
    private val tag = UnpackedTempfilesZipContext.javaClass.simpleName
    override fun uriForFileSource(context: Context, type: AssetContext.AssetType, source: AssetSource.FromFile): Uri? {
        // In the current setup, when experiences are loaded from a ZIP container their assets
        // (which are content-addressed by convention in their naming) are all dumped into
        // the images cache temporary directory as a side-effect.

        val cacheFolder = when (type) {
            AssetContext.AssetType.IMAGE -> LocalFolder.getImagesCacheFolder(context)
            AssetContext.AssetType.MEDIA -> LocalFolder.getMediaCacheFolder(context)
        }

        if (cacheFolder == null) {
            Log.e(tag, "Failed to get the local cache folder.")
            return null
        }

        val builder = Uri.Builder().apply {
            scheme("file")
            appendPath("${cacheFolder.path}/${source.assetName}")
        }
        return builder.build()
    }

    override fun uriForFontSource(context: Context, source: DocumentFont.Source): Uri? {
        val cacheFolder = LocalFolder.getFontsCacheFolder(context)

        if (cacheFolder == null) {
            Log.e(tag, "Failed to get the local cache folder.")
            return null
        }

        val builder = Uri.Builder().apply {
            scheme("file")
            appendPath("${cacheFolder.path}/${source.assetName}")
        }
        return builder.build()
    }
}

/**
 * This [AssetContext] provides access to assets from an Experience loaded via HTTP.
 */
internal class RemoteAssetContext(
    private val basePath: Uri,
    private val configuration: CDNConfiguration? = null
) : AssetContext {
    override fun uriForFileSource(context: Context, type: AssetContext.AssetType, source: AssetSource.FromFile): Uri? {
        configuration?.let {
            return Uri.parse(it.locationForAsset(type, source.assetName))
        }

        val folderName = when (type) {
            AssetContext.AssetType.IMAGE -> "images"
            AssetContext.AssetType.MEDIA -> "media"
        }

        val builder = basePath.buildUpon().apply {
            appendPath(folderName)
            appendPath(source.assetName)
        }
        return builder.build()
    }

    override fun uriForFontSource(context: Context, source: DocumentFont.Source): Uri? {
        configuration?.let {
            return Uri.parse(it.locationForFont(source.assetName))
        }

        val builder = basePath.buildUpon().apply {
            appendPath("fonts")
            appendPath(source.assetName)
        }
        return builder.build()
    }
}

private fun CDNConfiguration.locationForAsset(type: AssetContext.AssetType, assetName: String): String {
    return when (type) {
        AssetContext.AssetType.IMAGE -> {
            imageLocation.replace("{name}", assetName)
        }
        AssetContext.AssetType.MEDIA -> {
            mediaLocation.replace("{name}", assetName)
        }
    }
}

private fun CDNConfiguration.locationForFont(fontName: String): String {
    return fontLocation.replace("{name}", fontName)
}
