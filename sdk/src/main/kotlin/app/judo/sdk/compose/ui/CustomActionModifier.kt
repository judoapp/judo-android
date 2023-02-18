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

package app.judo.sdk.compose.ui

import android.net.Uri
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalProvider
import app.judo.sdk.compose.model.nodes.Node

data class CustomActionActivation(
    /**
     * The URL from which we retrieved the experience, if available.
     */
    val experienceUrl: Uri?,

    /**
     * Details about the screen containing the node with the action that was activated.
     */
    val screen: NodeDetails,

    /**
     * Details about the node with the action that was activated.
     */
    val node: NodeDetails,

    /**
     * The URL query parameters (if any) that were included on the URL
     * used to launch the experience.
     */
    val urlParameters: Map<String, String>,

    val data: Any? = null,

    val experienceName: String? = null,
    val experienceID: String? = null
) {
    data class NodeDetails(
        val id: String,
        val name: String?,
        val metadata: Metadata?
    ) {
        internal constructor(node: Node) : this(
            id = node.id,
            name = node.name,
            metadata = node.metadata?.let { Metadata(it) }
        )

        data class Metadata(
            val tags: Set<String> = emptySet(),
            val properties: Map<String, String> = emptyMap()
        ) {
            internal constructor(metadata: app.judo.sdk.compose.model.values.Metadata) :
                this(tags = metadata.tags, properties = metadata.propertiesMap)
        }
    }
}

/**
 * The shape of the custom action handler callback.
 */
typealias CustomActionHandler = (CustomActionActivation) -> Unit

/**
 * Apply this modifier to [Experience] or [AsyncExperience] to
 * register a callback for custom actions.
 */
fun Modifier.judoCustomAction(callback: CustomActionHandler): Modifier {
    @OptIn(ExperimentalComposeUiApi::class)
    return this.modifierLocalProvider(
        Environment.ModifierLocalCustomActionHandler
    ) {
        callback
    }
}
