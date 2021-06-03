package app.judo.sdk.api.models

/**
 * A Standard [Visitable] interface.
 * @see [Visitor]
 * See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
interface Visitable {
    fun <R> accept(visitor: Visitor<R>): R
}

/**
 * A Standard [Visitor] for all the [Visitable] models in a [Experience].
 *
 * See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
interface Visitor<R> {

    /**
     * Returns a default value for any [Visitor] visit functions that have no override.
     *
     * AKA the super function of visit.
     *
     *  See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
     */
    fun getDefault(): R

    fun visit(host: AppBar): R = getDefault()
    fun visit(host: BarBackground): R = getDefault()
    fun visit(host: Border): R = getDefault()
    fun visit(host: Carousel): R = getDefault()
    fun visit(host: Collection): R = getDefault()
    fun visit(host: DataSource): R = getDefault()
    fun visit(host: Experience): R = getDefault()
    fun visit(host: Divider): R = getDefault()
    fun visit(host: HStack): R = getDefault()
    fun visit(host: Image): R = getDefault()
    fun visit(host: MenuItem): R = getDefault()
    fun visit(host: MenuItemIcon): R = getDefault()
    fun visit(host: PageControl): R = getDefault()
    fun visit(host: Rectangle): R = getDefault()
    fun visit(host: Screen): R = getDefault()
    fun visit(host: ScrollContainer): R = getDefault()
    fun visit(host: Spacer): R = getDefault()
    fun visit(host: Text): R = getDefault()
    fun visit(host: VStack): R = getDefault()
    fun visit(host: WebView): R = getDefault()
    fun visit(host: ZStack): R = getDefault()
    fun visit(host: EmptyNode): R = getDefault()
    fun visit(host: Audio): R = getDefault()
    fun visit(host: Video): R = getDefault()
    fun visit(host: NamedIcon): R = getDefault()
    fun visit(host: Shadow): R = getDefault()
}