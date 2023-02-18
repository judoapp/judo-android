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

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import app.judo.sdk.compose.model.values.AssetSource
import app.judo.sdk.compose.model.values.ResizingMode
import app.judo.sdk.compose.model.values.interpolatedSource
import app.judo.sdk.compose.ui.AssetContext
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.data.Interpolator
import app.judo.sdk.compose.ui.data.makeDataContext
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.utils.ExpandMeasurePolicy
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView

@Composable
internal fun MediaPlayer(
    source: AssetSource,
    looping: Boolean,
    autoPlay: Boolean,
    showControls: Boolean,
    resizingMode: ResizingMode? = null,
    removeAudio: Boolean = false,
    posterImageURL: String? = null,
    timeoutControls: Boolean = true,
    modifier: Modifier = Modifier,
    judoModifiers: JudoModifiers = JudoModifiers(),
    measurePolicy: MeasurePolicy
) {
    val context = LocalContext.current

    val dataContext = makeDataContext(
        userInfo = Environment.LocalUserInfo.current?.invoke() ?: emptyMap(),
        urlParameters = Environment.LocalUrlParameters.current,
        data = Environment.LocalData.current
    )
    val interpolator = Interpolator(
        dataContext
    )

    val interpolatedSource = source.interpolatedSource(interpolator)

    interpolatedSource?.let { assetSource ->
        val assetPath: String = when (assetSource) {
            is AssetSource.FromFile -> {
                Environment.LocalAssetContext.current.uriForFileSource(LocalContext.current, AssetContext.AssetType.MEDIA, assetSource).toString()
            }
            is AssetSource.FromURL -> {
                assetSource.url
            }
        }

        val exoPlayer = remember {
            SimpleExoPlayer.Builder(context).build().apply {
                repeatMode = when (looping) {
                    true -> Player.REPEAT_MODE_ALL
                    false -> Player.REPEAT_MODE_OFF
                }

                if (removeAudio) {
                    volume = 0f
                }

                playWhenReady = autoPlay
                addMediaItem(MediaItem.fromUri(assetPath))
                prepare()
            }
        }

        LayerBox(judoModifiers) {
            Layout(
                {
                    DisposableEffect(
                        AndroidView(factory = {
                            PlayerView(context).apply {
                                player = exoPlayer

                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                resizeMode = if (resizingMode == ResizingMode.SCALE_TO_FIT) {
                                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                                } else {
                                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                }

                                if (!timeoutControls) {
                                    controllerShowTimeoutMs = -1
                                }

                                useController = showControls

                                useArtwork = false
                            }
                        })
                    ) {
                        onDispose {
                            exoPlayer.release()
                        }
                    }
                },
                measurePolicy = measurePolicy,
                modifier = modifier
            )
        }
    }
}
