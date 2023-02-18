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

import android.graphics.Point
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import app.judo.sdk.compose.model.nodes.Rectangle
import app.judo.sdk.compose.model.values.*
import app.judo.sdk.compose.model.values.Alignment
import app.judo.sdk.compose.model.values.Background
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.model.values.Fill
import app.judo.sdk.compose.model.values.Overlay
import app.judo.sdk.compose.ui.layers.Layer
import app.judo.sdk.compose.ui.layers.TextLayer
import app.judo.sdk.compose.ui.layout.PackedHeight
import app.judo.sdk.compose.ui.utils.SimpleMeasurePolicy

@Composable
internal fun BackgroundModifier(
    background: Background?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (background != null) {

        Layout({
            content(Modifier)
            Layout({
                Layer(background.node)
            }, measurePolicy = SimpleMeasurePolicy(), modifier = Modifier.layoutId("backgroundOrOverlay"))
        }, measurePolicy = OverlayBackgroundMeasurePolicy(overlay = false, alignment = background.alignment), modifier = modifier)

        // Original code that uses a synthesized ZStack, a bit more accurate when Composables
        // yield empty. See notes on [OverlayBackgroundMeasurePolicy].
        // Layout({
        //            ZStackLayer(background.alignment) {
        //                content(Modifier)
        //                Layout({
        //                    Layer(background.node)
        //                }, measurePolicy = SimpleMeasurePolicy(), modifier = Modifier.judoParentModifierData(-1, background.node))
        //            }
        //        }, measurePolicy = SimpleMeasurePolicy(), modifier = modifier)
    } else {
        content(modifier)
    }
}

@Composable
internal fun OverlayModifier(
    overlay: Overlay?,
    modifier: Modifier,
    content: @Composable (modifier: Modifier) -> Unit
) {
    if (overlay != null) {
        Layout({
            content(Modifier)
            Layout({
                Layer(overlay.node)
            }, measurePolicy = SimpleMeasurePolicy(), modifier = Modifier.layoutId("backgroundOrOverlay"))
        }, measurePolicy = OverlayBackgroundMeasurePolicy(overlay = true, alignment = overlay.alignment), modifier = modifier)
    } else {
        content(modifier)
    }
}

/**
 * Implements Overlay and Background by simply measuring the content, and then measuring the
 * background/overlay with constraints set as the measured content size.
 *
 * This is done in lieu of using a synthesized ZStack in order to avoid bugs in the various
 * intrinsics and fallback behaviour that manifest with that approach.
 *
 * Limitations:
 * 1) Yields a 0x0 layout node in the event of no measurables (ie., empty content). This might mean
 *    extra spacing in a stack or other issues in the event of a failed interpolation or empty
 *    conditional.
 * 2) unlike SwiftUI's background, this implementation of background modifier cannot be used
 *    with composables that yield multiple layout nodes. (ie a Group/ForEach/Collection etc).
 *    Thankfully, while SwiftUI supports this, Judo has no cases (yet) where you can apply a
 *    background/overlay modifier to such a group.
 *
 * This approach is likely to be temporary, because this approach may be imperfect because
 * empty content (in case of content that disappears itself due to an interpolation error).
 * Once stack/intrinsics issues are solved we should return to
 */
internal class OverlayBackgroundMeasurePolicy(
    val overlay: Boolean = false,
    val alignment: Alignment
) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val contentMeasurable = measurables.firstOrNull() {
            it.layoutId != "backgroundOrOverlay"
        } ?: return layout(0, 0) {
            // alas this empty case is liable to be problematic.
            // I'm pretty sure that layout(0,0) still will end up yielding a LayoutNode,
            // which means (for example) that a parent stack will get a measurable even
            // if the content did not yield one. That would mean extra spacing.
        }

        val backgroundOrOverlayMeasurable = measurables.firstOrNull {
            it.layoutId == "backgroundOrOverlay"
        }

        val contentPlaceable = contentMeasurable.measure(constraints)

        val backgroundOrOverlayPlaceable = backgroundOrOverlayMeasurable?.measure(
            Constraints(
                maxWidth = contentPlaceable.width,
                maxHeight = contentPlaceable.height
            )
        )

        return layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0, zIndex = if (overlay) 0f else 1f)
            backgroundOrOverlayPlaceable?.let { placeable ->
                val width = contentPlaceable.width
                val height = contentPlaceable.height

                val centerWidth = { maxOf(width / 2 - placeable.width / 2, 0) }
                val centerHeight = { maxOf(height / 2 - placeable.height / 2, 0) }
                val right = { width - placeable.width }
                val bottom = { height - placeable.height }

                val position: Point = when (alignment) {
                    Alignment.TOP -> Point(centerWidth(), 0)
                    Alignment.BOTTOM -> Point(centerWidth(), bottom())
                    Alignment.LEADING -> Point(0, centerHeight())
                    Alignment.TRAILING -> Point(right(), centerHeight())
                    Alignment.TOP_LEADING -> Point(0, 0)
                    Alignment.TOP_TRAILING -> Point(right(), 0)
                    Alignment.BOTTOM_LEADING -> Point(0, bottom())
                    Alignment.BOTTOM_TRAILING -> Point(right(), bottom())
                    else -> Point(
                        centerWidth(),
                        centerHeight()
                    )
                }

                placeable.place(position.x, position.y, zIndex = if (overlay) 1f else 0f)
            }
        }
    }

    // these intrinsics have the job of selecting the child measurable (discriminating
    // it from the overlay or background by layoutId) and proxying the intrinsics through it.

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int {
        return measurables.firstOrNull {
            it.layoutId != "backgroundOrOverlay"
        }?.maxIntrinsicHeight(width) ?: PackedHeight.Zero.packedValue
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ): Int {
        return measurables.firstOrNull {
            it.layoutId != "backgroundOrOverlay"
        }?.maxIntrinsicWidth(height) ?: PackedHeight.Zero.packedValue
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ): Int {
        return measurables.firstOrNull {
            it.layoutId != "backgroundOrOverlay"
        }?.minIntrinsicHeight(width) ?: PackedHeight.Zero.packedValue
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ): Int {
        return measurables.firstOrNull {
            it.layoutId != "backgroundOrOverlay"
        }?.minIntrinsicWidth(height) ?: PackedHeight.Zero.packedValue
    }
}

@Preview
@Composable
private fun BackgroundTest() {
    val backgroundRect = Rectangle(
        id = "test",
        fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
        cornerRadius = 2f
    )
    BackgroundModifier(background = Background(backgroundRect, Alignment.CENTER), modifier = Modifier) { modifier ->
        Box(modifier = modifier) {
            TextLayer(text = "Judo Rocks")
        }
    }
}

@Preview
@Composable
private fun OverlayTest() {
    val overlayRect = Rectangle(
        id = "test",
        fill = Fill.FlatFill(ColorReference.SystemColor("blue")),
        cornerRadius = 2f
    )
    OverlayModifier(overlay = Overlay(overlayRect, Alignment.CENTER), modifier = Modifier) { modifier ->
        Box(modifier = modifier) {
            TextLayer(text = "Judo Rocks")
        }
    }
}

/**
 * Jetpack Compose forgot to include a `layoutId` accessor for IntrinsicMeasurable.
 */
private val IntrinsicMeasurable.layoutId: Any?
    get() = (parentData as? LayoutIdParentData)?.layoutId
