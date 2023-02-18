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
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Audio(
    override val id: String,
    override val name: String?,
    override val metadata: Metadata?,
    override val opacity: Float?,
    override val aspectRatio: Float? = null,
    override val frame: Frame?,
    override val padding: Padding?,
    override val layoutPriority: Int?,
    override val offset: Point?,
    override val shadow: Shadow?,
    override val mask: Node?,
    override val accessibility: Accessibility?,
    override val overlay: Overlay?,
    override val background: Background?,
    override val action: Action?,
    override var children: List<Node> = emptyList(),
    override val childIDs: List<String> = emptyList(),
    val source: AssetSource,
    val autoPlay: Boolean,
    val looping: Boolean,
) : Node {
    override val typeName = NodeType.AUDIO.code
}
