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

package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.utils.ScreenFactory
import app.judo.sdk.utils.shouldEqual
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

internal class CarouselSizingTests {

    private val displayDensity = 1f

    private val mockDisplayMetrics = mock<DisplayMetrics>().apply {
        density = displayDensity
    }
    private val mockResources = mock<Resources> {
        on { displayMetrics } doReturn mockDisplayMetrics
    }
    private val mockContext = mock<Context> {
        on { resources } doReturn mockResources
    }
    private val screenFactory = ScreenFactory()

    private val screenNode = TreeNode(screenFactory.makeScreenWithSize())

    private fun createCarouselWithFrame(frame: Frame? = null): Carousel {
        return Carousel(
            childIDs = listOf(),
            id = "",
            isLoopEnabled = true,
            frame = frame,
        )
    }

    @Test
    fun `given inf constraints carousel sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val expectedHeight = 0f

        val carousel = createCarouselWithFrame()
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = carousel.sizeAndCoordinates.width
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val carousel = createCarouselWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val carousel = createCarouselWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val carousel = createCarouselWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f

        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints carousel frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val carousel = createCarouselWithFrame(Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints carousel frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val carousel = createCarouselWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val carousel = createCarouselWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val carousel = createCarouselWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minHeight and maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 20f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minHeight = 20f, maxHeight = MaxHeight.Finite(20f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minHeight and maxHeight sizing is correct 2`() {
        // Arrange
        val expectedHeight = 80f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minHeight = 20f, maxHeight = MaxHeight.Finite(80f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minHeight and infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minHeight = 20f, maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minWidth and maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 20f
        val carousel = createCarouselWithFrame(Frame(minWidth = 20f, maxWidth = MaxWidth.Finite(20f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minWidth and maxWidth sizing is correct 2`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 80f
        val carousel = createCarouselWithFrame(Frame(minWidth = 20f, maxWidth = MaxWidth.Finite(80f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minWidth and infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minWidth = 20f, maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}