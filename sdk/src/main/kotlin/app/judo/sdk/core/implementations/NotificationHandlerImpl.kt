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

import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.RegistrationRequestBody
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.notifications.NotificationHandler


internal class NotificationHandlerImpl(
    private val environment: Environment
) : NotificationHandler {

    companion object {
        private const val TAG = "NotificationHandler"
    }

    private object ACTIONS {
        const val SYNC: String = "SYNC"
    }

    override suspend fun handleRemoteMessagingData(data: Map<String, String>) {
        try {
            data[Environment.Keys.MESSAGE]?.let { json ->
                JsonParser.parseJudoMessage(json)?.let { judoMessage ->

                    environment.logger.i(TAG, "Sync message received: $judoMessage")

                    when (judoMessage.action) {
                        ACTIONS.SYNC -> {
                            environment.logger.d(TAG, "Triggering action: ${ACTIONS.SYNC}")
                            SynchronizerImpl(environment).performSync(prefetchAssets = true) {
                                environment.logger.d(TAG, "Sync Completed")
                            }
                        }
                        else -> {
                            environment.logger.e(
                                TAG,
                                null,
                                IllegalArgumentException("Invalid Action: ${judoMessage.action}")
                            )
                        }
                    }
                }
            }
        } catch (error: Throwable) {
            environment.logger.e(
                TAG,
                "Failed to handle notification:",
                error
            )
        }
    }
}
