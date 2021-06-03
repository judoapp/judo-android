package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import app.judo.sdk.utils.shouldEqual
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

class VideoPositioningTests {

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

    private fun createVideoWithFrame(frame: Frame? = null, offset: Point? = null, padding: Padding? = null,
                                         background: Background? = null, overlay: Overlay? = null): Video {
        return Video(
            id = "",
            offset = offset,
            autoPlay = false,
            looping = false,
            posterImageURL = "",
            removeAudio = false,
            resizingMode = VideoResizingMode.RESIZE_TO_FILL,
            sourceURL = "",
            showControls = false,
            padding = padding,
            frame = frame,
            background = background,
            overlay = overlay,
        )
    }

    private fun createRectangleWithFrame(frame: Frame? = null, offset: Point? = null, padding: Padding? = null,
                                         background: Background? = null, overlay: Overlay? = null): Rectangle {
        return Rectangle(
            id = "",
            offset = offset,
            padding = padding,
            fill = Fill.FlatFill(ColorVariants(default = Color(0f, 0f , 0f, 0f))),
            cornerRadius = 0f,
            frame = frame,
            background = background,
            overlay = overlay,
        )
    }

    @Test
    fun `given default positioning video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val video = createVideoWithFrame()

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val video = createVideoWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER))

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning with parent positioning video positioning is correct`() {
        // Arrange
        val expectedX = 100f
        val expectedY = 100f

        val video = createVideoWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER))

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        video.computePosition(mockContext, FloatPoint(100f, 100f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom trailing frame positioning video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val video = createVideoWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_TRAILING))

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning and offset video positioning is correct`() {
        // Arrange
        val expectedX = 100f
        val expectedY = 100f

        val video = createVideoWithFrame(
            Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            offset = Point(100, 100)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning and negative offset video positioning is correct`() {
        // Arrange
        val expectedX = -100f
        val expectedY = -100f

        val video = createVideoWithFrame(
            Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            offset = Point(-100, -100)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top leading frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_LEADING),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top trailing frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 0f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_TRAILING),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 0f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 20f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom trailing frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 20f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_TRAILING),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom leading frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 20f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_LEADING),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given leading frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.LEADING),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given trailing frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TRAILING),
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    // padding positioning

    @Test
    fun `given center frame and padding positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top leading frame and padding positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top trailing frame positioning with padding with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 20f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_TRAILING),
            padding = Padding(20f, 0f, 0f, 20f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top frame positioning with padding with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP),
            padding = Padding(10f, 10f, 0f, 0f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 90f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom frame positioning with padding with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 0f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM),
            padding = Padding(0f, 10f, 10f, 0f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 90f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given padding with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val video = createVideoWithFrame(
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom leading with padding frame positioning with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given leading frame positioning with padding with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given trailing frame positioning with padding with smaller content width video positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 10f

        val video = createVideoWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TRAILING),
            padding = Padding(10f, 0f, 10f, 10f)
        )

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 80f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = video.sizeAndCoordinates.x
        val actualY = video.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning rectangle background positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val backgroundNode = createRectangleWithFrame().apply {
            sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        }

        val video = createVideoWithFrame(background = Background(backgroundNode, Alignment.CENTER))

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (video.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background positioning and sizing is correct`() {
        // Arrange
        val expectedX = 25f
        val expectedY = 25f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val video = createVideoWithFrame(background = Background(backgroundNode, Alignment.CENTER))

        // Act
        video.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        video.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (video.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }
}