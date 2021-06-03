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

class DividerSizingTests {
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

    private val hStackNode = TreeNode(HStack(
        id = "2",
        childIDs = listOf(),
        spacing = 1f,
        alignment = VerticalAlignment.CENTER
    ))

    private val vStackNode = TreeNode(VStack(
        id = "4",
        childIDs = listOf(),
        spacing = 1f,
        alignment = HorizontalAlignment.CENTER
    ))

    private val horizontalDividerNode = TreeNode(parentId = vStackNode.value.id, value = Divider(
        id = "3",
        backgroundColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
    )).apply {
        this.parent = vStackNode
    }

    private val verticalDividerNode = TreeNode(parentId = hStackNode.value.id, value = Divider(
        id = "5",
        backgroundColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
    )).apply {
        this.parent = hStackNode
    }
    

    private fun createDividerWithFrame(frame: Frame? = null, padding: Padding? = null): Divider {
        return Divider(
            id = "",
            backgroundColor = ColorVariants(default = Color(0f, 0f , 0f, 0f)),
            frame = frame,
            padding = padding,
        )
    }

    // horizontal divider

    @Test
    fun `given vertical inf constraints hdivider sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 1f

        val divider = createDividerWithFrame()
        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Inf)

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualWidth = divider.sizeAndCoordinates.width
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints hdivider frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val divider = createDividerWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints hdivider frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 1f

        val divider = createDividerWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints hdivider frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 1f

        val divider = createDividerWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints divider frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f

        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints divider frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val divider = createDividerWithFrame(Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints divider frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val divider = createDividerWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider sizing is correct`() {
        // Arrange
        val expectedHeight = 1f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val divider = createDividerWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val divider = createDividerWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints divider with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, horizontalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }


    // vertical divider

    @Test
    fun `given horizontal inf constraints vdivider sizing is correct`() {
        // Arrange
        val expectedWidth = 1f
        val expectedHeight = 200f

        val divider = createDividerWithFrame()
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Value(200f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualWidth = divider.sizeAndCoordinates.width
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vdivider frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val divider = createDividerWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vdivider frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val divider = createDividerWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vdivider frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val divider = createDividerWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vdivider frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 1f

        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints vdivider frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val divider = createDividerWithFrame(Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints vdivider frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val divider = createDividerWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 1f
        val divider = createDividerWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val divider = createDividerWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val divider = createDividerWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 200f
        val divider = createDividerWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints vdivider with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val divider = createDividerWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        divider.computeSize(mockContext, verticalDividerNode, parentConstraints)
        val actualHeight = divider.sizeAndCoordinates.height
        val actualWidth = divider.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}