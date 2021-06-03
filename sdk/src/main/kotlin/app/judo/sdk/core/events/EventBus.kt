package app.judo.sdk.core.events

import kotlinx.coroutines.flow.SharedFlow

interface EventBus {
    val eventFlow: SharedFlow<Any>

    suspend fun publish(event: Any)
}