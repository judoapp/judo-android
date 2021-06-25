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

package app.judo.sdk.ui.layout.composition.sizing.vstack

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.sizing.computeSize
import app.judo.sdk.utils.shouldEqual
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class VStackInfConstrainedSizingTests {
    private val displayDensity = 1f

    private val mockDisplayMetrics = mock<DisplayMetrics>().apply {
        density = displayDensity
    }

    private val mockConfiguration = mock<Configuration>().apply {
        uiMode = 0
    }
    private val mockResources = mock<Resources> {
        on { displayMetrics } doReturn mockDisplayMetrics
        on { configuration } doReturn mockConfiguration
    }
    private val mockContext = mock<Context> {
        on { resources } doReturn mockResources
    }

    private fun createVStackWithFrame(frame: Frame? = null, spacing: Float = 0f, padding: Padding? = null): VStack {
        return VStack(
            id = "1",
            frame = frame,
            padding = padding,
            alignment = HorizontalAlignment.CENTER,
            spacing = spacing
        )
    }

    private fun createRectangleWithFrame(frame: Frame? = null, aspectRatio: Float? = null): Rectangle {
        return Rectangle(
            id = "2",
            fill = Fill.FlatFill(ColorVariants(default = Color(0f, 0f, 0f, 0f))),
            cornerRadius = 0f,
            frame = frame,
            aspectRatio = aspectRatio,
        )
    }

    private fun createDividerWithFrame(frame: Frame? = null): Divider {
        return Divider(
            id = "3",
            backgroundColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
            frame = frame,
        )
    }

    private fun createSpacerWithFrame(frame: Frame? = null): Spacer {
        return Spacer(
            id = "4",
            frame = frame,
        )
    }

    private fun createImageWithFrame(
        resizingMode: ResizingMode = ResizingMode.ORIGINAL,
        resolution: Float = 1f,
        layoutPriority: Float? = null,
        frame: Frame? = null,
        padding: Padding? = null
    ): Image {
        return Image(
            id = "3",
            imageURL = "",
            imageHeight = 100,
            imageWidth = 100,
            resizingMode = resizingMode,
            resolution = resolution,
            frame = frame,
            padding = padding,
            layoutPriority = layoutPriority,
        )
    }

    // inf constraints single rectangle

    @Test
    fun `given inf constraints vstack-no-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 0f

        val rectangle = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-no-frame-with-large-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 400f
        val expectedHeight = 400f

        val rectangle = createRectangleWithFrame(frame = Frame(400f, 400f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }


    @Test
    fun `given inf constraints vstack-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame()
        val vStack = createVStackWithFrame(frame = Frame(200f, 200f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-min-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame()
        val vStack = createVStackWithFrame(frame = Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-max-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 0f

        val rectangle = createRectangleWithFrame()
        val vStack = createVStackWithFrame(frame = Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-max-frame-inf-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 0f

        val rectangle = createRectangleWithFrame()
        val vStack = createVStackWithFrame(frame = Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-no-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-no-frame-with-fixed-rect-padding sizing is correct`() {
        // Arrange
        val expectedWidth = 220f
        val expectedHeight = 220f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame(padding = Padding(10f, 10f,10f, 10f))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame(Frame(300f, 300f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-max-inf-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-max-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-min-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vstack-rect-child fixed and expand sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 200f

        val expectedRectangle1Width = 200f
        val expectedRectangle1Height = 200f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 0f

        val rectangle1 = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child fixed and expand 2 sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 400f

        val expectedRectangle1Width = 200f
        val expectedRectangle1Height = 200f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 200f

        val rectangle1 = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame(Frame(height = 400f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child expand and expand sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 0f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 0f

        val rectangle1 = createRectangleWithFrame()
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child fixed height sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 150f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 150f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 0f

        val rectangle1 = createRectangleWithFrame(Frame(height = 150f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child with spacing sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 160f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 150f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 0f

        val rectangle1 = createRectangleWithFrame(Frame(height = 150f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame(spacing = 10f)
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child with spacing sizing 2 is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 145f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 145f

        val rectangle1 = createRectangleWithFrame(Frame(height = 145f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(Frame(height = 145f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame(spacing = 10f)
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child sizing minheight is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 400f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 200f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 200f

        val rectangle1 = createRectangleWithFrame(Frame(minHeight = 200f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(Frame(minHeight = 200f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child sizing maxheight is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 0f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 0f

        val rectangle1 = createRectangleWithFrame(Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child sizing maxheight infinite is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 0f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 0f

        val rectangle1 = createRectangleWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-rect-child sizing maxheight infinite 2 is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 50f

        val expectedRectangle1Width = 300f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Width = 300f
        val expectedRectangle2Height = 50f

        val rectangle1 = createRectangleWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(Frame(height = 50f, alignment = Alignment.CENTER))
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = vStack.sizeAndCoordinates.width
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Width = rectangle1.sizeAndCoordinates.width
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height

        val actualRectangle2Width = rectangle2.sizeAndCoordinates.width
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangle1Width shouldEqual actualRectangle1Width
        expectedRectangle1Height shouldEqual actualRectangle1Height

        expectedRectangle2Width shouldEqual actualRectangle2Width
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given inf constraints vstack-various-child height sizing is correct`() {
        // Arrange
        val expectedHeight = 200f

        val expectedRectangle1Height = 50f
        val expectedImage1Height = 100f
        val expectedImage2Height = 50f

        val rectangle1 = createRectangleWithFrame()
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.STRETCH)
        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualImage2Height = image2.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedImage2Height shouldEqual actualImage2Height
    }

    @Test
    fun `given inf constraints vstack-image-child height sizing is correct`() {
        // Arrange
        val expectedHeight = 200f

        val expectedImage1Height = 50f
        val expectedImage2Height = 100f
        val expectedImage3Height = 50f

        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL, resolution = 2f)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.STRETCH)
        val image3 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
            addChild(TreeNode(image3))
        }
        val parentConstraints = Dimensions(Dimension.Value(50f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualImage3Height = image3.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualImage2Height = image2.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage3Height shouldEqual actualImage3Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedImage2Height shouldEqual actualImage2Height
    }

    @Test
    fun `given inf constraints vstack-image-child scale sizing is correct`() {
        // Arrange
        val expectedHeight = 151f

        val expectedImage1Height = 50.500004f
        val expectedImage2Height = 50.500004f
        val expectedImage3Height = 50f

        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FILL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.STRETCH)
        val image3 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
            addChild(TreeNode(image3))
        }
        val parentConstraints = Dimensions(Dimension.Value(50f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualImage3Height = image3.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualImage2Height = image2.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage3Height shouldEqual actualImage3Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedImage2Height shouldEqual actualImage2Height
    }

    @Test
    fun `given inf constraints vstack-image-child 2 scale sizing is correct`() {
        // Arrange
        val expectedHeight = 401f

        val expectedImage1Height = 133.66667f
        val expectedImage2Height = 133.66667f
        val expectedImage3Height = 133.66667f

        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FILL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.STRETCH)
        val image3 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
            addChild(TreeNode(image3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualImage3Height = image3.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualImage2Height = image2.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage3Height shouldEqual actualImage3Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedImage2Height shouldEqual actualImage2Height
    }

    @Test
    fun `given inf constraints vstack-image-child 3 scale sizing is correct`() {
        // Arrange
        val expectedHeight = 77f

        val expectedImage1Height = 25.666666f
        val expectedImage2Height = 25.666666f
        val expectedImage3Height = 25.666666f

        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FILL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.TILE)
        val image3 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
            addChild(TreeNode(image3))
        }
        val parentConstraints = Dimensions(Dimension.Value(75f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualImage3Height = image3.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualImage2Height = image2.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage3Height shouldEqual actualImage3Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedImage2Height shouldEqual actualImage2Height
    }

    @Test
    fun `given inf constraints vstack-various-child scale sizing is correct`() {
        // Arrange
        val expectedHeight = 201f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 100f
        val expectedDivider1Height = 1f

        val divider1 = createDividerWithFrame()
        val rectangle1 = createRectangleWithFrame(Frame(100f, 100f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given inf constraints vstack-various-child scale sizing 2 is correct`() {
        // Arrange
        val expectedHeight = 101f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedDivider1Height = 1f

        val divider1 = createDividerWithFrame()
        val rectangle1 = createRectangleWithFrame(Frame(100f, maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given inf constraints vstack-various-child scale sizing 3 is correct`() {
        // Arrange
        val expectedHeight = 200f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedDivider1Height = 100f

        val divider1 = createDividerWithFrame(Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))
        val rectangle1 = createRectangleWithFrame(Frame(100f, maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given inf constraints vstack-various-child scale sizing 4 is correct`() {
        // Arrange
        val expectedHeight = 450f

        val expectedImage1Height = 175f
        val expectedRectangle1Height = 175f
        val expectedDivider1Height = 100f

        val divider1 = createDividerWithFrame(Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))
        val rectangle1 = createRectangleWithFrame(Frame(100f, minHeight = 50f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given inf constraints vstack-various-child scale sizing 5 is correct`() {
        // Arrange
        val expectedHeight = 110f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedDivider1Height = 10f

        val divider1 = createDividerWithFrame(Frame(100f, height = 10f, alignment = Alignment.CENTER))
        val rectangle1 = createRectangleWithFrame(Frame(100f, maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given inf constraints vstack-various-child scale sizing 6 is correct`() {
        // Arrange
        val expectedHeight = 200f

        val expectedImage1Height = 50f
        val expectedRectangle1Height = 50f
        val expectedDivider1Height = 100f

        val divider1 = createDividerWithFrame(Frame(100f, height = 100f, alignment = Alignment.CENTER))
        val rectangle1 = createRectangleWithFrame()
        val image1 = createImageWithFrame(resizingMode = ResizingMode.STRETCH, frame = Frame(100f, maxHeight = MaxHeight.Finite(150f), alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given inf constraints vstack-various-child with spacer sizing is correct`() {
        // Arrange
        val expectedHeight = 200f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedSpacer1Height = 100f

        val rectangle1 = createRectangleWithFrame()
        val spacer1 = createSpacerWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.STRETCH, frame = Frame(100f, height = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(spacer1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualSpacer1Height = spacer1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedSpacer1Height shouldEqual actualSpacer1Height
    }

    @Test
    fun `given inf constraints vstack-various-child with spacer sizing 2 is correct`() {
        // Arrange
        val expectedHeight = 110f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedSpacer1Height = 10f

        val rectangle1 = createRectangleWithFrame()
        val spacer1 = createSpacerWithFrame()
        val image1 = createImageWithFrame(resizingMode = ResizingMode.STRETCH, frame = Frame(100f, height = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(spacer1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualSpacer1Height = spacer1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedSpacer1Height shouldEqual actualSpacer1Height
    }

    @Test
    fun `given inf constraints vstack-various-child with spacer sizing 3 is correct`() {
        // Arrange
        val expectedHeight = 210f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 100f
        val expectedSpacer1Height = 10f

        val rectangle1 = createRectangleWithFrame(frame = Frame(100f, height = 100f, alignment = Alignment.CENTER))
        val spacer1 = createSpacerWithFrame()
        val image1 = createImageWithFrame(resizingMode = ResizingMode.STRETCH, frame = Frame(100f, height = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(spacer1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualSpacer1Height = spacer1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedSpacer1Height shouldEqual actualSpacer1Height
    }

    @Test
    fun `given inf constraints vstack-various-child with spacer sizing 4 is correct`() {
        // Arrange
        val expectedHeight = 200f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedSpacer1Height = 100f

        val rectangle1 = createRectangleWithFrame()
        val spacer1 = createSpacerWithFrame(frame = Frame(minHeight = 100f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.STRETCH, frame = Frame(100f, height = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(spacer1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualSpacer1Height = spacer1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedSpacer1Height shouldEqual actualSpacer1Height
    }

    @Test
    fun `given inf constraints vstack-various-child with spacer sizing 5 is correct`() {
        // Arrange
        val expectedHeight = 110f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedSpacer1Height = 10f

        val rectangle1 = createRectangleWithFrame()
        val spacer1 = createSpacerWithFrame(frame = Frame(maxHeight = MaxHeight.Finite(100f), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.STRETCH, frame = Frame(100f, height = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(spacer1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualSpacer1Height = spacer1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedSpacer1Height shouldEqual actualSpacer1Height
    }

    @Test
    fun `given inf constraints vstack-various-child with spacer sizing 6 is correct`() {
        // Arrange
        val expectedHeight = 110f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 0f
        val expectedSpacer1Height = 10f

        val rectangle1 = createRectangleWithFrame()
        val spacer1 = createSpacerWithFrame(frame = Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.STRETCH, frame = Frame(100f, height = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(spacer1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualSpacer1Height = spacer1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedSpacer1Height shouldEqual actualSpacer1Height
    }

    @Test
    fun `given inf constraints vstack-various-child scale sizing 7 is correct`() {
        // Arrange
        val expectedHeight = 250f

        val expectedImage1Height = 125f
        val expectedRectangle1Height = 125f

        val rectangle1 = createRectangleWithFrame(aspectRatio = 2f)
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, maxHeight = MaxHeight.Finite(150f), alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
    }

    @Test
    fun `given value constraints vstack-various-child scale sizing 8 is correct`() {
        // Arrange
        val expectedHeight = 150f

        val expectedImage1Height = 100f
        val expectedRectangle1Height = 25f
        val expectedRectangle2Height = 25f

        val rectangle2 = createRectangleWithFrame()
        val rectangle1 = createRectangleWithFrame(aspectRatio = 2f)
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(100f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }

    @Test
    fun `given value constraints vstack-various-child scale sizing 9 is correct`() {
        // Arrange
        val expectedHeight = 150f

        val expectedImage1Height = 50f
        val expectedRectangle1Height = 50f
        val expectedRectangle2Height = 50f

        val rectangle2 = createRectangleWithFrame()
        val rectangle1 = createRectangleWithFrame(aspectRatio = 2f)
        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle1))
        }
        val parentConstraints = Dimensions(Dimension.Value(100f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height

        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedImage1Height shouldEqual actualImage1Height
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
    }
}