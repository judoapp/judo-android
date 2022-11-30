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

import app.judo.sdk.api.Judo
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.core.cache.KeyValueCache
import app.judo.sdk.core.events.EventBus
import app.judo.sdk.core.interpolation.ProtoInterpolator
import app.judo.sdk.core.log.Logger
import app.judo.sdk.core.repositories.ExperienceRepository
import app.judo.sdk.core.repositories.ExperienceTreeRepository
import app.judo.sdk.core.services.*
import kotlinx.coroutines.CoroutineDispatcher

internal interface MutableEnvironment: Environment {

    override var configuration: Judo.Configuration

    override var baseURL: String?

    override var cachePath: String

    override var imageCacheSize: Long

    override var logger: Logger

    override var eventBus: EventBus

    override var profileService: ProfileService

    override var keyValueCache: KeyValueCache

    override var interpolator: ProtoInterpolator

    override var imageService: ImageService

    override var experienceService: ExperienceService

    override var fontResourceService: FontResourceService

    override var dataSourceService: DataSourceService

    override var ingestService: IngestService

    override var experienceRepository: ExperienceRepository

    override var experienceTreeRepository: ExperienceTreeRepository

    override var experienceFragmentFactory: ExperienceFragmentFactory

    override var eventQueue: AnalyticsServiceScope

    override var ioDispatcher: CoroutineDispatcher

    override var mainDispatcher: CoroutineDispatcher

    override var defaultDispatcher: CoroutineDispatcher
}