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

class ScaleToFillImageSizingTests {
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
        resizingMode: ResizingMode = ResizingMode.SCALE_TO_FILL,
        frame: Frame? = null,
        padding: Padding? = null
    ): Image {
        return Image(
            id = "",
            imageURL = "",
            imageHeight = 100,
            imageWidth = 200,
            resizingMode = resizingMode,
            resolution = 1f,
            frame = frame,
            padding = padding,
        )
    }

    // scale to fit
    @Test
    fun `given inf constraints scale to fill sizing is correct`() {
        // Arrange
        val expectedWidth = 1f
        val expectedHeight = 1f

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
    fun `given inf constraints scale to fill frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
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
    fun `given inf vertical constraint scale to fill sizing is correct`() {
        // Arrange
        val expectedWidth = 200f
        val expectedHeight = 1f

        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL,null)
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
    fun `given inf vertical constraints scale to fill frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 1f

        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints scale to fill frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 1f

        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf vertical constraints scale to fill frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 1f

        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Value(400f), Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints scale to fill frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 200f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints scale to fill sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, null)

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
    fun `given value constraints scale to fill scales correctly correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, null)

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        image.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = image.sizeAndCoordinates.height
        val actualWidth = image.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints scale to fill frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL,
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
    fun `given value constraints scale to fill frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

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
    fun `given value constraints scale to fill frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

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
    fun `given value constraints scale to fill frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

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
    fun `given value constraints scale to fill frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

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
    fun `given value constraints scale to fill with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val image = createImageWithFrame(ResizingMode.SCALE_TO_FILL, Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

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