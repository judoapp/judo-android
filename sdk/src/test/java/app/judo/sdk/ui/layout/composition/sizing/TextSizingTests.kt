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
import android.text.StaticLayout
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

internal class TextSizingTests {

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

    private val singleLineHeight = 30

    private val mockStaticLayout = mock<StaticLayout> {
        on { getLineBaseline(0) } doReturn 10
        on { lineCount } doReturn 1
        on { getEllipsisCount(0) } doReturn 0
        on { getLineWidth(0) } doReturn 100f
        on { height } doReturn singleLineHeight
    }

    private val screenFactory = ScreenFactory()

    private val screenNode = TreeNode(screenFactory.makeScreenWithSize())

    private fun createShortTextWithFrame(
        frame: Frame? = null,
        alignment: TextAlignment = TextAlignment.LEADING
    ): Text {
        return Text(
            id = "",
            text = "hello world",
            frame = frame,
            font = Font.Fixed(size = 14f, weight = FontWeight.Regular, isDynamic = false),
            textAlignment = alignment,
            textColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
        )
    }

    private fun createLongTextWithFrame(frame: Frame? = null, alignment: TextAlignment = TextAlignment.LEADING): Text {
        return Text(
            id = "",
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
            frame = frame,
            font = Font.Fixed(size = 14f, weight = FontWeight.Regular, isDynamic = false),
            textAlignment = alignment,
            textColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
        )
    }

    @Test
    fun `given inf constraints text sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 30f

        val text = createShortTextWithFrame()
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualWidth = text.sizeAndCoordinates.width
        val actualHeight = text.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints text frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val text = createShortTextWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints text frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 30f

        val text = createShortTextWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints text frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 30f

        val text = createShortTextWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints text frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f

        val text = createShortTextWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints text frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val text = createShortTextWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf vertical constraint text sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 30f

        val text = createShortTextWithFrame(null)
        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualWidth = text.sizeAndCoordinates.width
        val actualHeight = text.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints text frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val text = createShortTextWithFrame(Frame(minWidth = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints text frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val text = createShortTextWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text sizing is correct`() {
        // Arrange
        val expectedHeight = 30f
        val expectedWidth = 100f
        val text = createShortTextWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val text = createShortTextWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val text = createShortTextWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val text = createShortTextWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val text = createShortTextWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val text = createShortTextWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val text = createShortTextWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints text with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val text = createShortTextWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        text.computeSize(mockContext, screenNode, parentConstraints, mockStaticLayout)
        val actualHeight = text.sizeAndCoordinates.height
        val actualWidth = text.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}