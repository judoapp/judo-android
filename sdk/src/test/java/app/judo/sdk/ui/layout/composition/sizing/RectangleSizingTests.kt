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

internal class RectangleSizingTests {

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

    private fun createRectangleWithFrame(frame: Frame? = null): Rectangle {
        return Rectangle(
            id = "",
            fill = Fill.FlatFill(ColorVariants(default = Color(0f, 0f , 0f, 0f))),
            cornerRadius = 0f,
            frame = frame,
        )
    }

    private fun createRectangleWithAspect(aspectRatio: Float, frame: Frame? = null): Rectangle {
        return Rectangle(
            id = "",
            fill = Fill.FlatFill(ColorVariants(default = Color(0f, 0f , 0f, 0f))),
            aspectRatio = aspectRatio,
            cornerRadius = 0f,
            frame = frame,
        )
    }

    @Test
    fun `given inf constraints rectangle sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val expectedHeight = 0f

        val rectangle = createRectangleWithFrame()
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = rectangle.sizeAndCoordinates.width
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints rectangle frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val rectangle = createRectangleWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints rectangle frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val rectangle = createRectangleWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints rectangle frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val rectangle = createRectangleWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints rectangle frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f

        val rectangle = createRectangleWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints rectangle frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val rectangle = createRectangleWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints rectangle frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val rectangle = createRectangleWithFrame(Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints rectangle frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val rectangle = createRectangleWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val rectangle = createRectangleWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val rectangle = createRectangleWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val rectangle = createRectangleWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val rectangle = createRectangleWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val rectangle = createRectangleWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val rectangle = createRectangleWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val rectangle = createRectangleWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints rectangle with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val rectangle = createRectangleWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    // aspect ratio

    @Test
    fun `given inf constraints and aspect ratio rectangle sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val expectedHeight = 0f

        val rectangle = createRectangleWithAspect(1f)
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = rectangle.sizeAndCoordinates.width
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio rectangle frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val rectangle = createRectangleWithAspect(2f, Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio rectangle frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val rectangle = createRectangleWithAspect(2f, Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio rectangle frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val rectangle = createRectangleWithAspect(2f, Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio rectangle frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f

        val rectangle = createRectangleWithAspect(2f, Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio rectangle frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val rectangle = createRectangleWithAspect(2f, Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints and aspect ratio rectangle frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val rectangle = createRectangleWithAspect(2f, Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints and aspect ratio rectangle frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val rectangle = createRectangleWithAspect(2f, Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle sizing is correct`() {
        // Arrange
        val expectedHeight = 150f
        val expectedWidth = 300f
        val rectangle = createRectangleWithAspect(2f, null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val rectangle = createRectangleWithAspect(2f, Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val rectangle = createRectangleWithAspect(2f, Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 300f
        val rectangle = createRectangleWithAspect(2f, Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val rectangle = createRectangleWithAspect(2f, Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 200f
        val rectangle = createRectangleWithAspect(2f, Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 150f
        val expectedWidth = 300f
        val rectangle = createRectangleWithAspect(2f, Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio rectangle with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val rectangle = createRectangleWithAspect(2f, Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        rectangle.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = rectangle.sizeAndCoordinates.height
        val actualWidth = rectangle.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}