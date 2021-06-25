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

import android.content.Context
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.data.UserInfoSupplier
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.MutableEnvironment
import app.judo.sdk.core.events.EventBus
import app.judo.sdk.core.lang.Tokenizer
import app.judo.sdk.core.lang.TokenizerImpl
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.repositories.ExperienceRepository
import app.judo.sdk.core.repositories.ExperienceTreeRepository
import app.judo.sdk.core.repositories.SyncRepository
import app.judo.sdk.core.services.*
import app.judo.sdk.core.web.Http
import app.judo.sdk.ui.ExperienceFragment
import app.judo.sdk.ui.ExperienceFragment.Companion.applyArguments
import app.judo.sdk.ui.implementations.EventBusImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class EnvironmentImpl(
    context: Context
) : MutableEnvironment {

    override var accessToken: String = "TODO"

    override var domainNames: Set<String> = emptySet()

    override var baseURL: String? = null

    override var cachePath: String = context.cacheDir.path

    override var experienceCacheSize: Long = Environment.Sizes.EXPERIENCE_CACHE_SIZE

    override var imageCacheSize: Long = Environment.Sizes.IMAGE_CACHE_SIZE

    override var logger: Logger = ProductionLoggerImpl()

    override var eventBus: EventBus = EventBusImpl()

    override var userInfoSupplier: UserInfoSupplier = UserInfoSupplier {
        emptyMap()
    }

    override var keyValueCache: KeyValueCache = KeyValueCacheImpl(
        context = context
    )

    override var tokenizer: Tokenizer = TokenizerImpl()

    var baseClient = Http.coreClient(
        accessTokenSupplier = { accessToken },
        deviceIdSupplier = { keyValueCache.retrieveString(Environment.Keys.DEVICE_ID) ?: "TODO" },
        loggerSupplier = { logger },
        cookieJarSupplier = {
            CookieJarImpl(
                loggerSupplier = {
                    logger
                },
                keyValueCacheSupplier = {
                    keyValueCache
                }
            )
        }
    )

    override var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    override var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    override var imageService: ImageService = ImageServiceImpl(
        context = context,
        clientSupplier = { baseClient },
        baseURLSupplier = { baseURL },
        imageCachePathSupplier = { cachePath },
        cacheSizeSupplier = { imageCacheSize }
    )

    override var devicesService: DevicesService = DevicesServiceImpl(
        baseClientSupplier = { baseClient },
        baseURLSupplier = { baseURL }
    )

    override var experienceService: ExperienceService = ExperienceServiceImpl(
        cachePathSupplier = { cachePath },
        clientSupplier = { baseClient },
        baseURLSupplier = { baseURL },
        cacheSizeSupplier = { experienceCacheSize }
    )

    override var fontResourceService: FontResourceService = FontResourceServiceImpl(
        cachePathSupplier = { cachePath },
        clientSupplier = { baseClient },
        baseURLSupplier = { baseURL }
    )

    override var syncService: SyncService = SyncServiceImpl(
        baseClientSupplier = { baseClient },
        baseURLSupplier = { baseURL }
    )

    override var dataSourceService: DataSourceService = DataSourceServiceImpl(
        baseClientSupplier = { baseClient },
        baseURLSupplier = { baseURL },
        loggerSupplier = { logger }
    )

    private val experienceRepositoryImpl = ExperienceRepositoryImpl(
        experienceServiceSupplier = { experienceService },
        fontResourceServiceSupplier = {
            fontResourceService
        }
    ) {
        logger
    }

    override var experienceRepository: ExperienceRepository = experienceRepositoryImpl

    override var experienceTreeRepository: ExperienceTreeRepository = experienceRepositoryImpl

    override var syncRepository: SyncRepository = SyncRepositoryImpl(
        syncServiceSupplier = { syncService },
        keyValueCacheSupplier = { keyValueCache }
    )

    override var experienceFragmentFactory = ExperienceFragmentFactory { intent ->
        ExperienceFragment().applyArguments(intent)
    }


}