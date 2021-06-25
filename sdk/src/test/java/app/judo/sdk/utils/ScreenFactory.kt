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

package app.judo.sdk.utils

import app.judo.sdk.api.models.Color
import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.api.models.Screen
import app.judo.sdk.api.models.StatusBarStyle
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates


class ScreenFactory(
    private val idFactory: IDFactory = IDFactory(),
) {

    fun makeScreen1(): Screen {
        return Screen(
            id = idFactory.screen1Id,
            name = "Screen 1",
            childIDs = listOf(idFactory.text1Id, idFactory.image1Id, idFactory.image3Id),
            backgroundColor = ColorVariants(
                default = Color(
                    0f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                )
            ),
            androidStatusBarStyle = StatusBarStyle.DEFAULT,
            androidStatusBarBackgroundColor = ColorVariants(
                default = Color(
                    0f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                )
            ),
        )
    }

    fun makeScreen2(): Screen {
        return Screen(
            id = idFactory.screen2Id,
            name = "Screen 2",
            childIDs = listOf(idFactory.text1Id, idFactory.image2Id),
            backgroundColor = ColorVariants(
                default = Color(
                    0f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                )
            ),
            androidStatusBarStyle = StatusBarStyle.DEFAULT,
            androidStatusBarBackgroundColor = ColorVariants(
                default = Color(
                    0f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                )
            ),
        )
    }

    fun makeScreenWithSize(): Screen {
        return Screen(
            id = idFactory.screen2Id,
            name = "Screen 2",
            childIDs = listOf(idFactory.text1Id, idFactory.image2Id),
            backgroundColor = ColorVariants(
                default = Color(
                    0f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                )
            ),
            androidStatusBarStyle = StatusBarStyle.DEFAULT,
            androidStatusBarBackgroundColor = ColorVariants(
                default = Color(
                    0f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                )
            ),
        ).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 1080f, height = 1780f)
        }
    }

}
