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

class OriginalImageSizingTests {
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

    private fun createImageWithFrame(
        resizingMode: ResizingMode = ResizingMode.ORIGINAL,
        resolution: Float = 1f,
        frame: Frame? = null,
        padding: Padding? = null
    ): Image {
        return Image(
            id = "",
            imageURL = "",
            imageHeight = 100,
            imageWidth = 100,
            resizingMode = resizingMode,
            resolution = resolution,
            frame = frame,
            padding = padding,
        )
    }

    @Test
    fun `given inf constraints original sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val image = createImageWithFrame()
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = image.sizeAndCoordinates.width
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints original res 2 sizing is correct`() {
        // Arrange
        val expectedWidth = 50f
        val expectedHeight = 50f

        val image = createImageWithFrame(resolution = 2f)
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = image.sizeAndCoordinates.width
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints original frame res 2 sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val image = createImageWithFrame(ResizingMode.ORIGINAL, 2f, Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf vertical constraint original sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val expectedHeight = 100f

        val image = createImageWithFrame(ResizingMode.ORIGINAL,1f, null)
        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = image.sizeAndCoordinates.width
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf vertical constraints original frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f

        val image = createImageWithFrame(ResizingMode.ORIGINAL, resolution = 1f, Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints original frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f

        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf vertical constraints original frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f

        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints original frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original sizing res 2 is correct`() {
        // Arrange
        val expectedHeight = 50f
        val expectedWidth = 50f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 2f, null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f,
            Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints original with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val image = createImageWithFrame(ResizingMode.ORIGINAL, 1f, Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}