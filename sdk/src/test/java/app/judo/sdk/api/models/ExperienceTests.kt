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

package app.judo.sdk.api.models

import app.judo.sdk.utils.*
import org.junit.Before
import org.junit.Test

class ExperienceTests {

    private lateinit var idFactory: IDFactory
    private lateinit var imageFactory: ImageFactory
    private lateinit var screenFactory: ScreenFactory
    private lateinit var factory: ExperienceFactory
    private lateinit var experience: Experience

    @Before
    fun setUp() {

        idFactory = IDFactory()

        imageFactory = ImageFactory(idFactory = idFactory)

        screenFactory = ScreenFactory(idFactory = idFactory)

        factory = ExperienceFactory(
            idFactory = idFactory,
            imageFactory = imageFactory
        )

        experience = factory.makeExperience()
    }

    @Test
    fun nodes() {

        val expected = listOf(
            screenFactory.makeScreen1(),
            screenFactory.makeScreen2()
        )

        val actual: List<Screen> = experience.nodes()

        expected shouldEqual actual
    }
}