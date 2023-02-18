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

package app.judo.sdk.compose.ui.layers

import android.os.Trace
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import app.judo.sdk.compose.model.nodes.Image
import app.judo.sdk.compose.model.values.AssetSource
import app.judo.sdk.compose.model.values.Dimensions
import app.judo.sdk.compose.model.values.ResizingMode
import app.judo.sdk.compose.model.values.interpolatedSource
import app.judo.sdk.compose.ui.AssetContext
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.data.Interpolator
import app.judo.sdk.compose.ui.data.makeDataContext
import app.judo.sdk.compose.ui.layers.stacks.HStackLayer
import app.judo.sdk.compose.ui.layers.stacks.VStackLayer
import app.judo.sdk.compose.ui.layers.stacks.ZStackLayer
import app.judo.sdk.compose.ui.layout.*
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.utils.ExpandMeasurePolicy
import app.judo.sdk.compose.ui.utils.ifInfinity
import app.judo.sdk.compose.ui.utils.preview.InfiniteHeightMeasurePolicy
import app.judo.sdk.compose.ui.utils.unlessInfinity
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlin.math.roundToInt

@Composable
internal fun ImageLayer(node: Image) {
    ImageLayer(
        source = node.source,
        darkModeSource = node.darkModeSource,
        resizingMode = node.resizingMode,
        dimensions = node.dimensions,
        resolution = node.resolution,
        judoModifiers = JudoModifiers(node)
    )
}

@Composable
internal fun ImageLayer(
    source: AssetSource,
    darkModeSource: AssetSource? = null,
    resizingMode: ResizingMode,
    dimensions: Dimensions? = null,
    resolution: Float,
    modifier: Modifier = Modifier,
    judoModifiers: JudoModifiers = JudoModifiers()
) {
    val localDensityContext = LocalDensity.current
    val dataContext = makeDataContext(
        userInfo = Environment.LocalUserInfo.current?.invoke() ?: emptyMap(),
        urlParameters = Environment.LocalUrlParameters.current,
        data = Environment.LocalData.current
    )
    val interpolator = Interpolator(
        dataContext
    )

    val inputSource = if (Environment.LocalIsDarkTheme.current) darkModeSource ?: source else source
    val interpolatedSource = inputSource.interpolatedSource(interpolator)

    val dimensionsPx: Dimensions? = dimensions?.let {
        with(localDensityContext) {
            Dimensions(it.width.dp.roundToPx(), it.height.dp.roundToPx())
        }
    }

    interpolatedSource?.let { assetSource ->
        LayerBox(judoModifiers = judoModifiers) {
            when (resizingMode) {
                ResizingMode.SCALE_TO_FILL -> {
                    Layout({
                        Image(
                            assetSource,
                            ContentScale.Crop,
                            dimensionsPx
                        )
                    }, measurePolicy = ExpandMeasurePolicy(expandChildren = true))
                }
                ResizingMode.STRETCH -> {
                    Layout({
                        Image(
                            assetSource,
                            ContentScale.FillBounds,
                            dimensionsPx
                        )
                    }, measurePolicy = ExpandMeasurePolicy(expandChildren = true))
                }
                // TODO: TILE needs to be handled separately in the future.
                ResizingMode.SCALE_TO_FIT,
                ResizingMode.TILE -> {
                    Image(
                        assetSource,
                        ContentScale.Fit,
                        dimensionsPx,
                        // ContentScale.Fit is not enough; it is designed to take minimum size offered to
                        // it, uses all of that space, and the negative space not used by the image
                        // (the resulting letterbox) is just filled with padding, which is not what
                        // we want.
                        // A custom layout modifier, FitToSpace, is used in order constrain the image
                        // to its aspect ratio as determined with intrinsics.
                        modifier = modifier.then(FitToSpace())
                    )
                }
                ResizingMode.ORIGINAL -> {
                    // iOS uses @1x, @2x, and @3x to represent screen density when dealing with images.
                    // 1x is the full density, 2x is half density, and 3x is a third of the density.
                    // So, if we have 2.5 as the Android pixel density, and the image has a 2x resolution, we divide 2.5 by 2.
                    val desiredDensity = localDensityContext.density / resolution

                    Image(
                        assetSource,
                        FixedScale(1f * desiredDensity),
                        dimensionsPx,
                        modifier = modifier.then(StripPackedJudoIntrinsics())
                    )
                }
            }
        }
    }
}

@Composable
private fun Image(
    source: AssetSource,
    contentScale: ContentScale,
    dimensions: Dimensions? = null,
    modifier: Modifier = Modifier
) {
    val assetPath: String = when (source) {
        is AssetSource.FromFile -> {
            Environment.LocalAssetContext.current.uriForFileSource(
                LocalContext.current,
                AssetContext.AssetType.IMAGE,
                source
            ).toString()
        }
        is AssetSource.FromURL -> {
            source.url
        }
    }

    // if dimension data is available (non-interpolated images) use a placeholder image while the
    // real image is loading to cut down on layout changes.
    val placeholder = if (dimensions != null) {
        with(LocalDensity.current) {
            PlaceholderPainter(IntSize(dimensions.width, dimensions.height))
        }
    } else {
        null
    }

    val services = Environment.LocalServices.current ?: run {
        Log.e("ImageLayer.Image", "Services not injected")
        return
    }

    services.imageLoader.let {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(assetPath)
                .size(Size.ORIGINAL)
                .build(),
            imageLoader = it,
            contentScale = contentScale,
            placeholder = placeholder,
            onError = {
                Log.e("ImageLayer.Image", "Painter failed to fetch and/or display the image. ${it.result.throwable}")
            }
        )

        Image(
            painter = painter,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

/**
 * ContentScale.Fit's behavior does not match Judo/SwiftUI: it takes the minimum size offered to it,
 * in constraints, and fills that entire space up just like Fill does, and then letterboxes the
 * image. This means it does not maintain the aspect ratio of the image.
 *
 * This layout modifier wraps the view and ensures that the layout only uses the space
 * needed by the image by determining the aspect ratio using the Image composables measurable's
 * intrinsics.
 */
private class FitToSpace : LayoutModifier {
    private val tag = "FitToSpace"

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        Trace.beginSection("$tag::measure")
        // this yields the pixel size of an Image if that is the measurable. perfect. 👍
        val contentWidth = measurable.maxIntrinsicWidth(
            Constraints.Infinity
        )
        val contentHeight = measurable.maxIntrinsicHeight(
            Constraints.Infinity
        )

        // TODO: clean these up with a version of .unlessInfinity for Float { }
        val widthScale = if (constraints.maxWidth == Constraints.Infinity) {
            Float.POSITIVE_INFINITY
        } else {
            if (contentWidth != 0) constraints.maxWidth / contentWidth.toFloat() else 1f
        }

        val heightScale = if (constraints.maxHeight == Constraints.Infinity) {
            Float.POSITIVE_INFINITY
        } else {
            if (contentHeight != 0) constraints.maxHeight / contentHeight.toFloat() else 1f
        }

        // thus we want the minimum of these values for "fit" behaviour.
        val scale = minOf(widthScale, heightScale)

        val targetSize = if (scale == Float.POSITIVE_INFINITY) {
            // if both constraint dimensions are infinity, then the image should fall back
            // to the equivalent to 1x original sizing.
            // the contentHeight and contentWidth values are the original size of the image
            // in *pixels*. to get 1x scale, treat it as "dp" to multiply it by density.
            android.util.Size(
                contentWidth.dp.roundToPx(),
                contentHeight.dp.roundToPx()
            )
        } else {
            android.util.Size(
                (contentWidth * scale).roundToInt(),
                (contentHeight * scale).roundToInt()
            )
        }

        val placeable = measurable.measure(
            Constraints.fixed(targetSize.width, targetSize.height)
        )

        val l = layout(targetSize.width, targetSize.height) {
            placeable.place(0, 0)
        }
        Trace.endSection()
        return l
    }



    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        Trace.beginSection("$tag::intrinsicMeasure")
        return try {
            mapMaxIntrinsicWidthAsMeasure(height) { (width, height) ->
                // measurable is always the image.
                // intrinsics yield the pixel size of an Image. perfect. 👍
                val contentWidth = measurable.maxIntrinsicWidth(
                    Constraints.Infinity
                )
                val contentHeight = measurable.maxIntrinsicHeight(
                    Constraints.Infinity
                )

                // TODO: clean these up with a version of .unlessInfinity for Float { }
                val widthScale = if (width == Constraints.Infinity) {
                    Float.POSITIVE_INFINITY
                } else {
                    if (contentWidth != 0) width / contentWidth.toFloat() else 1f
                }

                val heightScale = if (height == Constraints.Infinity) {
                    Float.POSITIVE_INFINITY
                } else {
                    if (contentHeight != 0) height / contentHeight.toFloat() else 1f
                }

                // thus we want the minimum of these values for "fit" behaviour.
                val scale = minOf(widthScale, heightScale)

                if (scale == Float.POSITIVE_INFINITY) {
                    // if both constraint dimensions are infinity, then the image should fall back
                    // to the equivalent to 1x original sizing.
                    // the contentHeight and contentWidth values are the original size of the image
                    // in *pixels*. to get 1x scale, treat it as "dp" to multiply it by density.
                    android.util.Size(
                        contentWidth.dp.roundToPx(),
                        contentHeight.dp.roundToPx()
                    )
                } else {
                    android.util.Size(
                        (contentWidth * scale).roundToInt(),
                        (contentHeight * scale).roundToInt()
                    )
                }
            }
        } finally {
            Trace.endSection()
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // fit images are flexible.
            IntRange(0, Constraints.Infinity)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return mapMinIntrinsicAsFlex {
            // fit images are flexible.
            IntRange(0, Constraints.Infinity)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        throw IllegalStateException("Only call maxIntrinsicWidth, with packed parameter, on Judo measurables.")
    }
}


interface JudoMeasurable {
    fun measure(width: Int, height: Int): Int
}

// Note: these previews can only work in a live on-device preview, since they contain asynchronous behaviour.

@Preview
@Composable
private fun ImageOriginalSize() {
    ImageLayer(
        source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
        resizingMode = ResizingMode.ORIGINAL,
        resolution = 1.0f,
        modifier = Modifier.border(2.dp, Color.Red)
    )
}

@Preview
@Composable
private fun ImageFill() {
    ImageLayer(
        source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
        resizingMode = ResizingMode.SCALE_TO_FILL,
        resolution = 1.0f, // unused
        modifier = Modifier.border(2.dp, Color.Red)
    )
}

@Preview
@Composable
private fun ImageFit() {
    ImageLayer(
        source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
        resizingMode = ResizingMode.SCALE_TO_FIT,
        resolution = 1.0f, // unused
        modifier = Modifier.border(2.dp, Color.Red)
    )
}

@Preview
@Composable
private fun ImageFitInZStack() {
    ZStackLayer {
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
    }
}

@Preview
@Composable
private fun IntegrationImageFitInZStack() {
    ScrollContainerLayer {
        ZStackLayer {
            ImageLayer(
                source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
                resizingMode = ResizingMode.SCALE_TO_FIT,
                resolution = 1.0f, // unused
                modifier = Modifier.border(2.dp, Color.Red)
            )
        }
    }
}

@Preview
@Composable
private fun ImageFitInfinityHeight() {
    // will constrain only on width.
    Layout({
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
    }, measurePolicy = InfiniteHeightMeasurePolicy)
}

@Preview
@Composable
private fun ImageFillInfinityHeight() {
    Layout({
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FILL,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
    }, measurePolicy = InfiniteHeightMeasurePolicy)
}

@Preview
@Composable
private fun ImageStretch() {
    ImageLayer(
        source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
        resizingMode = ResizingMode.STRETCH,
        resolution = 1.0f, // unused
        modifier = Modifier.border(2.dp, Color.Red)
    )
}

@Preview
@Composable
private fun ImageStretchInfinityHeight() {
    Layout({
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.STRETCH,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
    }, measurePolicy = InfiniteHeightMeasurePolicy)
}

@Preview
@Composable
private fun ImageFitInStack() {
    VStackLayer {
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
    }
}

@Preview
@Composable
private fun ImageFitInHStack() {
    HStackLayer {
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
        ImageLayer(
            source = AssetSource.FromURL("https://upload.wikimedia.org/wikipedia/en/2/27/Bliss_%28Windows_XP%29.png"),
            resizingMode = ResizingMode.SCALE_TO_FIT,
            resolution = 1.0f, // unused
            modifier = Modifier.border(2.dp, Color.Red)
        )
    }
}

/**
 * This painter will take up the same amount of space as the image
 * will do, but does not draw anything. This cuts down on unsightly
 * layout changes while images are loading.
 *
 * It is used in lieu of the real image while it is being loaded.
 */
private class PlaceholderPainter(
    private val size: IntSize
): Painter() {
    override val intrinsicSize: androidx.compose.ui.geometry.Size
        get() = size.toSize()

    override fun DrawScope.onDraw() {
        // draw nothing at all.
    }
}