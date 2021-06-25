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

internal class VideoSizingTests {

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

    private fun createVideoWithFrame(frame: Frame? = null): Video {
        return Video(
            id = "",
            frame = frame,
            autoPlay = false,
            looping = false,
            posterImageURL = "",
            removeAudio = false,
            resizingMode = VideoResizingMode.RESIZE_TO_FILL,
            showControls = true,
            sourceURL = ""
        )
    }

    private fun createVideoWithAspect(aspectRatio: Float, frame: Frame? = null): Video {
        return Video(
            id = "",
            aspectRatio = aspectRatio,
            frame = frame,
            autoPlay = false,
            looping = false,
            posterImageURL = "",
            removeAudio = false,
            resizingMode = VideoResizingMode.RESIZE_TO_FILL,
            showControls = true,
            sourceURL = ""
        )
    }

    @Test
    fun `given inf constraints video sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val expectedHeight = 0f

        val video = createVideoWithFrame()
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = video.sizeAndCoordinates.width
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints video frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val video = createVideoWithFrame(Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints video frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val video = createVideoWithFrame(Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints video frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val video = createVideoWithFrame(Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints video frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f

        val video = createVideoWithFrame(Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints video frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val video = createVideoWithFrame(Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints video frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val video = createVideoWithFrame(Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints video frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val video = createVideoWithFrame(Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val video = createVideoWithFrame(null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val video = createVideoWithFrame(Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val video = createVideoWithFrame(Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val video = createVideoWithFrame(Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val video = createVideoWithFrame(Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val video = createVideoWithFrame(Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val video = createVideoWithFrame(Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints video with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val video = createVideoWithFrame(Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    // aspect ratio

    @Test
    fun `given inf constraints and aspect ratio video sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val expectedHeight = 0f

        val video = createVideoWithAspect(1f)
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = video.sizeAndCoordinates.width
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio video frame sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 100f

        val video = createVideoWithAspect(2f, Frame(height = 100f, width = 100f, alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio video frame infinite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val video = createVideoWithAspect(2f, Frame(maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio video frame finite maxHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 0f

        val video = createVideoWithAspect(2f, Frame(maxHeight = MaxHeight.Finite(300f), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio video frame infinite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f

        val video = createVideoWithAspect(2f, Frame(maxWidth = MaxWidth.Infinite(), alignment = Alignment.CENTER))
        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualHeight
    }

    @Test
    fun `given inf constraints and aspect ratio video frame finite maxWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 0f
        val video = createVideoWithAspect(2f, Frame(maxWidth = MaxWidth.Finite(300f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints and aspect ratio video frame minWidth sizing is correct`() {
        // Arrange
        val expectedWidth = 100f
        val video = createVideoWithAspect(2f, Frame(minWidth = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
    }

    @Test
    fun `given inf constraints and aspect ratio video frame minHeight sizing is correct`() {
        // Arrange
        val expectedHeight = 100f
        val video = createVideoWithAspect(2f, Frame(minHeight = 100f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Inf, Dimension.Inf)

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height

        // Assert
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video sizing is correct`() {
        // Arrange
        val expectedHeight = 150f
        val expectedWidth = 300f
        val video = createVideoWithAspect(2f, null)

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video frame sizing larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 400f
        val expectedWidth = 400f
        val video = createVideoWithAspect(2f, Frame(width = 400f, height = 400f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video frame sizing smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 200f
        val video = createVideoWithAspect(2f, Frame(width = 200f, height = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video frame minHeight and minWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 200f
        val expectedWidth = 300f
        val video = createVideoWithAspect(2f, Frame(minWidth = 200f, minHeight = 200f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video frame minHeight and minWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val video = createVideoWithAspect(2f, Frame(minWidth = 300f, minHeight = 300f, alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(200f), Dimension.Value(200f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video frame maxHeight and maxWidth smaller than constraints is correct`() {
        // Arrange
        val expectedHeight = 100f
        val expectedWidth = 200f
        val video = createVideoWithAspect(2f, Frame(maxWidth = MaxWidth.Finite(200f), maxHeight = MaxHeight.Finite(200f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video frame maxHeight and maxWidth larger than constraints is correct`() {
        // Arrange
        val expectedHeight = 150f
        val expectedWidth = 300f
        val video = createVideoWithAspect(2f, Frame(maxWidth = MaxWidth.Finite(400f), maxHeight = MaxHeight.Finite(400f), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }

    @Test
    fun `given value constraints and aspect ratio video with frame inf maxHeight and inf maxWidth sizing is correct`() {
        // Arrange
        val expectedHeight = 300f
        val expectedWidth = 300f
        val video = createVideoWithAspect(2f, Frame(maxWidth = MaxWidth.Infinite(), maxHeight = MaxHeight.Infinite(), alignment = Alignment.CENTER))

        val parentConstraints = Dimensions(Dimension.Value(300f), Dimension.Value(300f))

        // Act
        video.computeSize(mockContext, screenNode, parentConstraints)
        val actualHeight = video.sizeAndCoordinates.height
        val actualWidth = video.sizeAndCoordinates.width

        // Assert
        expectedWidth shouldEqual actualWidth
        expectedHeight shouldEqual actualHeight
    }
}