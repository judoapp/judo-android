package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.utils.shouldEqual
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

class ZStackSizingTests {
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

    private fun createZStackWithFrame(frame: Frame? = null, padding: Padding? = null): ZStack {
        return ZStack(
            id = "1",
            frame = frame,
            padding = padding,
            alignment = Alignment.CENTER,
        )
    }

    private fun createRectangleWithFrame(frame: Frame? = null): Rectangle {
        return Rectangle(
            id = "2",
            fill = Fill.FlatFill(ColorVariants(default = Color(0f, 0f , 0f, 0f))),
            cornerRadius = 0f,
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

    // value constraints

    @Test
    fun `given value constraints zstack-no-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame()
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-noframe-rect-orig-image-layout prio is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame()
        val image = createImageWithFrame(layoutPriority = 1f)
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(frame = Frame(200f, 200f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-min-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(frame = Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-max-frame-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(frame = Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-max-frame-inf-with-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(frame = Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-max-frame-inf-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(frame = Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(frame = Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-max-frame-with-small-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(frame = Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(frame = Frame(maxWidth = MaxWidth.Finite(300f), maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    // value constraints with fixed child

    @Test
    fun `given value constraints zstack-no-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame()
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-no-frame-with-fixed-rect-padding sizing is correct`() {
        // Arrange
        val expectedWidth = 220f
        val expectedHeight = 220f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(padding = Padding(10f, 10f,10f, 10f))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(Frame(300f, 300f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-max-inf-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-max-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints zstack-min-frame-with-fixed-rect sizing is correct`() {
        // Arrange
        val expectedWidth = 300f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))
        val treeNode = TreeNode(zStack).apply { addChild(TreeNode(rectangle)) }
        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    // vertical inf constrained

    @Test
    fun `given vertical inf constraints zstack-no-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 400f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame()
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given vertical inf constraints zstack-noframe-rect-orig-image layout prio sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame()
        val image = createImageWithFrame(layoutPriority = 1f)
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given vertical inf constraints zstack-no-frame-fixed-rect-padding sizing is correct`() {
        // Arrange
        val expectedWidth = 220f
        val expectedHeight = 220f

        val rectangle = createRectangleWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val zStack = createZStackWithFrame(padding = Padding(10f, 10f, 10f, 10f))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given vertical inf constraints zstack-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given vertical inf constraints zstack-min-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 400f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(minWidth = 200f, minHeight = 300f, alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given vertical inf constraints zstack-max-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given vertical inf constraints zstack-max-inf-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 400f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    // inf constrained

    @Test
    fun `given inf constraints zstack-no-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame()
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints zstack-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 200f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(200f, 200f, alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints zstack-min-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 300f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(minWidth = 200f, minHeight = 300f, alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints zstack-max-frame-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints zstack-max-frame-inf-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val rectangle = createRectangleWithFrame()
        val zStack = createZStackWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given vertical inf constraints zstack-rect-orig-image sizing is correct`() {
        // Arrange
        val expectedWidth = 400f
        val expectedHeight = 100f

        val expectedRectangleWidth = 400f
        val expectedRectangleHeight = 100f

        val expectedImageWidth = 100f
        val expectedImageHeight = 100f

        val zStack = createZStackWithFrame()
        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame()
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangleWidth shouldEqual rectangle.sizeAndCoordinates.width
        expectedRectangleHeight shouldEqual rectangle.sizeAndCoordinates.height

        expectedImageWidth shouldEqual image.sizeAndCoordinates.width
        expectedImageHeight shouldEqual image.sizeAndCoordinates.height
    }

    @Test
    fun `given vertical inf constraints zstack-rect-orig-image-scale sizing is correct`() {
        // Arrange
        val expectedWidth = 400f
        val expectedHeight = 400f

        val expectedRectangleWidth = 400f
        val expectedRectangleHeight = 400f

        val expectedImageWidth = 400f
        val expectedImageHeight = 400f

        val zStack = createZStackWithFrame()
        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangleWidth shouldEqual rectangle.sizeAndCoordinates.width
        expectedRectangleHeight shouldEqual rectangle.sizeAndCoordinates.height

        expectedImageWidth shouldEqual image.sizeAndCoordinates.width
        expectedImageHeight shouldEqual image.sizeAndCoordinates.height
    }

    @Test
    fun `given vertical inf constraints min frame zstack-rect-orig-image-scale sizing is correct`() {
        // Arrange
        val expectedWidth = 400f
        val expectedHeight = 400f

        val expectedRectangleWidth = 400f
        val expectedRectangleHeight = 400f

        val expectedImageWidth = 400f
        val expectedImageHeight = 400f

        val zStack = createZStackWithFrame(frame = Frame(minHeight = 100f, alignment = Alignment.CENTER))
        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangleWidth shouldEqual rectangle.sizeAndCoordinates.width
        expectedRectangleHeight shouldEqual rectangle.sizeAndCoordinates.height

        expectedImageWidth shouldEqual image.sizeAndCoordinates.width
        expectedImageHeight shouldEqual image.sizeAndCoordinates.height
    }

    @Test
    fun `given inf constraints zstack-rect-orig-image-scale sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val expectedRectangleWidth = 100f
        val expectedRectangleHeight = 100f

        val expectedImageWidth = 100f
        val expectedImageHeight = 100f

        val zStack = createZStackWithFrame()
        val rectangle = createRectangleWithFrame()
        val image = createImageWithFrame(resizingMode = ResizingMode.SCALE_TO_FIT)
        val treeNode = TreeNode(zStack).apply {
            addChild(TreeNode(rectangle))
            addChild(TreeNode(image))
        }

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        zStack.computeSize(mockContext, treeNode, parentConstraints)
        val actualWidth = zStack.sizeAndCoordinates.width
        val actualHeight = zStack.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight

        expectedRectangleWidth shouldEqual rectangle.sizeAndCoordinates.width
        expectedRectangleHeight shouldEqual rectangle.sizeAndCoordinates.height

        expectedImageWidth shouldEqual image.sizeAndCoordinates.width
        expectedImageHeight shouldEqual image.sizeAndCoordinates.height
    }
}