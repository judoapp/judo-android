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

import app.judo.sdk.api.models.Appearance
import app.judo.sdk.api.models.Experience


class ExperienceFactory(
    private val idFactory: IDFactory = IDFactory(),
    var textFactory: TextFactory = TextFactory(idFactory),
    var imageFactory: ImageFactory = ImageFactory(idFactory),
    var screenFactory: ScreenFactory = ScreenFactory(idFactory),
) {

    fun makeExperience(): Experience {

        return Experience(
            id = "1",
            name = "Test",
            version = 1,
            revisionID = "1",
            nodes = listOf(
                screenFactory.makeScreen1(),
                screenFactory.makeScreen2(),
                textFactory.makeText1(),
                imageFactory.makeImage1(),
                imageFactory.makeImage2(),
                imageFactory.makeImage3()
            ),
            initialScreenID = idFactory.screen1Id,
            screenIDs = listOf(idFactory.screen1Id),
            appearance = Appearance.AUTO
        )
    }

}
