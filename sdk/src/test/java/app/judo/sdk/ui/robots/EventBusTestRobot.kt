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
