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

package app.judo.sdk.core.environment

import app.judo.sdk.BuildConfig
import app.judo.sdk.api.Judo
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.events.EventBus
import app.judo.sdk.core.lang.Tokenizer
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.repositories.ExperienceRepository
import app.judo.sdk.core.repositories.ExperienceTreeRepository
import app.judo.sdk.core.repositories.SyncRepository
import app.judo.sdk.core.services.*
import kotlinx.coroutines.CoroutineDispatcher

internal interface Environment {

    object Keys {
        const val IGNORE_CACHE: String = "ignore-cache"
        const val LOAD_FROM_MEMORY: String = "load-from-memory"
        const val EXPERIENCE_KEY: String = "experience-key"
        const val EXPERIENCE_URL: String = "experience-url"
        const val PREFERENCES_NAME: String = "judo-sdk-preferences"
        const val DEVICE_ID: String = "device-id"
        const val MESSAGE: String = "judo"
        const val SCREEN_ID: String = "screenID"
        const val EXPERIENCE_INTENT = "experience-intent"
        const val USER_INFO_OVERRIDE = "user-info-override"
    }

    object RegexPatterns {
        const val HANDLE_BAR_EXPRESSION_PATTERN: String = """\{\{.*\}\}"""
    }

    object Sizes {
        const val EXPERIENCE_CACHE_SIZE: Long = 250L * 1024L * 1024L // 256 MB
        const val IMAGE_CACHE_SIZE: Long = 250L * 1024L * 1024L // 256 MB
        const val FONT_CACHE_SIZE: Long = 50L * 1024L * 1024L // 50 MB
    }

    companion object

    val configuration: Judo.Configuration

    val baseURL: String?

    val cachePath: String

    val experienceCacheSize: Long

    val imageCacheSize: Long

    val logger: Logger

    val eventBus: EventBus

    val profileService: ProfileService

    val keyValueCache: KeyValueCache

    val imageService: ImageService

    val experienceService: ExperienceService

    val fontResourceService: FontResourceService

    val syncService: SyncService

    val pushTokenService: PushTokenService

    val dataSourceService: DataSourceService

    val ingestService: IngestService

    val experienceRepository: ExperienceRepository

    val experienceTreeRepository: ExperienceTreeRepository

    val syncRepository: SyncRepository

    val experienceFragmentFactory: ExperienceFragmentFactory

    val eventQueue: AnalyticsServiceScope

    val ioDispatcher: CoroutineDispatcher

    val mainDispatcher: CoroutineDispatcher

    val defaultDispatcher: CoroutineDispatcher

    var tokenizer: Tokenizer

}