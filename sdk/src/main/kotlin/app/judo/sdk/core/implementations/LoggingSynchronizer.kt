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

import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.sync.Synchronizer

/**
 * Logs how many and which syncs have completed.
 */
internal class LoggingSynchronizer(
    private val delegate: Synchronizer,
    private val logger: Logger
) : Synchronizer by delegate {

    companion object {
        private const val TAG = "Synchronizer"
    }

    private var syncs = 0

    override suspend fun performSync(onComplete: () -> Unit) {
        syncs++
        logger.d(TAG, "Syncs added: $syncs")
        delegate.performSync {
            val syncNumber = syncs
            logger.d(TAG, "Syncs completed: $syncNumber")
            onComplete()
        }
    }

}
