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

package app.judo.sdk.compose.model.nodes


import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.ui.data.*
import app.judo.sdk.compose.ui.data.fromKeyPath
import app.judo.sdk.compose.ui.data.makeDataContext
import app.judo.sdk.compose.ui.data.userInfo
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
internal data class Collection(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    override val opacity: Float? = null,
    override val aspectRatio: Float? = null,
    override val frame: Frame? = null,
    override val padding: Padding? = null,
    override val layoutPriority: Int? = null,
    override val offset: Point? = null,
    override val shadow: Shadow? = null,
    override val mask: Node? = null,
    override val accessibility: Accessibility? = null,
    override val overlay: Overlay? = null,
    override val background: Background? = null,
    override val action: Action? = null,
    override var children: List<Node> = emptyList(),
    override val childIDs: List<String> = emptyList(),
    val filters: List<Condition>,
    val keyPath: String,
    val sortDescriptors: List<SortDescriptor>,
    val limit: Limit? = null,
) : Node {
    override val typeName: String = NodeType.COLLECTION.code
}

internal fun Collection.getItems(dataContext: DataContext): List<Any?> {
    var items = dataContext.fromKeyPath(keyPath) as? List<Any?> ?: return emptyList()

    filters.forEach { condition ->
        items = items.filter { data ->
            val itemContext = makeDataContext(
                userInfo = dataContext.userInfo,
                urlParameters = dataContext.urlParameters,
                data = data
            )
            condition.isSatisfied(itemContext)
        }
    }

    if (sortDescriptors.isNotEmpty()) {
        items = this.sort(dataContext.userInfo, dataContext.urlParameters, items)
    }

    return limit?.let {
        items
            .drop(it.startAt - 1)
            .take(it.show)
    } ?: items
}

internal fun Collection.sort(
    userInfo: Map<String, Any>,
    urlParams: Map<String, String>,
    items: List<Any?>
): List<Any?> {
    val result = items.toMutableList()
    sortDescriptors.forEach { sortDescriptor ->
        result.sortWith { a, b ->
            val v1 = makeDataContext(
                userInfo,
                urlParams,
                a
            ).fromKeyPath(sortDescriptor.keyPath)
            val v2 = makeDataContext(
                userInfo,
                urlParams,
                b
            ).fromKeyPath(sortDescriptor.keyPath)

            var compare: Int = -1

            if (v1 is Int && v2 is Int) {
                compare = v1.compareTo(v2)
            } else if (v1 is Double && v2 is Double) {
                compare = v1.compareTo(v2)
            } else if (v1 is String && v2 is String) {
                compare = v1.compareTo(v2)
            } else if (v1 is Boolean && v2 is Boolean) {
                compare = v1.compareTo(v2)
            } else if (v1 is Date && v2 is Date) {
                compare = v1.compareTo(v2)
            }

            if (!sortDescriptor.ascending) {
                compare *= -1
            }

            compare
        }
    }
    return result
}
