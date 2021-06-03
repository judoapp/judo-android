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

        override fun visit(host: AppBar): MutableSet<String> {
            return host.menuItems?.fold(accumulator) { acc, menuItem ->
                acc.addAll(menuItem.accept(this))
                acc
            } ?: accumulator
        }

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

        override fun visit(host: MenuItem): MutableSet<String> {
            return host.icon?.accept(this) ?: getDefault()
        }

        override fun visit(host: MenuItemIcon): MutableSet<String> {
            return (host as? MenuItemIcon.AnImage)?.accept(this) ?: getDefault()
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

        override fun visit(host: Screen): MutableSet<String> {
            return host.appBar?.accept(this) ?: getDefault()
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