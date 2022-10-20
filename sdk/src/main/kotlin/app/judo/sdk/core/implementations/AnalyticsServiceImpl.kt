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

import androidx.lifecycle.*
import app.judo.sdk.api.Judo
import app.judo.sdk.api.analytics.AnalyticsEvent
import app.judo.sdk.api.events.Event
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.services.AnalyticsServiceScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

internal class AnalyticsServiceImpl(
    private val environment: Environment,
    private val processLifecycleSupplier: () -> LifecycleOwner,
    private val deviceIdSupplier: () -> String
): AnalyticsServiceScope {
    sealed class Message {
        class EventReceived(val analyticsEvent: AnalyticsEvent): Message()
        class RequestFlush(val minimumQueue: Int?): Message()
    }

    private val messages = MutableSharedFlow<Message>()

    private val analyticsMode get() = environment.configuration.analyticsMode
    private val ioDispatcher get() = environment.ioDispatcher
    private val mainDispatcher get() = environment.mainDispatcher
    private val logger get() = environment.logger
    private val ingestService get() = environment.ingestService
    private val processLifecycle get() = processLifecycleSupplier()
    private val eventBus get() = environment.eventBus
    private val deviceId get() = deviceIdSupplier()
    private val profileService get() = environment.profileService
    private val keyValueCache get() = environment.keyValueCache

    override fun start() {
        processLifecycleSupplier().lifecycle.addObserver(
            object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                fun onBackgrounded() {
                    CoroutineScope(mainDispatcher).launch {
                        requestFlush(null)
                    }
                }
            }
        )

        messages.onEach { message ->
            withContext(ioDispatcher) {
                when (message) {
                    is Message.EventReceived -> {
                        queue = queue.plus(message.analyticsEvent).takeLast(QUEUE_LIMIT)
                    }
                    is Message.RequestFlush -> {
                        val queue = queue
                        if (message.minimumQueue != null && queue.count() < message.minimumQueue) {
                            logger.d(TAG, "Skipping flush, insufficient queue size.")
                            return@withContext
                        }
                        logger.i(TAG, "Flushing analytics events.")
                        val chunks = queue.chunked(100)
                        if (chunks.isEmpty()) {
                            return@withContext
                        }
                        chunks.forEach { eventsToSubmit ->
                            if (ingestService.submitBatch(eventsToSubmit)) {
                                val idsToDrop = HashSet(eventsToSubmit.map { it.id })
                                logger.d(
                                    TAG,
                                    "Dropping ${idsToDrop.count()} events from queue."
                                )
                                this@AnalyticsServiceImpl.queue = this@AnalyticsServiceImpl.queue.filter {
                                    !idsToDrop.contains(it.id)
                                }
                            }
                        }
                        logger.d(TAG, "Flushed ${chunks.count()} chunks of events.")
                    }
                }
            }
        }.launchIn(processLifecycle.lifecycleScope)

        processLifecycle.lifecycleScope.launch {
            eventBus
                .eventFlow
                .collect { event ->
                    when(event) {
                        is Event.ScreenViewed -> {
                            if(analyticsMode == Judo.Configuration.AnalyticsMode.DEFAULT) {
                                enqueue(
                                    AnalyticsEvent.Screen(
                                        UUID.randomUUID().toString(),
                                        profileService.anonymousId,
                                        userIdForEvents,
                                        timestamp,
                                        context,
                                        event.analyticsEventProperties
                                    )
                                )
                            }
                        }
                        is Event.PushTokenUpdated -> {
                            if(analyticsMode != Judo.Configuration.AnalyticsMode.DISABLED) {
                                enqueue(
                                    AnalyticsEvent.Register(
                                        UUID.randomUUID().toString(),
                                        profileService.anonymousId,
                                        userIdForEvents,
                                        timestamp,
                                        context
                                    )
                                )
                            }
                        }
                        is Event.Identified -> {
                            if (analyticsMode == Judo.Configuration.AnalyticsMode.DEFAULT) {
                                enqueue(
                                    AnalyticsEvent.Identify(
                                        UUID.randomUUID().toString(),
                                        profileService.anonymousId,
                                        userIdForEvents,
                                        timestamp,
                                        context,
                                        profileService.traits
                                    )
                                )
                            }
                        }
                    }
                }
        }

        // also run a 30s timer:
        processLifecycleSupplier().lifecycleScope.launchWhenResumed {
            while (true) {
                delay(30000)
                messages.emit(Message.RequestFlush(FOREGROUND_FLUSH_MINIMUM))
            }
        }
    }

    val userIdForEvents: String? get() =
        if (analyticsMode == Judo.Configuration.AnalyticsMode.DEFAULT) profileService.userId else null

    /**
     * The queue storage.
     */
    private var queue: List<AnalyticsEvent>
        get() {
            return try {
                keyValueCache.retrieveString(EVENT_QUEUE_FIELD)
                    ?.let { jsonString ->
                        JsonParser.parseAnalyticsEvents(jsonString)
                    } ?: listOf()
            } catch(exception: java.lang.Exception) {
                logger.e(TAG, "Invalid analytics events queue data in storage, resetting to empty queue (reason: ${exception.message}).")
                listOf()
            }
        }
        set(value) {
            val jsonString = JsonParser.encodeAnalyticsEvents(value)
            keyValueCache.putString(Pair(EVENT_QUEUE_FIELD, jsonString))
        }

    private val context: AnalyticsEvent.Context
        get() = AnalyticsEvent.Context(
            AnalyticsEvent.Context.Device(
                deviceId,
                // We no longer send push tokens.
                null
            ),
            Locale.getDefault().toString()
        )

    private val localTimeWithOffset8601Format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

    private val timestamp: String
        get() = localTimeWithOffset8601Format.format(Calendar.getInstance().time)

    suspend fun enqueue(event: AnalyticsEvent) {
        messages.emit(Message.EventReceived(event))
    }

    suspend fun requestFlush(minimumQueue: Int?) {
        messages.emit(Message.RequestFlush(minimumQueue))
    }

    companion object {
        private const val EVENT_QUEUE_FIELD = "EVENT_QUEUE"
        private const val QUEUE_LIMIT = 1000
        private const val FOREGROUND_FLUSH_MINIMUM = 20
        private const val TAG = "EventQueueImpl"
    }
}

private val Event.ScreenViewed.analyticsEventProperties: AnalyticsEvent.Screen.Properties
    get() {
        return AnalyticsEvent.Screen.Properties(
            this.screen.id,
            this.screen.name ?: "Screen",
            this.experience.id,
            this.experience.name,
            this.experience.revisionID
        )
    }