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

import app.judo.sdk.api.events.Event
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.events.EventBus
import app.judo.sdk.core.services.PushTokenService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

internal class PushTokenServiceImpl(
    private val baseClientSupplier: () -> OkHttpClient,
    private val keyValueCacheSupplier: () -> KeyValueCache,
    private val ioDispatcherSupplier: () -> CoroutineDispatcher,
    private val eventBusSupplier: () -> EventBus
) : PushTokenService {
    companion object {
        const val PUSH_TOKEN_KEY = "push_token"
    }

    override var pushToken: String?
        get() = keyValueCacheSupplier().retrieveString(PUSH_TOKEN_KEY)
        private set(value) {
            if (value == null) {
                keyValueCacheSupplier().remove(PUSH_TOKEN_KEY)
            } else {
                keyValueCacheSupplier().putString(Pair(PUSH_TOKEN_KEY, value))
            }
        }

    override fun register(fcmToken: String) {
        pushToken = fcmToken

        CoroutineScope(ioDispatcherSupplier()).launch {
            eventBusSupplier().publish(
                Event.PushTokenUpdated
            )
        }
    }
}
