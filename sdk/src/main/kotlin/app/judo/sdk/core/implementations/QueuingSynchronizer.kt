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

import app.judo.sdk.core.sync.Synchronizer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Decorates any [Synchronizer] with queue like behavior,
 * making sure syncs are handled sequentially.
 *
 * @param extraBufferCapacity Amount of syncs that can be in queue at a time.
 */
internal class QueuingSynchronizer(
    ioDispatcher: CoroutineDispatcher,
    private val delegate: Synchronizer,
    private val extraBufferCapacity: Int = 20
) : Synchronizer by delegate {

    private data class WorkRequest(
        val onComplete: () -> Unit
    )

    private val scope = CoroutineScope(ioDispatcher)

    private val queue = MutableSharedFlow<WorkRequest>(extraBufferCapacity = 20)

    init {
        queue.onEach(::executeRequest).launchIn(scope)
    }

    override suspend fun performSync(onComplete: () -> Unit) {
        queue.emit(WorkRequest(onComplete))
    }

    private suspend fun executeRequest(request: WorkRequest) {
        with(request) { delegate.performSync(onComplete) }
    }

}
