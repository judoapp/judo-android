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

package app.judo.sdk.compose.model

import android.content.Context
import android.util.Log
import app.judo.sdk.compose.data.JsonParser
import app.judo.sdk.compose.data.LocalFolder
import app.judo.sdk.compose.model.values.ExperienceModel
import okio.ByteString.Companion.readByteString
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

internal fun ExperienceModel.Companion.fromZipStream(context: Context, zipInputStream: ZipInputStream): ExperienceModel {
    var zipEntry: ZipEntry? = zipInputStream.nextEntry
    val imageCache = LocalFolder.getImagesCacheFolder(context)
    val mediaCache = LocalFolder.getMediaCacheFolder(context)
    val fontCache = LocalFolder.getFontsCacheFolder(context)

    var experience: ExperienceModel? = null

    while (zipEntry != null) {
        Log.println(
            Log.DEBUG,
            "ExperienceModel",
            "zipEntry.name: ${zipEntry.name}"
        )

        // temporary solution to test whether or not things are deserialized correctly
        if (zipEntry.name == "document.json") {
            val documentContents =
                zipInputStream.readByteString(zipEntry.size.toInt()).utf8()

            experience = app.judo.sdk.compose.data.JsonParser.parseExperience(documentContents)
            experience!!.buildTreeAndRelationships()

            Log.println(
                Log.DEBUG,
                "ExperienceViewModel",
                "Loaded Experience nodes, initial screen ID is: ${experience.initialScreenID}"
            )
        }

        if (zipEntry.name.startsWith("images/") && imageCache != null) {
            try {
                val imageName = zipEntry.name.removePrefix("images/")
                val imageFile = File(imageCache, imageName)

                BufferedOutputStream(FileOutputStream(imageFile)).use { outputFileStream ->
                    // Data is read into this buffer.
                    val auxBuffer = ByteArray(1024)
                    var currentByteArrayPosition = zipInputStream.read(auxBuffer)

                    // -1 means end of file.
                    while (currentByteArrayPosition != -1) {
                        outputFileStream.write(auxBuffer)
                        currentByteArrayPosition = zipInputStream.read(auxBuffer)
                    }
                }
            } catch (e: Exception) {
                Log.e("ExperienceViewModel", "Failed to read embedded image ${zipEntry.name} and save it into cache.")
            }
        }

        if (zipEntry.name.startsWith("media/") && mediaCache != null) {
            try {
                val mediaName = zipEntry.name.removePrefix("media/")
                val mediaFile = File(mediaCache, mediaName)

                BufferedOutputStream(FileOutputStream(mediaFile)).use { outputFileStream ->
                    // Data is read into this buffer.
                    val auxBuffer = ByteArray(1024)
                    var currentByteArrayPosition = zipInputStream.read(auxBuffer)

                    // -1 means end of file.
                    while (currentByteArrayPosition != -1) {
                        outputFileStream.write(auxBuffer)
                        currentByteArrayPosition = zipInputStream.read(auxBuffer)
                    }
                }
            } catch (e: Exception) {
                Log.e("ExperienceViewModel", "Failed to read embedded media ${zipEntry.name} and save it into cache.")
            }
        }

        if (zipEntry.name.startsWith("fonts/") && fontCache != null) {
            try {
                val fontFileName = zipEntry.name.removePrefix("fonts/")
                val fontFile = File(fontCache, fontFileName)

                BufferedOutputStream(FileOutputStream(fontFile)).use { outputFileStream ->
                    // Data is read into this buffer.
                    val auxBuffer = ByteArray(1024)
                    var currentByteArrayPosition = zipInputStream.read(auxBuffer)

                    // -1 means end of file.
                    while (currentByteArrayPosition != -1) {
                        outputFileStream.write(auxBuffer)
                        currentByteArrayPosition = zipInputStream.read(auxBuffer)
                    }
                }
            } catch (e: Exception) {
                Log.e("ExperienceViewModel", "Failed to read embedded font ${zipEntry.name} and save it into cache.")
            }
        }

        zipEntry = zipInputStream.nextEntry
    }

    return experience!!
}
