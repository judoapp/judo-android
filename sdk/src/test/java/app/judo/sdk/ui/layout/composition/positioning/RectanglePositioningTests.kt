package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.sizing.computeSize
import app.judo.sdk.utils.shouldEqual
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

class RectanglePositioningTests {

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
    fun `given default positioning rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val rectangle = createRectangleWithFrame()

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val rectangle = createRectangleWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning with parent positioning rectangle positioning is correct`() {
        // Arrange
        val expectedX = 100f
        val expectedY = 100f

        val rectangle = createRectangleWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        rectangle.computePosition(mockContext, FloatPoint(100f, 100f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom trailing frame positioning rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val rectangle = createRectangleWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_TRAILING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning and offset rectangle positioning is correct`() {
        // Arrange
        val expectedX = 100f
        val expectedY = 100f

        val rectangle = createRectangleWithFrame(
            Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            offset = Point(100, 100)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning and negative offset rectangle positioning is correct`() {
        // Arrange
        val expectedX = -100f
        val expectedY = -100f

        val rectangle = createRectangleWithFrame(
            Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            offset = Point(-100, -100)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top leading frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_LEADING),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top trailing frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 0f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_TRAILING),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 0f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 20f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom trailing frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 20f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_TRAILING),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom leading frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 20f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_LEADING),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given leading frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.LEADING),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given trailing frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TRAILING),
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    // padding positioning

    @Test
    fun `given center frame and padding positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top leading frame and padding positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top trailing frame positioning with padding with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 20f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_TRAILING),
            padding = Padding(20f, 0f, 0f, 20f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top frame positioning with padding with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP),
            padding = Padding(10f, 10f, 0f, 0f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 90f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom frame positioning with padding with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 0f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM),
            padding = Padding(0f, 10f, 10f, 0f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 90f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given padding with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom leading with padding frame positioning with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given leading frame positioning with padding with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given trailing frame positioning with padding with smaller content width rectangle positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 10f

        val rectangle = createRectangleWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TRAILING),
            padding = Padding(10f, 0f, 10f, 10f)
        )

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 80f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = rectangle.sizeAndCoordinates.x
        val actualY = rectangle.sizeAndCoordinates.y

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

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.CENTER))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
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

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.CENTER))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background top leading alignment is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.TOP_LEADING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background top alignment is correct`() {
        // Arrange
        val expectedX = 25f
        val expectedY = 0f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.TOP))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background top trailing alignment is correct`() {
        // Arrange
        val expectedX = 50f
        val expectedY = 0f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.TOP_TRAILING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background leading alignment is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 25f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.LEADING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background trailing alignment is correct`() {
        // Arrange
        val expectedX = 50f
        val expectedY = 25f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.TRAILING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background bottom leading alignment is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 50f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.BOTTOM_LEADING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background bottom center alignment is correct`() {
        // Arrange
        val expectedX = 25f
        val expectedY = 50f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.BOTTOM))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background bottom center alignment 2 is correct`() {
        // Arrange
        val expectedX = 25f
        val expectedY = 50f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.BOTTOM))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 50f, contentWidth = 50f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background bottom trailing alignment is correct`() {
        // Arrange
        val expectedX = 50f
        val expectedY = 50f

        val backgroundNode = createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.BOTTOM_TRAILING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning frame rectangle background bottom trailing alignment 2 is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val backgroundNode = createRectangleWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER)).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 100f, height = 100f, contentWidth = 100f, contentHeight = 100f)
        }

        val rectangle = createRectangleWithFrame(background = Background(backgroundNode, Alignment.BOTTOM_TRAILING))

        // Act
        rectangle.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentHeight = 100f, contentWidth = 100f)
        rectangle.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (rectangle.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }
}