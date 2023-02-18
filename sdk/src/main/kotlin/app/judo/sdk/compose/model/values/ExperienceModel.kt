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

package app.judo.sdk.compose.model.values

import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.nodes.Screen
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExperienceModel internal constructor(
    internal val nodes: List<Node>,
    internal val screenIDs: List<String>,
    internal val initialScreenID: String,
    internal val appearance: Appearance,
    internal val localizations: Map<String, Map<String, String>> = emptyMap(),
    internal val colors: List<DocumentColor>,
    internal val gradients: List<DocumentGradient>,
    internal val fonts: List<DocumentFont>,
    @Json(name = "userInfo")
    internal var userInfoList: List<String> = emptyList(),
    @Json(name = "urlParameters")
    internal var urlParametersList: List<String> = emptyList(),
    internal val authorizers: List<Authorizer> = emptyList(),
    internal val segues: List<Segue>
) {
    @Transient
    internal var userInfo: Map<String, Any> = emptyMap()

    @Transient
    internal var urlParameters: Map<String, String> = emptyMap()

    /**
     * Populates the [Node.children] children array.
     * This should be called right after an Experience is deserialized.
     */
    fun buildTreeAndRelationships() {
        val nodesById = nodes.associateBy { it.id }
        val documentColorsById = this.colors.associateBy { it.id }
        val documentGradientsById = this.gradients.associateBy { it.id }
        val screensById = nodes.filterIsInstance<Screen>().associateBy { it.id }

        segues.forEach { segue ->
            (nodesById[segue.sourceID]?.action as? Action.PerformSegue)?.let {
                it.screenID = segue.destinationID
                it.segueStyle = segue.style
            }
        }

        userInfo = userInfoList.zipWithNext().toMap()
        urlParameters = urlParametersList.zipWithNext().toMap()

        nodes.forEach { node ->
            node.setRelationships(
                nodesById, documentColorsById, documentGradientsById, screensById
            )
        }
    }

    companion object { }
}
