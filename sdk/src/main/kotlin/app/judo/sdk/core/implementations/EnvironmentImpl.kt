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
import androidx.lifecycle.ProcessLifecycleOwner
import app.judo.sdk.api.Judo
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.MutableEnvironment
import app.judo.sdk.core.events.EventBus
import app.judo.sdk.core.interpolation.ProtoInterpolator
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.repositories.ExperienceRepository
import app.judo.sdk.core.repositories.ExperienceTreeRepository
import app.judo.sdk.core.services.*
import app.judo.sdk.core.web.Http
import app.judo.sdk.core.web.JudoCallInterceptor
import app.judo.sdk.ui.ExperienceFragment
import app.judo.sdk.ui.ExperienceFragment.Companion.applyArguments
import app.judo.sdk.ui.implementations.EventBusImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class EnvironmentImpl(
    context: Context
) : MutableEnvironment {

    override var configuration: Judo.Configuration = Judo.Configuration(
        "TODO",
        "TODO"
    )

    override var baseURL: String? = null

    override var cachePath: String = context.cacheDir.path

    override var imageCacheSize: Long = Environment.Sizes.IMAGE_CACHE_SIZE

    override var logger: Logger = ProductionLoggerImpl()

    override var eventBus: EventBus = EventBusImpl()

    override var profileService: ProfileService = ProfileServiceImpl(this)

    override var keyValueCache: KeyValueCache = KeyValueCacheImpl(
        context = context
    )

    private val packageInfo = context.packageManager.getPackageInfo(
        context.packageName,
        0
    )

    var packageName = packageInfo.packageName

    var appVersion = packageInfo.versionName

    var baseClient = Http.coreClient(
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
        },
    )

    var judoClient = baseClient.newBuilder().apply {
       addInterceptor(JudoCallInterceptor(
           accessTokenSupplier = { configuration.accessToken },
           deviceIdSupplier = { keyValueCache.retrieveString(Environment.Keys.DEVICE_ID) ?: "TODO" },
           loggerSupplier = { logger },
           httpAgent = System.getProperty("http.agent") ?: "",
           clientPackageName = { packageName },
           appVersion = { appVersion },
       ))
    }.build()

    override var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    override var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    override var interpolator: ProtoInterpolator = InterpolatorImpl(loggerSupplier = { logger })

    override var imageService: ImageService = ImageServiceImpl(
        context = context,
        clientSupplier = { judoClient },
        baseURLSupplier = { baseURL },
        imageCachePathSupplier = { cachePath },
        cacheSizeSupplier = { imageCacheSize }
    )

    override var experienceService: ExperienceService = ExperienceServiceImpl(
        clientSupplier = { judoClient },
        baseURLSupplier = { baseURL },
    )

    override var fontResourceService: FontResourceService = FontResourceServiceImpl(
        cachePathSupplier = { cachePath },
        clientSupplier = { judoClient },
        baseURLSupplier = { baseURL }
    )

    override var dataSourceService: DataSourceService = DataSourceServiceImpl(
        baseClientSupplier = { baseClient },
        baseURLSupplier = { baseURL },
        loggerSupplier = { logger },
        authorizersSupplier = { configuration.authorizers },
    )

    override var ingestService: IngestService = IngestServiceImpl(
        baseClientSupplier = { judoClient },
        loggerSupplier = { logger },
        ioDispatcherSupplier = { ioDispatcher }
    )

    private val experienceRepositoryImpl = ExperienceRepositoryImpl(
        experienceServiceSupplier = { experienceService }
    ) {
        fontResourceService
    }

    override var experienceRepository: ExperienceRepository = experienceRepositoryImpl

    override var experienceTreeRepository: ExperienceTreeRepository = experienceRepositoryImpl

    override var experienceFragmentFactory = ExperienceFragmentFactory { intent ->
        ExperienceFragment().applyArguments(intent)
    }

    override var eventQueue: AnalyticsServiceScope = AnalyticsServiceImpl(
        environment = this,
        deviceIdSupplier = { keyValueCache.retrieveString(Environment.Keys.DEVICE_ID) ?: "TODO" },
        processLifecycleSupplier = { ProcessLifecycleOwner.get() }
    )

}
