package app.judo.sdk.core.environment

import app.judo.sdk.BuildConfig
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.events.EventBus
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.repositories.ExperienceRepository
import app.judo.sdk.core.repositories.SyncRepository
import app.judo.sdk.core.services.*
import app.judo.sdk.core.services.ExperienceService
import app.judo.sdk.core.services.DevicesService
import app.judo.sdk.core.services.SyncService
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
    }

    object RegexPatterns {
        const val HANDLE_BAR_EXPRESSION_PATTERN: String = """\{\{[^}]*\}\}"""
    }

    object Sizes {
        const val EXPERIENCE_CACHE_SIZE: Long = 250L * 1024L * 1024L // 256 MB
        const val IMAGE_CACHE_SIZE: Long = 250L * 1024L * 1024L // 256 MB
    }

    companion object {
        val Type: String = if (BuildConfig.DEBUG) "DEVELOPMENT" else "PRODUCTION"
    }

    val accessToken: String

    val domainNames: Set<String>

    val baseURL: String?

    val cachePath: String

    val experienceCacheSize: Long

    val imageCacheSize: Long

    val logger: Logger

    val eventBus: EventBus

    val userDataSupplier: UserDataSupplier

    val keyValueCache: KeyValueCache

    val imageService: ImageService

    val experienceService: ExperienceService

    val fontResourceService: FontResourceService

    val syncService: SyncService

    val devicesService: DevicesService

    val dataSourceService: DataSourceService

    val experienceRepository: ExperienceRepository

    val syncRepository: SyncRepository

    val experienceFragmentFactory: ExperienceFragmentFactory

    val ioDispatcher: CoroutineDispatcher

    val mainDispatcher: CoroutineDispatcher

    val defaultDispatcher: CoroutineDispatcher

}