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

package app.judo.sdk.compose.data

import android.content.Context
import android.util.Log
import java.io.File

object LocalFolder {
    private val TAG: String = LocalFolder::class.java.simpleName

    private const val Images = "/images/"
    private const val Media = "/media/"
    private const val Fonts = "/fonts/"

    private fun getJudoCacheDir(context: Context): File? {
        val judoCacheFolder = File(context.cacheDir, "/Judo/")

        if (!judoCacheFolder.exists()) {
            if (!judoCacheFolder.mkdir()) {
                Log.e(TAG, "Failed to create Judo folder inside the cache directory. You may need to give your application further file handling permissions.")
                return null
            }
        }

        return judoCacheFolder
    }

    fun getImagesCacheFolder(context: Context): File? {
        val imageFolder = File(getJudoCacheDir(context) ?: return null, Images)

        if (!imageFolder.exists()) {
            if (!imageFolder.mkdir()) {
                Log.e(TAG, "Failed to create $Images inside the cache directory. You may need to give your application further file handling permissions.")
                return null
            }
        }

        return imageFolder
    }

    fun getMediaCacheFolder(context: Context): File? {
        val mediaFolder = File(getJudoCacheDir(context) ?: return null, Media)

        if (!mediaFolder.exists()) {
            if (!mediaFolder.mkdir()) {
                Log.e(TAG, "Failed to create $Media inside the cache directory. You may need to give your application further file handling permissions.")
                return null
            }
        }

        return mediaFolder
    }

    fun getFontsCacheFolder(context: Context): File? {
        val fontFolder = File(getJudoCacheDir(context) ?: return null, Fonts)

        if (!fontFolder.exists()) {
            if (!fontFolder.mkdir()) {
                Log.e(TAG, "Failed to create $Fonts inside the cache directory. You may need to give your application further file handling permissions.")
                return null
            }
        }

        return fontFolder
    }
}
