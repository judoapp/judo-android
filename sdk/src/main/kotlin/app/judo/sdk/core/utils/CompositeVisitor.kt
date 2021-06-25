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
 * A [Visitor] that injects all the [Text.typeface] values.
 *
 *  See also: [Visitor Wiki](https://en.wikipedia.org/wiki/Visitor_pattern)
 */
internal class CompositeVisitor<R> : Visitor<List<R>> {

    private val visitors = mutableListOf<Visitor<R>>()

    private val accumulator = mutableListOf<R>()

    override fun visit(host: AppBar): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: BarBackground): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Border): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Carousel): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Experience): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Divider): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: HStack): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Image): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: MenuItem): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: PageControl): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Rectangle): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Screen): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: ScrollContainer): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Spacer): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Text): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: VStack): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: WebView): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: ZStack): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: EmptyNode): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Audio): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Video): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun visit(host: Shadow): List<R> {
        visitors.forEach {
            accumulator.add(host.accept(it))
        }
        return accumulator
    }

    override fun getDefault(): List<R> {
        return accumulator
    }

    fun add(visitor: Visitor<R>) {
        visitors += visitor
    }

}

internal inline fun <reified R> visitorsOf(vararg visitors: Visitor<R>): CompositeVisitor<R> {
    return CompositeVisitor<R>().apply {
        visitors.forEach { add(it) }
    }
}