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

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Collection(
    override val id: String,
    override val metadata: Metadata? = null,
    val childIDs: List<String>,
    val filters: List<Condition>,
    val keyPath: String,
    override val name: String? = null,
    val sortDescriptors: List<SortDescriptor>,
    val limit: Limit? = null,
) : NodeContainer {

    override val typeName: String = NodeType.COLLECTION.code

    override fun getChildNodeIDs(): List<String> {
        return childIDs
    }

    @Transient
    internal var items: List<Any?>? = null

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

}