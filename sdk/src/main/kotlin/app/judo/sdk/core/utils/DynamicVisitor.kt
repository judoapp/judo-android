package app.judo.sdk.core.utils

import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection

/**
 * A [Visitor] that inspects nodes.
 */
internal class DynamicVisitor(
    initialize: DynamicVisitor.() -> Unit = {}
) : Visitor<Unit> {

    private val behaviors = mutableListOf<DynamicVisitor.(Any) -> Unit>()

    init {
        initialize()
    }

    override fun visit(host: AppBar) {
        visitElement(host)
    }

    override fun visit(host: BarBackground) {
        visitElement(host)
    }

    override fun visit(host: Border) {
        visitElement(host)
    }

    override fun visit(host: Carousel) {
        visitElement(host)
    }

    override fun visit(host: Experience) {
        visitElement(host)
    }

    override fun visit(host: Divider) {
        visitElement(host)
    }

    override fun visit(host: HStack) {
        visitElement(host)
    }

    override fun visit(host: Image) {
        visitElement(host)
    }

    override fun visit(host: MenuItem) {
        visitElement(host)
    }

    override fun visit(host: MenuItemIcon) {
        visitElement(host)
    }

    override fun visit(host: PageControl) {
        visitElement(host)
    }

    override fun visit(host: Rectangle) {
        visitElement(host)
    }

    override fun visit(host: Screen) {
        visitElement(host)
    }

    override fun visit(host: ScrollContainer) {
        visitElement(host)
    }

    override fun visit(host: Spacer) {
        visitElement(host)
    }

    override fun visit(host: Text) {
        visitElement(host)
    }

    override fun visit(host: VStack) {
        visitElement(host)
    }

    override fun visit(host: WebView) {
        visitElement(host)
    }

    override fun visit(host: ZStack) {
        visitElement(host)
    }

    override fun visit(host: EmptyNode) {
        visitElement(host)
    }

    override fun visit(host: Audio) {
        visitElement(host)
    }

    override fun visit(host: Video) {
        visitElement(host)
    }

    override fun visit(host: Shadow) {
        visitElement(host)
    }

    override fun visit(host: Collection) {
        visitElement(host)
    }

    override fun visit(host: DataSource) {
        visitElement(host)
    }

    override fun visit(host: NamedIcon) {
        visitElement(host)
    }

    override fun getDefault() {
        /* no-op */
    }

    inline fun <reified T> on(crossinline behavior: DynamicVisitor.(element: T) -> Unit) {
        behaviors += { element ->
            if (element is T) behavior(element)
        }
    }

    private fun visitElement(element: Any) {
        behaviors.forEach { behavior -> behavior(element) }
    }

}