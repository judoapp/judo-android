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

package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.data.dataContextOf
import app.judo.sdk.core.data.fromKeyPath
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.lang.Keyword

internal fun Collection.limit() {

    val (amountToDisplay, startAt) = limit ?: return
    val items = items.takeUnless { it.isNullOrEmpty() }  ?: return

    this.items = items
        .subList(
            fromIndex = startAt.dec(),
            toIndex = items.size
        )
        .take(amountToDisplay)

}

internal fun Collection.filter(userInfo: Map<String, Any>, urlParams: Map<String, String>) {
    if (filters.isNotEmpty()) {
        this.items = this.items?.filter { value ->
            val itemDataContext = dataContextOf(
                Keyword.USER.value to userInfo,
                Keyword.DATA.value to value,
                Keyword.URL.value to urlParams
            )

            return@filter this.filters.resolve(
                itemDataContext, InterpolatorImpl(dataContext = itemDataContext)
            )
        }
    }
}

internal fun Collection.sort(
    userInfo: Map<String, Any>,
    urlParams: Map<String, String>
) {
    sortDescriptors.forEach { sortDescriptor ->
        items = items?.sortedWith { o1, o2 ->
            val v1 = dataContextOf(
                Keyword.USER.value to userInfo,
                Keyword.DATA.value to o1,
                Keyword.USER.value to urlParams
            ).fromKeyPath(sortDescriptor.keyPath)

            val v2 = dataContextOf(
                Keyword.USER.value to userInfo,
                Keyword.DATA.value to o2,
                Keyword.USER.value to urlParams
            ).fromKeyPath(sortDescriptor.keyPath)

            if (sortDescriptor.ascending) {
                when (v1) {
                    v2 -> 0
                    null -> 1
                    else -> -1
                }

            } else {
                when (v1) {
                    v2 -> 0
                    null -> -1
                    else -> 1
                }
            }
        }
    }
}
