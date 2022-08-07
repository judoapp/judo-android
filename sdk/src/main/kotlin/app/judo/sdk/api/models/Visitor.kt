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
    fun visit(host: Conditional): R = getDefault()
    fun visit(host: DataSource): R = getDefault()
    fun visit(host: Experience): R = getDefault()
    fun visit(host: Divider): R = getDefault()
    fun visit(host: HStack): R = getDefault()
    fun visit(host: Image): R = getDefault()
    fun visit(host: MenuItem): R = getDefault()
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
    fun visit(host: Icon): R = getDefault()
    fun visit(host: Shadow): R = getDefault()
    fun visit(host: ErasedNode): R = getDefault()
}