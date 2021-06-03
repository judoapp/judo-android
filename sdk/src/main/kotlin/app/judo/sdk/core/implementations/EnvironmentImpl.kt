package app.judo.sdk.core.implementations

import android.content.Context
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.MutableEnvironment
import app.judo.sdk.core.events.EventBus
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.repositories.ExperienceRepository
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

    override var userDataSupplier: UserDataSupplier = UserDataSupplier {
        emptyMap()
    }

    override var keyValueCache: KeyValueCache = KeyValueCacheImpl(
        context = context
    )

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

    override var experienceRepository: ExperienceRepository = ExperienceRepositoryImpl(
        experienceServiceSupplier = { experienceService },
        fontResourceServiceSupplier = {
            fontResourceService
        }
    ) {
        logger
    }

    override var syncRepository: SyncRepository = SyncRepositoryImpl(
        syncServiceSupplier = { syncService },
        keyValueCacheSupplier = { keyValueCache }
    )

    override var experienceFragmentFactory = ExperienceFragmentFactory { intent ->
        ExperienceFragment().applyArguments(intent)
    }


}