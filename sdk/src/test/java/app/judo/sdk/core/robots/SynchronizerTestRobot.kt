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

package app.judo.sdk.core.robots

import app.judo.sdk.core.implementations.SynchronizerImpl
import app.judo.sdk.core.sync.Synchronizer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert

@ExperimentalCoroutinesApi
internal class SynchronizerTestRobot : AbstractTestRobot() {

    private lateinit var synchronizer: Synchronizer

    override fun onSetUp() {
        super.onSetUp()

        backingEnvironment.configuration = backingEnvironment.configuration.copy(domain = "test1.judo.app")
        
        synchronizer = SynchronizerImpl(
            backingEnvironment
        )

    }

    suspend fun performSync(onComplete: () -> Unit = {}) {
        synchronizer.performSync(onComplete)
    }

    fun assertTheLastURLPathToBeFetchedWas(expected: String) {
        Assert.assertEquals(expected, serverDispatcher.actualRequest?.path)
    }

    fun assertTheLastURLPathToBeFetchedStartsWith(expected: String) {
        Assert.assertTrue(serverDispatcher.actualRequest?.path?.startsWith(expected) == true)
    }

}
