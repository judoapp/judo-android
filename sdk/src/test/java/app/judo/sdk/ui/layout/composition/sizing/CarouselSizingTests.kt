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

internal class CarouselSizingTests {

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

    private fun createCarouselWithFrame(frame: Frame? = null): Carousel {
        return Carousel(
            childIDs = listOf(),
            id = "",
            isLoopEnabled = true,
            frame = frame,
        )
    }

    @Test
    fun `given inf constraints carousel sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val expectedHeight = 0f

        val carousel = createCarouselWithFrame()
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = carousel.sizeAndCoordinates.width
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val carousel = createCarouselWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val carousel = createCarouselWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val carousel = createCarouselWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f

        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints carousel frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints carousel frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val carousel = createCarouselWithFrame(Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints carousel frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val carousel = createCarouselWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val carousel = createCarouselWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val carousel = createCarouselWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints carousel with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val carousel = createCarouselWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        carousel.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = carousel.sizeAndCoordinates.height
        val actualWidth = carousel.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}