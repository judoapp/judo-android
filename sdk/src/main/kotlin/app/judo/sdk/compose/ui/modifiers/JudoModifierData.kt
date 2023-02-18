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

package app.judo.sdk.compose.ui.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Density
import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.ui.layers.LayerBox

/**
 * Information on Judo internal modifiers. I.e. Judo modifiers that don't use Compose native modifiers.
 * This Data is relevant for parents inside their measuring policies, and details relevant modifiers
 * for each child.
 *
 * It can be accessed via the [Measurable.parentData] property, and is set through each modifier's
 * [ParentDataModifier] implementation, such as in [OffsetModifierData].
 */
internal data class JudoModifierData(
    var layoutPriority: Int? = null,

    /**
     * Nodes that have a natural corner radius shape can promote a corner radius value.
     */
    var cornerRadiusForShadow: Float? = null,

    /**
     * A reference to the Judo node ultimately responsible for this measurable.  Use
     * this to debug layout problems.
     *
     * If you are trying to use this programmatically, it is likely a code smell.
     */
    var debugNode: Node? = null
)

/**
 * Helper method for getting the [JudoModifierData] object directly if it exists.
 */
internal val Measurable.judoChildModifierData: JudoModifierData?
    get() = parentData as? JudoModifierData

/**
 * Helper method for getting the [JudoModifierData] object directly if it exists.
 */
internal val IntrinsicMeasurable.judoChildModifierData: JudoModifierData?
    get() = parentData as? JudoModifierData

internal val Placeable.judoChildModifierData: JudoModifierData?
    get() = parentData as? JudoModifierData


/**
 * Sets several details relevant to Judo layout using a parent data modifier. This makes
 * that information available to be read back via measurables (intrinsic or otherwise).
 *
 * Must be placed on the outermost layout node that occurs in a composable for
 * any given Judo layer composable. That generally means [LayerBox].
 *
 * Used for layout priorities, metadata for shadows (so a shadow modifier can detect
 * the shape of the content within in order to confirm to it).
 *
 * Sets the layout priority on the give composable for a parent stack to use. Sets LayerData
 * that can be read back through the intrinsic measurable.
 *
 * Note; be sure to set this on the outermost layout node, as these do not propagate upwards.
 */
internal fun Modifier.judoParentModifierData(
    layoutPriority: Int,
    debugNode: Node?
) = this
    .then(
        JudoParentModifierData(layoutPriority = layoutPriority, debugNode = debugNode)
    )

internal class JudoParentModifierData(
    private val layoutPriority: Int,
    private val debugNode: Node?
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = ((parentData as? JudoModifierData) ?: JudoModifierData()).also {
        it.layoutPriority = layoutPriority
        it.debugNode = debugNode
    }
}
