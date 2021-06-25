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

class VStackLayoutPrioSizingTests {
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

    private fun createRectangleWithFrame(frame: Frame? = null, layoutPriority: Float? = null): Rectangle {
        return Rectangle(
            id = "2",
            fill = Fill.FlatFill(ColorVariants(default = Color(0f, 0f, 0f, 0f))),
            cornerRadius = 0f,
            layoutPriority = layoutPriority,
            frame = frame,
        )
    }

    private fun createDividerWithFrame(frame: Frame? = null): Divider {
        return Divider(
            id = "2",
            backgroundColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
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

    @Test
    fun `given value constraints vstack-rect-layout-prio sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 300f
        val expectedRectangle2Height = 0f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val rectangle2 = createRectangleWithFrame()
        val rectangle3 = createRectangleWithFrame()

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-rect-layout-prio 2 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 150f
        val expectedRectangle2Height = 150f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val rectangle2 = createRectangleWithFrame(layoutPriority = 1f)
        val rectangle3 = createRectangleWithFrame()

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-rect-layout-prio 3 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 300f
        val expectedRectangle2Height = 0f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f)
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f)
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-rect-layout-prio 4 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 100f
        val expectedRectangle2Height = 200f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f)
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-rect-layout-prio 5 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 100f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-rect-layout-prio 6 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 100f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f)
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-rect-layout-prio 7 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 50f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 150f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-rect-layout-prio 8 sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedRectangle1Height = 50f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 50f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given inf constraints vstack-rect-layout-prio sizing is correct`() {
        // Arrange
        val expectedHeight = 0f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Height = 0f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val rectangle2 = createRectangleWithFrame()
        val rectangle3 = createRectangleWithFrame()

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given inf constraints vstack-rect-layout-prio 2 sizing is correct`() {
        // Arrange
        val expectedHeight = 0f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Height = 0f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val rectangle2 = createRectangleWithFrame(layoutPriority = 1f)
        val rectangle3 = createRectangleWithFrame()

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }


    @Test
    fun `given inf constraints vstack-rect-layout-prio 4 sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedRectangle1Height = 100f
        val expectedRectangle2Height = 0f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f)
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given inf constraints vstack-rect-layout-prio 5 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 100f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given inf constraints vstack-rect-layout-prio 6 sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f)
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given inf constraints vstack-rect-layout-prio 7 sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given inf constraints vstack-rect-layout-prio 8 sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedRectangle1Height = 0f
        val expectedRectangle2Height = 100f
        val expectedRectangle3Height = 0f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val rectangle2 = createRectangleWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val rectangle3 = createRectangleWithFrame(layoutPriority = 1f, frame = Frame(100f, maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(rectangle3))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualRectangle2Height = rectangle2.sizeAndCoordinates.height
        val actualRectangle3Height = rectangle3.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedRectangle2Height shouldEqual actualRectangle2Height
        expectedRectangle3Height shouldEqual actualRectangle3Height
    }

    @Test
    fun `given value constraints vstack-layout-prio sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 199f
        val expectedImage1Height = 100f
        val expectedDivider1Height = 1f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val image1 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val divider1 = createDividerWithFrame()

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given value constraints vstack-layout-prio 2 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 145f
        val expectedImage1Height = 145f
        val expectedDivider1Height = 10f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val image1 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.SCALE_TO_FIT)
        val divider1 = createDividerWithFrame(frame = Frame(height = 10f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given value constraints vstack-layout-prio 3 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 140f
        val expectedImage1Height = 150f
        val expectedDivider1Height = 10f

        val rectangle1 = createRectangleWithFrame()
        val image1 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.SCALE_TO_FIT)
        val divider1 = createDividerWithFrame(frame = Frame(height = 10f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
        }
        val parentConstraints = Dimensions(Dimension.Value(150f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given value constraints vstack-layout-prio 4 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 240f
        val expectedImage1Height = 50f
        val expectedDivider1Height = 10f

        val rectangle1 = createRectangleWithFrame()
        val image1 = createImageWithFrame(layoutPriority = 1f, frame = Frame(maxWidth = MaxWidth.Finite(50f), alignment = Alignment.CENTER), resizingMode = ResizingMode.SCALE_TO_FIT)
        val divider1 = createDividerWithFrame(frame = Frame(height = 10f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(divider1))
        }
        val parentConstraints = Dimensions(Dimension.Value(150f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualHeight = vStack.sizeAndCoordinates.height
        val actualRectangle1Height = rectangle1.sizeAndCoordinates.height
        val actualImage1Height = image1.sizeAndCoordinates.height
        val actualDivider1Height = divider1.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
        expectedRectangle1Height shouldEqual actualRectangle1Height
        expectedImage1Height shouldEqual actualImage1Height
        expectedDivider1Height shouldEqual actualDivider1Height
    }

    @Test
    fun `given value constraints vstack-layout-prio 5 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 200f
        val expectedImage1Height = 0f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f)
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FILL)
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

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
    fun `given value constraints vstack-layout-prio 6 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 50f
        val expectedImage1Height = 150f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 50f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FILL)
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL)

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

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
    fun `given value constraints vstack-layout-prio 7 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 100f
        val expectedImage1Height = 100f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FIT, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

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
    fun `given value constraints vstack-layout-prio 8 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 100f
        val expectedImage1Height = 100f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f)
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FIT, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

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
    fun `given value constraints vstack-layout-prio 9 sizing is correct`() {
        // Arrange
        val expectedHeight = 250f
        val expectedRectangle1Height = 50f
        val expectedImage1Height = 100f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FIT, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

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
    fun `given value constraints vstack-layout-prio 10 sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedRectangle1Height = 50f
        val expectedImage1Height = 100f
        val expectedImage2Height = 50f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FIT, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))

        val vStack = createVStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(image1))
            addChild(TreeNode(image2))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

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
    fun `given inf constraints vstack-layout-prio sizing is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedRectangle1Height = 300f
        val expectedImage1Height = 0f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)

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
    fun `given inf constraints vstack-layout-prio 2 sizing is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedRectangle1Height = 150f
        val expectedImage1Height = 150f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT, layoutPriority = 1f)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)

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
    fun `given inf constraints vstack-layout-prio 3 sizing is correct`() {
        // Arrange
        val expectedHeight = 500f
        val expectedRectangle1Height = 100f
        val expectedImage1Height = 300f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT, layoutPriority = 2f)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL, layoutPriority = 1f)

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
    fun `given inf constraints vstack-layout-prio 4 sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedRectangle1Height = 100f
        val expectedImage1Height = 100f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FIT, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

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
    fun `given inf constraints vstack-layout-prio 5 sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedRectangle1Height = 0f
        val expectedImage1Height = 100f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f)
        val image1 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.SCALE_TO_FIT, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

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
    fun `given inf constraints vstack-layout-prio 6 sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedRectangle1Height = 0f
        val expectedImage1Height = 100f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT, layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL, layoutPriority = 1f, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

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
    fun `given inf constraints vstack-layout-prio 7 sizing is correct`() {
        // Arrange
        val expectedHeight = 150f
        val expectedRectangle1Height = 0f
        val expectedImage1Height = 100f
        val expectedImage2Height = 50f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 3f, frame = Frame(maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))
        val image1 = createImageWithFrame(layoutPriority = 2f, frame = Frame(100f, 100f, alignment = Alignment.CENTER))
        val image2 = createImageWithFrame(layoutPriority = 1f, frame = Frame(100f, maxHeight = MaxHeight.Finite(50f), alignment = Alignment.CENTER))

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
    fun `given inf constraints vstack-layout-prio 8 sizing is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedRectangle1Height = 0f
        val expectedImage1Height = 300f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 1f)
        val image1 = createImageWithFrame(layoutPriority = 3f, resizingMode = ResizingMode.SCALE_TO_FIT)
        val image2 = createImageWithFrame(layoutPriority = 2f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

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
    fun `given inf constraints vstack-layout-prio 9 sizing is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedRectangle1Height = 300f
        val expectedImage1Height = 0f
        val expectedImage2Height = 100f

        val rectangle1 = createRectangleWithFrame(layoutPriority = 2f)
        val image1 = createImageWithFrame(layoutPriority = 1f, resizingMode = ResizingMode.SCALE_TO_FIT)
        val image2 = createImageWithFrame(layoutPriority = 3f, resizingMode = ResizingMode.ORIGINAL, frame = Frame(100f, minHeight = 100f, alignment = Alignment.CENTER))

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
}