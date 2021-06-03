package app.judo.sdk.core.environment

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

internal interface MutableEnvironment: Environment {

    override var accessToken: String

    override var domainNames: Set<String>

    override var baseURL: String?

    override var cachePath: String

    override var experienceCacheSize: Long

    override var imageCacheSize: Long

    override var logger: Logger

    override var eventBus: EventBus

    override var userDataSupplier: UserDataSupplier

    override var keyValueCache: KeyValueCache

    override var imageService: ImageService

    override var experienceService: ExperienceService

    override var fontResourceService: FontResourceService

    override var syncService: SyncService

    override var devicesService: DevicesService

    override var dataSourceService: DataSourceService

    override var experienceRepository: ExperienceRepository

    override var syncRepository: SyncRepository

    override var experienceFragmentFactory: ExperienceFragmentFactory

    override var ioDispatcher: CoroutineDispatcher

    override var mainDispatcher: CoroutineDispatcher

    override var defaultDispatcher: CoroutineDispatcher
}