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

class AudioPositioningTests {

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

    private fun createAudioWithFrame(frame: Frame? = null, offset: Point? = null, padding: Padding? = null,
                                     background: Background? = null, overlay: Overlay? = null): Audio {
        return Audio(
            id = "",
            offset = offset,
            autoPlay = false,
            looping = false,
            sourceURL = "",
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
    fun `given default positioning audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val audio = createAudioWithFrame()

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 70f, contentWidth = 100f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 15f

        val audio = createAudioWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER))

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning with parent positioning audio positioning is correct`() {
        // Arrange
        val expectedX = 100f
        val expectedY = 115f

        val audio = createAudioWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.CENTER))

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(100f, 100f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom trailing frame positioning audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 30f

        val audio = createAudioWithFrame(Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_TRAILING))

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning and offset audio positioning is correct`() {
        // Arrange
        val expectedX = 100f
        val expectedY = 115f

        val audio = createAudioWithFrame(
            Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            offset = Point(100, 100)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given center frame positioning and negative offset audio positioning is correct`() {
        // Arrange
        val expectedX = -100f
        val expectedY = -85f

        val audio = createAudioWithFrame(
            Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            offset = Point(-100, -100)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }


    @Test
    fun `given top leading frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_LEADING),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 100f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top trailing frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 0f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_TRAILING),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 0f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 30f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom trailing frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 30f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_TRAILING),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom leading frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 30f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_LEADING),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given leading frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 15f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.LEADING),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given trailing frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 20f
        val expectedY = 15f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TRAILING),
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    // padding positioning

    @Test
    fun `given center frame and padding positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 15f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.CENTER),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top leading frame and padding positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top trailing frame positioning with padding with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 20f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP_TRAILING),
            padding = Padding(20f, 0f, 0f, 20f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given top frame positioning with padding with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TOP),
            padding = Padding(10f, 10f, 0f, 0f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom frame positioning with padding with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 20f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM),
            padding = Padding(0f, 10f, 10f, 0f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given padding with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 10f

        val audio = createAudioWithFrame(
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 90f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given bottom leading with padding frame positioning with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 20f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.BOTTOM_LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given leading frame positioning with padding with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 10f
        val expectedY = 15f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.LEADING),
            padding = Padding(10f, 10f, 10f, 10f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 80f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given trailing frame positioning with padding with smaller content width audio positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 15f

        val audio = createAudioWithFrame(
            frame = Frame(width = 100f, height = 100f, alignment = Alignment.TRAILING),
            padding = Padding(10f, 0f, 10f, 10f)
        )

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 100f, contentWidth = 90f, contentHeight = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))
        val actualX = audio.sizeAndCoordinates.x
        val actualY = audio.sizeAndCoordinates.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }

    @Test
    fun `given default positioning rectangle background positioning is correct`() {
        // Arrange
        val expectedX = 0f
        val expectedY = 0f

        val audio = createAudioWithFrame(background = Background(createRectangleWithFrame().apply {
            sizeAndCoordinates = SizeAndCoordinates(100f, 70f, contentWidth = 100f, contentHeight = 70f)
        }, Alignment.CENTER))

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 70f, contentHeight = 100f, contentWidth = 70f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (audio.background?.node as? Layer)?.sizeAndCoordinates
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
        val expectedY = 10f

        val audio = createAudioWithFrame(background = Background(createRectangleWithFrame(Frame(width = 50f, height = 50f, alignment = Alignment.CENTER))
            .apply {
                sizeAndCoordinates = SizeAndCoordinates(width = 50f, height = 50f, contentWidth = 50f, contentHeight = 50f)
            }, Alignment.CENTER))

        // Act
        audio.sizeAndCoordinates = SizeAndCoordinates(100f, 70f, contentHeight = 70f, contentWidth = 100f)
        audio.computePosition(mockContext, FloatPoint(0f, 0f))


        val backgroundSizeAndCoordinates = (audio.background?.node as? Layer)?.sizeAndCoordinates
        val actualX = backgroundSizeAndCoordinates?.x
        val actualY = backgroundSizeAndCoordinates?.y

        // Assert
        expectedX shouldEqual actualX
        expectedY shouldEqual actualY
    }
}