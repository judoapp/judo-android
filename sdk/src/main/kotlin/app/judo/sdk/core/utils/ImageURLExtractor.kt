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

/**
 * A [Visitor] that returns all URLs that point to images
 *
 *  See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
class ImageURLExtractor {

    private var accumulator: MutableSet<String> = mutableSetOf()

    private val visitor = object : Visitor<MutableSet<String>> {

        override fun visit(host: BarBackground): MutableSet<String> {
            return (host as? BarBackground.ImageBarBackground)?.accept(this) ?: getDefault()
        }

        override fun visit(host: Carousel): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: Experience): MutableSet<String> {
            host.nodes.sortedBy {
                it is Screen && it.id == host.initialScreenID
            }.flatMapTo(accumulator) { node -> node.accept(this) }

            return accumulator
        }

        override fun visit(host: HStack): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: Video): MutableSet<String> {
            val urls = setOfNotNull(host.interpolatedPosterImageURL)
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()

            accumulator.addAll(urls)

            accumulator.addAll(overlay)
            accumulator.addAll(background)

            return accumulator
        }

        override fun visit(host: Image): MutableSet<String> {
            val urls = setOfNotNull(host.interpolatedImageURL, host.interpolatedDarkModeImageURL)
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()

            accumulator.addAll(urls)

            accumulator.addAll(overlay)
            accumulator.addAll(background)

            return accumulator
        }

        override fun visit(host: PageControl): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()

            if (host.style is PageControlStyle.ImagePageControlStyle) {
                val currentImage = host.style.currentImage.accept(this)
                val normalImage = host.style.normalImage.accept(this)
                accumulator.addAll(currentImage)
                accumulator.addAll(normalImage)
            }

            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: Rectangle): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: ScrollContainer): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: Text): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: VStack): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: WebView): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: ZStack): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun visit(host: Audio): MutableSet<String> {
            val background = host.background?.node?.accept(this) ?: getDefault()
            val overlay = host.overlay?.node?.accept(this) ?: getDefault()
            accumulator.addAll(overlay)
            accumulator.addAll(background)
            return accumulator
        }

        override fun getDefault(): MutableSet<String> {
            return accumulator
        }

    }

    fun extract(experiences: List<Experience>): Set<String> {
        accumulator = mutableSetOf()

        experiences.fold(accumulator) { acc, judo ->
            acc += visitor.visit(judo)
            acc
        }

        return accumulator
    }

}