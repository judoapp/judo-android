package app.judo.sdk.core.utils

import app.judo.sdk.api.models.*

/**
 * A [Visitor] that injects all the [Text.typeface] values.
 *
 *  See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
internal class TranslationLoader(
    private val translator: Translator,
) : Visitor<Unit> {

    override fun visit(host: Carousel) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: Experience) {
        host.nodes.forEach { it.accept(this) }
    }

    override fun visit(host: HStack) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: Image) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: PageControl) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: Rectangle) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: ScrollContainer) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: Text) {
        host.translator = translator
    }

    override fun visit(host: VStack) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: WebView) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: ZStack) {
        host.background?.node?.accept(this)
        host.overlay?.node?.accept(this)
    }

    override fun visit(host: AppBar) {
        host.translator = translator
        host.menuItems?.forEach { it.accept(this) }
    }

    override fun visit(host: MenuItem) {
        host.translator = translator
    }

    override fun visit(host: Screen) {
        host.appBar?.accept(this)
    }

    override fun getDefault() {
        /* no-op */
    }

}