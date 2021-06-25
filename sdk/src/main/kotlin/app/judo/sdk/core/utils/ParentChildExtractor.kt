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

package app.judo.sdk.core.utils

import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection

/**
 * A [Visitor] that injects all the [Text.typeface] values.
 *
 *  See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
internal class ParentChildExtractor(
    private val experience: Experience,
) {

    private val sourcesAndParents = mutableListOf<SourceAndParent>()

    private var previousLocation: Visitable? = null

    private val visitor = object : Visitor<Unit> {

        override fun visit(host: Experience) {

            host.nodes.forEach { it.accept(this) }
        }

        override fun visit(host: DataSource) {

            val last = previousLocation

            previousLocation = host

            sourcesAndParents.add(
                SourceAndParent(
                    value = host,
                    parent = last as? DataSource
                )
            )

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            previousLocation = last

        }

        override fun visit(host: Collection) {
            val last = previousLocation
            previousLocation = host

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            previousLocation = last

        }

        override fun visit(host: Screen) {
            val last = previousLocation
            previousLocation = host

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            previousLocation = last

        }

        override fun visit(host: ScrollContainer) {
            val last = previousLocation
            previousLocation = host

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: Carousel) {
            val last = previousLocation
            previousLocation = host

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: VStack) {
            val last = previousLocation
            previousLocation = host

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: HStack) {
            val last = previousLocation
            previousLocation = host

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: ZStack) {
            val last = previousLocation
            previousLocation = host

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: Text) {
            val last = previousLocation
            previousLocation = host

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: Audio) {
            val last = previousLocation
            previousLocation = host

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: Video) {
            val last = previousLocation
            previousLocation = host

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: Image) {
            val last = previousLocation
            previousLocation = host

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: PageControl) {
            val last = previousLocation
            previousLocation = host

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: Rectangle) {
            val last = previousLocation
            previousLocation = host

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun visit(host: WebView) {
            val last = previousLocation
            previousLocation = host

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

            previousLocation = last

        }

        override fun getDefault() {
            /* no-op */
        }

    }

    fun extract(visitable: Visitable): List<SourceAndParent> {

        previousLocation = null

        sourcesAndParents.clear()

        visitable.accept(visitor)

        return sourcesAndParents.toList()

    }

}

internal data class SourceAndParent(
    val value: DataSource,
    var parent: DataSource? = null,
)
