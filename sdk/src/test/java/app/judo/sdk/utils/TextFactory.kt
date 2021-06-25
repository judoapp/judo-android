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

import app.judo.sdk.api.models.*


class TextFactory(
    private val idFactory: IDFactory = IDFactory()
) {

    fun makeText1(): Text {
        return  Text(
            id = idFactory.text1Id,
            text = "Hello World coming at you from in memory!",
            font = Font.Fixed(weight = FontWeight.Bold, size = 20F, isDynamic = false),
            textAlignment = TextAlignment.CENTER,
            textColor = ColorVariants(
                default = Color(
                    1f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                ),
            )
        )
    }

}
