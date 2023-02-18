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

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalProvider
import app.judo.sdk.compose.model.values.HttpMethod

/**
 * A mutable description of the configuration of an outbound HTTP API request to a Data Source.
 *
 * Mutate the fields on this object to change the request to a data source before Judo submits it.
 */
data class URLRequest(
    var url: String,
    var method: HttpMethod,
    var headers: HashMap<String, String> = hashMapOf(),
    var body: String? = null
)

/**
 * The shape of the custom action handler callback.
 */
typealias AuthorizerHandler = (URLRequest) -> Unit

fun Modifier.judoAuthorize(callback: AuthorizerHandler): Modifier {
    @OptIn(ExperimentalComposeUiApi::class)
    return this.modifierLocalProvider(
        Environment.ModifierLocalAuthorizerHandler
    ) {
        callback
    }
}
