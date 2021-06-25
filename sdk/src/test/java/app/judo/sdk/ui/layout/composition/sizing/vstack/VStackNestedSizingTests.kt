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

class VStackNestedSizingTests {
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

    private fun createHStackWithFrame(frame: Frame? = null, spacing: Float = 0f, padding: Padding? = null): HStack {
        return HStack(
            id = "2",
            frame = frame,
            padding = padding,
            alignment = VerticalAlignment.CENTER,
            spacing = spacing
        )
    }

    private fun createZStackWithFrame(frame: Frame? = null, padding: Padding? = null): ZStack {
        return ZStack(
            id = "2",
            frame = frame,
            padding = padding,
            alignment = Alignment.CENTER
        )
    }

    private fun createRectangleWithFrame(frame: Frame? = null, aspectRatio: Float? = null): Rectangle {
        return Rectangle(
            id = "3",
            fill = Fill.FlatFill(ColorVariants(default = Color(0f, 0f, 0f, 0f))),
            cornerRadius = 0f,
            frame = frame,
            aspectRatio = aspectRatio,
        )
    }

    private fun createDividerWithFrame(frame: Frame? = null): Divider {
        return Divider(
            id = "4",
            backgroundColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
            frame = frame,
        )
    }

    private fun createSpacerWithFrame(frame: Frame? = null): Spacer {
        return Spacer(
            id = "5",
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
            id = "6",
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
    fun `given value constraints nested vstack sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 100f

        val rectangle1 = createRectangleWithFrame()
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val rectangle3 = createRectangleWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle3))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 2 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 150f

        val rectangle1 = createRectangleWithFrame()
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val rectangle3 = createRectangleWithFrame()
        val hStack = createHStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(rectangle1))
                addChild(TreeNode(rectangle2))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle3))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 3 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 150f

        val rectangle1 = createRectangleWithFrame()
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val rectangle3 = createRectangleWithFrame()
        val zStack = createZStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(zStack).apply {
                addChild(TreeNode(rectangle1))
                addChild(TreeNode(rectangle2))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle3))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 4 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 150f

        val rectangle1 = createRectangleWithFrame()
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val rectangle3 = createRectangleWithFrame()
        val innerVStack2 = createZStackWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(innerVStack2).apply {
                addChild(TreeNode(rectangle1))
                addChild(TreeNode(rectangle2))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle3))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 5 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 200f

        val rectangle1 = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image))

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle1))
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 6 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 200f

        val rectangle1 = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val zStack = createZStackWithFrame()
        val innerVStack = createVStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            zStack.apply {
                addChild(TreeNode(image))
            }

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle1))
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 7 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 200f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 200f
        val expectedInnerVStackHeight = 200f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 8 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 100f
        val expectedOuterVStackHeight = 200f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 9 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 250f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 150f

        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(maxHeight = MaxHeight.Finite(150f), alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
                addChild(TreeNode(rectangle))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 10 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 100f
        val expectedOuterVStackHeight = 250f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 150f

        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(minHeight = 150f, alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 11 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(height = 100f, alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
                addChild(TreeNode(rectangle))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 12 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 100f
        val expectedOuterVStackHeight = 300f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(height = 100f, alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(createSpacerWithFrame()))

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 13 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 201f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(createDividerWithFrame()))

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 14 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 200f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
                addChild(TreeNode(createSpacerWithFrame()))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given value constraints nested vstack 15 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 201f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 101f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
                addChild(TreeNode(createDividerWithFrame()))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 0f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 0f

        val rectangle1 = createRectangleWithFrame()
        val rectangle2 = createRectangleWithFrame()
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val rectangle3 = createRectangleWithFrame()
        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(rectangle1))
            addChild(TreeNode(rectangle2))
            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle3))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 5 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 400f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 300f

        val rectangle1 = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(image))

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle1))
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 6 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 400f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 300f

        val rectangle1 = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val zStack = createZStackWithFrame()
        val innerVStack = createVStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            zStack.apply {
                addChild(TreeNode(image))
            }

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(rectangle1))
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 7 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 400f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 300f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 8 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 100f
        val expectedOuterVStackHeight = 200f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 9 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 200f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 100f

        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(maxHeight = MaxHeight.Finite(150f), alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
                addChild(TreeNode(rectangle))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 10 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 100f
        val expectedOuterVStackHeight = 250f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 150f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(minHeight = 150f, alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 11 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 200f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(height = 100f, alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
                addChild(TreeNode(rectangle))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 12 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 100f
        val expectedOuterVStackHeight = 210f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame(frame = Frame(height = 100f, alignment = Alignment.CENTER))
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(createSpacerWithFrame()))

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 13 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 201f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(createDividerWithFrame()))

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 14 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 200f

        val expectedInnerVStackWidth = 100f
        val expectedInnerVStackHeight = 100f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
                addChild(TreeNode(createSpacerWithFrame()))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }

    @Test
    fun `given inf constraints nested vstack 15 sizing is correct`() {
        // Arrange
        val expectedOuterVStackWidth = 300f
        val expectedOuterVStackHeight = 201f

        val expectedInnerVStackWidth = 300f
        val expectedInnerVStackHeight = 101f

        val image = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val image2 = createImageWithFrame(resizingMode = ResizingMode.ORIGINAL)
        val vStack = createVStackWithFrame()
        val innerVStack = createVStackWithFrame()
        val hStack = createHStackWithFrame()

        val treeNode = TreeNode(vStack).apply {
            addChild(TreeNode(hStack).apply {
                addChild(TreeNode(image))
            })

            addChild(TreeNode(innerVStack).apply {
                addChild(TreeNode(image2))
                addChild(TreeNode(createDividerWithFrame()))
            })
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Inf)

        // Act
        vStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualOuterVStackWidth = vStack.sizeAndCoordinates.width
        val actualOuterVStackHeight = vStack.sizeAndCoordinates.height

        val actualInnerVStackWidth = innerVStack.sizeAndCoordinates.width
        val actualInnerVStackHeight = innerVStack.sizeAndCoordinates.height

        // Assert
        expectedOuterVStackWidth shouldEqual actualOuterVStackWidth
        expectedOuterVStackHeight shouldEqual actualOuterVStackHeight

        expectedInnerVStackWidth shouldEqual actualInnerVStackWidth
        expectedInnerVStackHeight shouldEqual actualInnerVStackHeight
    }
}