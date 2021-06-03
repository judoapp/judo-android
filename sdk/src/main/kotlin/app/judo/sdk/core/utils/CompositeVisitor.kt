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

    override fun visit(host: MenuItemIcon): List<R> {
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