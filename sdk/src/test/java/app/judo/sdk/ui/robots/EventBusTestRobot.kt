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

package app.judo.sdk.ui.robots

import app.judo.sdk.core.events.EventBus
import app.judo.sdk.core.robots.AbstractTestRobot
import app.judo.sdk.ui.implementations.EventBusImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Assert

@ExperimentalCoroutinesApi
internal class EventBusTestRobot: AbstractTestRobot() {

    private lateinit var bus: EventBus

    private val events = mutableListOf<Any>()

    override fun onSetUp() {
        super.onSetUp()
        bus = EventBusImpl()
    }

    fun subscribe() {
        testScope.launch {
            bus.eventFlow.collect { event ->
                events += event
            }
        }

    }

   suspend fun publish(event: Any) {
        bus.publish(event)
    }

    fun assertThatEventsContains(
        event: Any,
        atPosition: Int,
    ) {
        Assert.assertEquals(event, events[atPosition])
    }

}
