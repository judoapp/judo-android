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
import org.junit.Test

class PageControlSizingTests {
    private val displayDensity = 1f
    private val pageControlDefaultHeight = 20f

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

    private fun createPageControlWithFrame(frame: Frame? = null, padding: Padding? = null, style: PageControlStyle = PageControlStyle.DefaultPageControlStyle()): PageControl {
        return PageControl(
            id = "",
            hidesForSinglePage = false,
            carouselID = "abc",
            frame = frame,
            padding = padding,
            style = style,
        )
    }

    private fun createImage(
        resizingMode: ResizingMode = ResizingMode.ORIGINAL,
        resolution: Float = 1f,
        padding: Padding? = null
    ): Image {
        return Image(
            id = "",
            imageURL = "",
            imageHeight = 100,
            imageWidth = 100,
            resizingMode = resizingMode,
            resolution = resolution,
            padding = padding,
        )
    }

    @Test
    fun `given inf vertical constraint pageControl sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = pageControlDefaultHeight

        val pageControl = createPageControlWithFrame(null)
        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = pageControl.sizeAndCoordinates.width
        val actualHeight = pageControl.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf vertical constraint with image style pageControl sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 100f

        val pageControl = createPageControlWithFrame(style = PageControlStyle.ImagePageControlStyle(
            ColorVariants(default = Color(1f, 1f, 1f, 1f)),
            ColorVariants(default = Color(1f, 1f, 1f, 1f)),
            createImage(),
            createImage()
        )
        )
        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = pageControl.sizeAndCoordinates.width
        val actualHeight = pageControl.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints pageControl frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val pageControl = createPageControlWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints pageControl frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = pageControlDefaultHeight

        val pageControl = createPageControlWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints pageControl frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = pageControlDefaultHeight

        val pageControl = createPageControlWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints pageControl frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 7f

        val pageControl = createPageControlWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints pageControl frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 7f
        val pageControl = createPageControlWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints pageControl frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val pageControl = createPageControlWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl sizing is correct`() {
        // Arrange
        val expectedHeight = pageControlDefaultHeight
        val expectedWidth = 300f
        val pageControl = createPageControlWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val pageControl = createPageControlWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val pageControl = createPageControlWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 300f
        val pageControl = createPageControlWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val pageControl = createPageControlWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val pageControl = createPageControlWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val pageControl = createPageControlWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val pageControl = createPageControlWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints pageControl with frame inf maxHeight and padding is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val pageControl = createPageControlWithFrame(
            frame = Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER),
            padding = Padding(top = 10f, bottom = 10f)
        )

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        pageControl.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = pageControl.sizeAndCoordinates.height
        val actualWidth = pageControl.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}