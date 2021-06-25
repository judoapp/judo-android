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

package app.judo.sdk.core.implementations

import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.data.CookieDTO
import app.judo.sdk.core.log.Logger
import com.squareup.moshi.Moshi
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

internal class CookieJarImpl(
    keyValueCacheSupplier: () -> KeyValueCache? = { null },
    loggerSupplier: () -> Logger? = { null },
) : CookieJar {

    companion object {
        private const val TAG = "CookieJarImpl"
    }

    private val arrayAdapter = Moshi.Builder().build().adapter(List::class.java)

    private val logger by lazy(loggerSupplier)

    private val cache by lazy(keyValueCacheSupplier)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

        try {

            logger?.v(TAG, "Saving: \n\tURL: $url\n\tCookies: $cookies")

            val key = url.toString()

            val cookieDTOs = cookies.map { cookieDTO(it) }

            val arrayJSON = arrayAdapter.toJson(cookieDTOs)

            logger?.v(TAG, "Saving: \n\tURL: $url\n\tCookies JSON: $arrayJSON")

            cache?.putString(key to arrayJSON)
        } catch (error: Throwable) {
            logger?.e(TAG, "Failed to cache cookie\n\tURL:$url\n\tCookies: $cookies", error)
        }

    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {

        return try {

            logger?.v(TAG, "Loading: \n\tURL: $url")

            val jsonArray: String? = cache?.retrieveString("$url")

            val cookies: List<Cookie> = jsonArray
                ?.let(arrayAdapter::fromJson)
                ?.filterIsInstance<CookieDTO>()
                ?.map(::cookie) ?: emptyList()

            logger?.v(TAG, "Returning: \n\tURL: $url\n\tCookies: $cookies")

            return cookies
        } catch (error: Throwable) {
            logger?.e(TAG, "Failed to load cookies\n\tURL:$url", error)
            emptyList()
        }
    }

    private fun cookieDTO(cookie: Cookie): CookieDTO {
        return with(cookie) {
            CookieDTO(
                name = name(),
                value = value(),
                expiresAt = expiresAt(),
                domain = domain(),
                path = path(),
                secure = secure(),
                httpOnly = httpOnly(),
                persistent = persistent(),
                hostOnly = hostOnly()
            )
        }
    }

    private fun cookie(dto: CookieDTO): Cookie {
        return with(dto) {
            Cookie.Builder().apply {
                name(name)
                value(value)
                expiresAt(expiresAt)
                domain(domain)
                path(path)

                if (secure) secure()

                if (httpOnly) httpOnly()

            }.build()
        }
    }

}
