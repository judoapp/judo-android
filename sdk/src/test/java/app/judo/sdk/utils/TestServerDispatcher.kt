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

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File

internal class TestServerDispatcher(
    val onRequest: (RecordedRequest) -> Unit = {},
    val onResponse: (Pair<MockResponse, String>) -> Unit = {}
) : Dispatcher() {

    private val assetsPath = "${System.getProperty("user.dir")}/src/test/java/app/judo/sdk/assets"

    val responseHeaders = mutableListOf<Pair<String, String>>()

    var code: () -> Int = { 200 }

    var actualRequest: RecordedRequest? = null

    @Suppress("SpellCheckingInspection")
    override fun dispatch(request: RecordedRequest): MockResponse {

        actualRequest = request

        val response = MockResponse().apply {

            responseHeaders.forEach { (name, value) ->
                setHeader(name, value)
            }

            request.getHeader("Accept")?.let {
                setHeader("Accept", it)
            }

            request.getHeader("Judo-Access-Token")?.let {
                setHeader("Judo-Access-Token", it)
            }

            request.getHeader("Judo-Device-ID")?.let {
                setHeader("Judo-Device-ID", it)
            }

        }

        try {
            onRequest(request)
        } catch (e: Throwable) {
            return response.apply {
                setResponseCode(500)
                e.message?.let {
                    setBody(it)
                }
            }
        }

        when (val pathAndCode = request.path to code()) {

            "/test1.judo.app/sync" to 200, "/test2.judo.app/sync" to 200 -> {
                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(TestJSON.syncResponse)
                }
                onResponse(response to TestJSON.syncResponse)
            }

            "/test1.judo.app/testexperience" to 200, "/test2.judo.app/testexperience" to 200 -> {
                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(TestJSON.experience)
                }
                onResponse(response to TestJSON.experience)
            }

            "/test1.judo.app/datasourcetestexperience" to 200 -> {
                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(TestJSON.data_source_experience_single_screen)
                }
                onResponse(response to TestJSON.data_source_experience_single_screen)
            }

            "/test1.judo.app/userdatatestexperience" to 200 -> {
                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(TestJSON.user_data_experience)
                }
                onResponse(response to TestJSON.user_data_experience)
            }

            "/register" to 200 -> {
                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(TestJSON.register_response)
                }
                onResponse(response to TestJSON.register_response)
            }

            "/test1.judo.app/testexperience" to 401 -> {
                response.apply {
                    setResponseCode(pathAndCode.second)
                }
                onResponse(response to "")
            }

            "/test1.judo.app/testexperience" to 201 -> {
                response.apply {
                    setResponseCode(pathAndCode.second)
                }
                onResponse(response to "")
            }

            "/dummyapi.io/data/api/user?limit=10" to 200 -> {
                val body = if (request.getHeader("app-id") == "609561d4e8fed0600a0a26b8") {
                    TestJSON.dummy_api_response
                } else {
                    ""
                }

                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(body)
                }

                onResponse(response to body)
            }

            "/dummyapi.io/data/api/user/put" to 200 -> {
                val requestBody = request.body.readString(Charsets.UTF_8)

                val responseBody = if (
                    request.getHeader("app-id") == "609561d4e8fed0600a0a26b8" &&
                            requestBody == """{"name":"User"}"""
                ) {
                    """{"result":"Success"}"""
                } else {
                    ""
                }

                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(responseBody)
                }

                onResponse(response to responseBody)
            }

            "/dummyapi.io/data/api/user/put/null" to 200 -> {

                val responseBody = if (
                    request.getHeader("app-id") == "609561d4e8fed0600a0a26b8"
                ) {
                    """{"result":"Success"}"""
                } else {
                    ""
                }

                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(responseBody)
                }

                onResponse(response to responseBody)
            }

            "/dummyapi.io/data/api/user/post" to 200 -> {
                val requestBody = request.body.readString(Charsets.UTF_8)

                val responseBody = if (
                    request.getHeader("app-id") == "609561d4e8fed0600a0a26b8" &&
                            requestBody == """{"name":"User"}"""
                ) {
                    """{"result":"Success"}"""
                } else {
                    ""
                }

                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(responseBody)
                }

                onResponse(response to responseBody)
            }

            "/dummyapi.io/data/api/user/post/null" to 200 -> {

                val responseBody = if (
                    request.getHeader("app-id") == "609561d4e8fed0600a0a26b8"
                ) {
                    """{"result":"Success"}"""
                } else {
                    ""
                }

                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(responseBody)
                }

                onResponse(response to responseBody)
            }

            "/reqres.in/api/users" to 200 -> {

                val responseBody = TestJSON.reqres_api_response

                response.apply {
                    setResponseCode(pathAndCode.second)
                    setBody(responseBody)
                }

                onResponse(response to responseBody)
            }

            else -> {

                // Is the request for an image? If so return the response.png in the assets folder
                if (pathAndCode.first?.startsWith("/content.judo") == true) {
                    val bodyFile = File("$assetsPath/response.png")
                    val body = bodyFile.readText(Charsets.UTF_8)
                    response.apply {
                        setHeader("Cache-Control", "public, max-age=31557600, immutable")
                        setResponseCode(pathAndCode.second)
                        setBody(body)
                    }
                    onResponse(response to body)
                }

            }

        }

        return response
    }

}