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

package app.judo.sdk.compose.ui.fonts

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import app.judo.sdk.compose.data.JsonParser
import app.judo.sdk.compose.data.JudoHttpClient
import app.judo.sdk.compose.data.LocalFolder
import app.judo.sdk.compose.model.values.DocumentFont
import app.judo.sdk.compose.ui.AssetContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.io.File

internal class FontLoader(
    private val context: Context,
    private val httpClient: JudoHttpClient
) {
    private val tag = FontLoader::class.java.simpleName

    interface ResourceAPI {
        @GET
        suspend fun getFile(
            @Url aURL: String,
        ): Response<ResponseBody>
    }

    private val retrofit by lazy {
        Retrofit
            .Builder()
            // This base URL is usually not used, but is required by Retrofit.
            .baseUrl("https://content.judo.app/")
            .addConverterFactory(MoshiConverterFactory.create(app.judo.sdk.compose.data.JsonParser.moshi))
            .client(httpClient.client)
            .build()
    }

    private val api: ResourceAPI =
        retrofit.create(ResourceAPI::class.java)

    data class TypeFaceMapping(
        val mapping: Map<String, Typeface> = hashMapOf()
    )

    suspend fun getTypefaceMappings(
        context: Context,
        assetContext: AssetContext,
        sources: List<DocumentFont.Source>,
    ): TypeFaceMapping {

        val result = mutableMapOf<String, Typeface>()

        sources.forEach { source ->
            val sourceUrl = assetContext.uriForFontSource(context, source)
            if (sourceUrl == null) {
                Log.e(tag, "Asset context did not yield a source URL for font source $source")
                return@forEach
            }
            val sourceScheme = sourceUrl.scheme?.lowercase() ?: return@forEach
            // TODO we do not support TTC for now, so only one font name is expected.
            // TODO: variable fonts (other than default face) also not supported.
            val fontName = source.fontNames.firstOrNull() ?: return@forEach
            if (sourceScheme == "file" || sourceScheme == "content") {
                // local uri
                val sourcePath = sourceUrl.path ?: return@forEach
                val localFile = File(sourcePath)

                val face = Typeface.createFromFile(localFile)

                result[fontName] = face
            } else {
                // remote uri
                val response = api.getFile(sourceUrl.toString())

                val body = response.body()?.bytes()

                // we'll use the same cache folder as we do for local fonts.
                val cacheFolder = LocalFolder.getFontsCacheFolder(context)

                val file =
                    File(cacheFolder, source.assetName)

                body?.let { bytes ->
                    file.writeBytes(bytes)

                    val face = Typeface.createFromFile(file)

                    result.put(fontName, face)
                }

                file.delete()
            }
        }

        Log.d(tag, "Fonts loaded: $result")
        return TypeFaceMapping(result)
    }
}
