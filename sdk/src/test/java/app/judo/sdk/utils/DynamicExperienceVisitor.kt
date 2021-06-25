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

package app.judo.sdk.utils

import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection

/**
 * A [Visitor] that inspects nodes.
 */
internal class DynamicExperienceVisitor(
    initialize: DynamicExperienceVisitor.() -> Unit = {}
) {

    private var backingExperience: Experience? = null

    private val experience
        get() = backingExperience!!

    private val behaviors = mutableListOf<DynamicExperienceVisitor.(Any) -> Unit>()

    init {
        initialize()
    }

    private val visitor = object : Visitor<Unit> {
        override fun visit(host: BarBackground) {
            visitElement(host)
        }

        override fun visit(host: Border) {
            visitElement(host)
        }

        override fun visit(host: Carousel) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: Experience) {
            visitElement(host)
            host.nodes.forEach { it.accept(this) }
        }

        override fun visit(host: Divider) {
            visitElement(host)
        }

        override fun visit(host: HStack) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: Image) {
            visitElement(host)
            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: PageControl) {
            visitElement(host)
        }

        override fun visit(host: Rectangle) {
            visitElement(host)
            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: Screen) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }
        }

        override fun visit(host: ScrollContainer) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: Spacer) {
            visitElement(host)
        }

        override fun visit(host: Text) {
            visitElement(host)
            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: VStack) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: WebView) {
            visitElement(host)
            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: ZStack) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: EmptyNode) {
            visitElement(host)
        }

        override fun visit(host: Audio) {
            visitElement(host)
            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: Video) {
            visitElement(host)
            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.shadow?.accept(this)
            host.mask?.accept(this)
        }

        override fun visit(host: Shadow) {
            visitElement(host)
        }

        override fun visit(host: Collection) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }

        }

        override fun visit(host: DataSource) {
            visitElement(host)

            experience.nodes
                .filter { node ->
                    host.childIDs.contains(node.id)
                }
                .forEach { node ->
                    node.accept(this)
                }

        }

        override fun visit(host: Icon) {
            visitElement(host)
        }

        override fun getDefault() {
            /* no-op */
        }

    }

    inline fun <reified T> on(crossinline behavior: DynamicExperienceVisitor.(element: T) -> Unit) {
        behaviors += { element ->
            if (element is T) behavior(element)
        }
    }

    private fun visitElement(element: Any) {
        behaviors.forEach { behaviour -> behaviour(element) }
    }

    fun visit(experience: Experience) {

        backingExperience = experience

        experience.accept(visitor)

    }

}