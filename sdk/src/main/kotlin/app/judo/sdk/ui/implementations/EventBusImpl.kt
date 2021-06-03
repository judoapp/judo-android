package app.judo.sdk.ui.implementations

import app.judo.sdk.core.events.EventBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventBusImpl : EventBus {

    private val backingEventFlow = MutableSharedFlow<Any>()

    override val eventFlow: SharedFlow<Any> = backingEventFlow.asSharedFlow()

    override suspend fun publish(event: Any) {
        backingEventFlow.emit(event)
    }

}