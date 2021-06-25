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

import android.graphics.Typeface
import app.judo.sdk.api.models.*

/**
 * A [Visitor] that injects all the [Text.typeface] values.
 *
 *  See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
internal class TypeFaceLoader(
    private val typefaces: Map<String, Typeface>,
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
        (host.font as? Font.Custom)?.let { font ->
            host.typeface = typefaces[font.fontName]
        }
    }

    override fun visit(host: AppBar) {
        (host.titleFont as? Font.Custom)?.let { font ->
            host.typeface = typefaces[font.fontName]
        }
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

    override fun getDefault() {
        /* no-op */
    }

    fun loadTypefacesInto(experience: Experience) {
        experience.accept(this)
    }

}