package app.judo.sdk.core.utils

import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.data.JsonDAO
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.log.Logger

/**
 * A [Visitor] that injects all the [Text.typeface] values.
 *
 *  See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
internal class ScreenInterpolatorLoader(
    private val experience: Experience,
    private val screenId: String,
    private val loggerSupplier: () -> Logger? = { null },
    private val userDataSupplier: () -> Map<String, String> = { emptyMap() },
) {

    private val screen: Screen =
        experience.nodes<Screen>().first {
            it.id == screenId
        }

    private val visitor = object : Visitor<Unit> {

        private var currentDAO: JsonDAO? = null

        override fun visit(host: Screen) {

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            // TODO: 2021-05-14 Visit AppBar
//            host.appBar?.accept(this)

        }

        override fun visit(host: DataSource) {

            val previousDAO = currentDAO

            currentDAO = host.jsonDAO

            val children = experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }

            children.forEach {
                it.accept(this)
            }

            currentDAO = previousDAO
        }

        override fun visit(host: Collection) {
            host.jsonDAOs = currentDAO?.findArrayByKey(host.dataKey)
        }

        override fun visit(host: AppBar) {
            if (host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            } else if (host.interpolator!!.jsonDAO == null) {
                host.interpolator!!.jsonDAO = currentDAO
            }

        }

        override fun visit(host: ScrollContainer) {

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: Carousel) {

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: VStack) {

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: HStack) {

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: ZStack) {

            experience.nodes.filter { node ->
                host.childIDs.contains(node.id)
            }.forEach {
                it.accept(this)
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: Text) {
            if (host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            } else if (host.interpolator!!.jsonDAO == null) {
                host.interpolator!!.jsonDAO = currentDAO
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: Audio) {
            if (host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            } else if (host.interpolator!!.jsonDAO == null) {
                host.interpolator!!.jsonDAO = currentDAO
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: Video) {
            if (host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            } else if (host.interpolator!!.jsonDAO == null) {
                host.interpolator!!.jsonDAO = currentDAO
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: Image) {
            if (host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            } else if (host.interpolator!!.jsonDAO == null) {
                host.interpolator!!.jsonDAO = currentDAO
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: PageControl) {

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: Rectangle) {

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: WebView) {
            if (host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            } else if (host.interpolator!!.jsonDAO == null) {
                host.interpolator!!.jsonDAO = currentDAO
            }

            host.background?.node?.accept(this)
            host.overlay?.node?.accept(this)
            host.mask?.accept(this)

        }

        override fun visit(host: MenuItem) {
            if (host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            } else if (host.interpolator!!.jsonDAO == null) {
                host.interpolator!!.jsonDAO = currentDAO
            }

        }

        override fun visit(host: MenuItemIcon) {
            if (host is MenuItemIcon.AnImage && host.interpolator == null) {
                host.interpolator = InterpolatorImpl(
                    jsonDAO = currentDAO,
                    loggerSupplier = loggerSupplier,
                    userDataSupplier = userDataSupplier
                )
            }

        }

        override fun getDefault() {
            /* no-op */
        }

    }

    fun load() {

        screen.accept(visitor)

    }

}