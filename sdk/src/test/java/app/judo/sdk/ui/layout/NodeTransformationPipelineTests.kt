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

package app.judo.sdk.ui.layout

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.extensions.resolve
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.utils.shouldEqual
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import java.util.*

class NodeTransformationPipelineTests {

    private val mockEnvironment = mock<Environment>()



    private val screenID = UUID.randomUUID().toString()
    private val dataSourceID = UUID.randomUUID().toString()
    private val dataSource2ID = UUID.randomUUID().toString()
    private val vStackID = UUID.randomUUID().toString()
    private val rectangleID = UUID.randomUUID().toString()
    private val rectangle2ID = UUID.randomUUID().toString()
    private val rectangle3ID = UUID.randomUUID().toString()
    private val rectangle4ID = UUID.randomUUID().toString()
    private val conditionalID = UUID.randomUUID().toString()
    private val conditional2ID = UUID.randomUUID().toString()
    private val collectionID = UUID.randomUUID().toString()
    private val collection2ID = UUID.randomUUID().toString()
    private val defaultColorVariant = ColorVariants(default = Color(1f, 1f, 1f, 1f))

    private fun createScreen(childIDs: List<String>): Screen {
        return Screen(
            id = screenID,
            childIDs = childIDs,
            backgroundColor = defaultColorVariant,
            androidStatusBarBackgroundColor = defaultColorVariant,
            androidStatusBarStyle = StatusBarStyle.INVERTED
        )
    }

    private fun createCollection(id: String, childIDs: List<String>): Collection {
        return Collection(
            id = collectionID,
            childIDs = childIDs,
            filters = listOf(),
            keyPath = "",
            sortDescriptors = listOf()
        )
    }

    private fun createDataSource(id: String, childIDs: List<String>): DataSource {
        return DataSource(
            id = id,
            url = "",
            httpMethod = HttpMethod.GET,
            headers = listOf(),
            childIDs = childIDs
        )
    }

    private fun createRectangle(id: String): Rectangle {
        return Rectangle(
            id = id,
            fill = Fill.FlatFill(defaultColorVariant),
            cornerRadius = 0f
        )
    }

    private fun createVStack(id: String = vStackID, childIDs: List<String>): VStack {
        return VStack(
            id = id,
            spacing = 0f,
            childIDs = childIDs,
            alignment = HorizontalAlignment.CENTER
        )
    }

    private fun createConditional(id: String, childIDs: List<String> = listOf(), conditions: List<Condition> = listOf()): Conditional {
        return Conditional(
            id = id,
            conditions = conditions,
            childIDs = childIDs,
        )
    }

    @Test
    fun `given datasource with no children datasource correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(dataSourceID)),
            createDataSource(id = dataSourceID, childIDs = listOf())
        )

        val expectedNodes = listOf<Node>(createScreen(listOf()))

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given datasource with children datasource correctly removed`() {
        // Arrange
        val nodes = listOf(
            createScreen(childIDs = listOf(dataSourceID)),
            createDataSource(id = dataSourceID, childIDs = listOf(rectangleID)),
            createRectangle(id = rectangleID)
        )

        val expectedNodes = listOf<Node>(
            createRectangle(id = rectangleID),
            createScreen(childIDs = listOf(rectangleID))
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given nested dataSources with children datasource correctly removed`() {
        // Arrange
        val nodes = listOf(
            createScreen(childIDs = listOf(dataSourceID)),
            createDataSource(id = dataSourceID, childIDs = listOf(dataSource2ID)),
            createDataSource(id = dataSource2ID, childIDs = listOf(rectangleID)),
            createRectangle(id = rectangleID)
        )

        val expectedNodes = listOf<Node>(
            createRectangle(id = rectangleID),
            createScreen(childIDs = listOf(rectangleID))
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given container with multiple children ordering is preserved when datasource removed`() {
        // Arrange
        val nodes = listOf(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, dataSourceID, rectangle2ID)),
            createDataSource(id = dataSourceID, childIDs = listOf(rectangle3ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangleID),
            createVStack(childIDs = listOf(rectangleID, rectangle3ID, rectangle2ID)),
            createRectangle(id = rectangle3ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given container with multiple children and nested dataSources ordering is preserved when datasource removed`() {
        // Arrange
        val nodes = listOf(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(dataSourceID, rectangleID)),
            createDataSource(id = dataSourceID, childIDs = listOf(rectangle3ID, dataSource2ID, rectangle2ID)),
            createDataSource(id = dataSource2ID, childIDs = listOf(rectangle4ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID),
            createRectangle(id = rectangle4ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(rectangle3ID, rectangle4ID, rectangle2ID, rectangleID)),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle3ID),
            createRectangle(id = rectangle4ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    private val mockInterpolator = mock<Interpolator>().apply {
    }

    @Test
    fun `given conditional with no children conditional correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(conditionalID)),
            createConditional(id = conditionalID, childIDs = listOf())
        )

        val expectedNodes = listOf<Node>(createScreen(listOf()))

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given conditional with children conditional correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(conditionalID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID)),
            createRectangle(id = rectangleID)
        )

        val expectedNodes = listOf<Node>(
            createRectangle(id = rectangleID),
            createScreen(listOf(rectangleID))
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given conditional with children that resolve to false they are correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(conditionalID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID), conditions = listOf(
                Condition("", Predicate.IS_TRUE, null)
            )),
            createRectangle(id = rectangleID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf())
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given nested conditional with one that resolves to false they are correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(conditionalID)),
            createConditional(id = conditionalID, childIDs = listOf(conditional2ID)),
            createConditional(id = conditional2ID, childIDs = listOf(rectangleID),
                conditions = listOf(
                    Condition("", Predicate.IS_TRUE, null)
                )),
            createRectangle(id = rectangleID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf())
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given nested conditionals with children conditionals correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(conditionalID)),
            createConditional(id = conditionalID, childIDs = listOf(conditional2ID)),
            createConditional(id = conditional2ID, childIDs = listOf(rectangleID)),
            createRectangle(id = rectangleID)
        )

        val expectedNodes = listOf<Node>(
            createRectangle(id = rectangleID),
            createScreen(listOf(rectangleID))
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given container with multiple children conditionals correctly removed and order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, conditionalID, rectangle3ID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangle2ID)),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle3ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, rectangle2ID, rectangle3ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given container with multiple children conditionals correctly removed and order preserved 2`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(conditionalID, rectangle2ID, rectangle3ID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID)),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle3ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, rectangle2ID, rectangle3ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given container with multiple children false conditionals correctly removed and order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(conditionalID, rectangle2ID, rectangle3ID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID),
                conditions = listOf(
                    Condition("", Predicate.IS_TRUE, null)
                )
            ),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle3ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf(vStackID)),
            createVStack(childIDs = listOf(rectangle2ID, rectangle3ID)),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given container with multiple children conditionals correctly removed and order preserved 3`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(conditionalID, rectangle2ID, conditional2ID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID),
                conditions = listOf(
                    Condition("", Predicate.IS_TRUE, null)
                )
            ),
            createConditional(id = conditional2ID, childIDs = listOf(rectangle3ID)),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle3ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf(vStackID)),
            createVStack(childIDs = listOf(rectangle2ID, rectangle3ID)),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given container with multiple children conditionals correctly removed and order preserved 4`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(conditionalID, rectangle2ID, conditional2ID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID)),
            createConditional(id = conditional2ID, childIDs = listOf(rectangle3ID, rectangle4ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID),
            createRectangle(id = rectangle4ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, rectangle2ID, rectangle3ID, rectangle4ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID),
            createRectangle(id = rectangle4ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given container with multiple children nested conditionals correctly removed and order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, conditionalID, rectangle2ID)),
            createConditional(id = conditionalID, childIDs = listOf(conditional2ID)),
            createConditional(id = conditional2ID, childIDs = listOf(rectangle3ID, rectangle4ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID),
            createRectangle(id = rectangle4ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, rectangle3ID, rectangle4ID, rectangle2ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID),
            createRectangle(id = rectangle4ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given container with multiple children nested conditionals correctly removed and order preserved 2`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, conditionalID, rectangle2ID)),
            createConditional(id = conditionalID, childIDs = listOf(conditional2ID)),
            createConditional(id = conditional2ID, childIDs = listOf(rectangle3ID, rectangle4ID),
            conditions = listOf(
                Condition("", Predicate.IS_TRUE, null)
            )),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID),
            createRectangle(id = rectangle4ID)
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf(vStackID)),
            createVStack(childIDs = listOf(rectangleID, rectangle2ID)),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given collection with no children or items collection is correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(collectionID)),
            createCollection(id = collectionID, childIDs = listOf()).apply {
                items = null
            }
        )

        val expectedNodes = listOf<Node>(createScreen(listOf()))

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes shouldEqual outputNodes
    }

    @Test
    fun `given collection with children and items collection is correctly removed`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(collectionID)),
            createCollection(id = collectionID, childIDs = listOf(rectangleID)).apply {
                items = listOf(1, 2, 3)
            },
            createRectangle(id = rectangleID)
        )

        val copiedRectID = "$collectionID-0-$rectangleID"
        val copiedRectID1 = "$collectionID-1-$rectangleID"
        val copiedRectID2 = "$collectionID-2-$rectangleID"

        val expectedNodes = listOf<Node>(
            createScreen(listOf(copiedRectID, copiedRectID1, copiedRectID2)),
            createRectangle(id = copiedRectID),
            createRectangle(id = copiedRectID1),
            createRectangle(id = copiedRectID2)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given collection with children and items collection is correctly removed with order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(rectangle2ID, collectionID, rectangle3ID)),
            createCollection(id = collectionID, childIDs = listOf(rectangleID)).apply {
                items = listOf(1, 2, 3)
            },
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID)
        )

        val copiedRectID = "$collectionID-0-$rectangleID"
        val copiedRectID1 = "$collectionID-1-$rectangleID"
        val copiedRectID2 = "$collectionID-2-$rectangleID"

        val expectedNodes = listOf<Node>(
            createScreen(listOf(rectangle2ID, copiedRectID, copiedRectID1, copiedRectID2, rectangle3ID)),
            createRectangle(id = copiedRectID),
            createRectangle(id = copiedRectID1),
            createRectangle(id = copiedRectID2),
            createRectangle(id = rectangle2ID),
            createRectangle(id = rectangle3ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given collection with conditional children and items collection is correctly removed with order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(collectionID)),
            createCollection(id = collectionID, childIDs = listOf(conditionalID)).apply {
                items = listOf(1, 2, 3)
            },
            createConditional(id = conditionalID, childIDs = listOf(rectangleID)),
            createRectangle(id = rectangleID),
        )

        val copiedRectID = "$collectionID-0-$rectangleID"
        val copiedRectID1 = "$collectionID-1-$rectangleID"
        val copiedRectID2 = "$collectionID-2-$rectangleID"

        val expectedNodes = listOf<Node>(
            createScreen(listOf(copiedRectID, copiedRectID1, copiedRectID2)),
            createRectangle(id = copiedRectID),
            createRectangle(id = copiedRectID1),
            createRectangle(id = copiedRectID2),
            // this shouldn't be here but doesn't affect functionality
            createRectangle(id = rectangleID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given collection with false conditional children and items collection is correctly removed with order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(collectionID)),
            createCollection(id = collectionID, childIDs = listOf(conditionalID)).apply {
                items = listOf(1, 2, 3)
            },
            createConditional(id = conditionalID, childIDs = listOf(rectangleID),
                conditions = listOf(
                    Condition("", Predicate.IS_TRUE, null)
                )
            ),
            createRectangle(id = rectangleID),
        )

        val expectedNodes = listOf<Node>(
            createScreen(listOf()),
            // this shouldn't be here but doesn't affect functionality
            createRectangle(id = rectangleID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given collection with nested conditional children and items collection is correctly removed with order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(collectionID)),
            createCollection(id = collectionID, childIDs = listOf(vStackID)).apply {
                items = listOf(1, 2, 3)
            },
            createVStack(childIDs = listOf(conditionalID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID)),
            createRectangle(id = rectangleID),
        )

        val copiedRectID = "$collectionID-0-$rectangleID"
        val copiedRectID1 = "$collectionID-1-$rectangleID"
        val copiedRectID2 = "$collectionID-2-$rectangleID"

        val copiedVStackID = "$collectionID-0-$vStackID"
        val copiedVStackID1 = "$collectionID-1-$vStackID"
        val copiedVStackID2 = "$collectionID-2-$vStackID"

        val expectedNodes = listOf<Node>(
            createScreen(listOf(copiedVStackID, copiedVStackID1, copiedVStackID2)),
            createRectangle(id = copiedRectID),
            createRectangle(id = copiedRectID1),
            createRectangle(id = copiedRectID2),
            createVStack(id = copiedVStackID, childIDs = listOf(copiedRectID)),
            createVStack(id = copiedVStackID1, childIDs = listOf(copiedRectID1)),
            createVStack(id = copiedVStackID2, childIDs = listOf(copiedRectID2)),
            // this shouldn't be here but doesn't affect functionality
            createRectangle(id = rectangleID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given collection with nested false conditional children and items collection is correctly removed with order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(collectionID)),
            createCollection(id = collectionID, childIDs = listOf(vStackID)).apply {
                items = listOf(1, 2, 3)
            },
            createVStack(childIDs = listOf(conditionalID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID),
                conditions = listOf(
                    Condition("", Predicate.IS_TRUE, null)
                )
            ),
            createRectangle(id = rectangleID),
        )

        val copiedVStackID = "$collectionID-0-$vStackID"
        val copiedVStackID1 = "$collectionID-1-$vStackID"
        val copiedVStackID2 = "$collectionID-2-$vStackID"

        val copiedConditionalID = "$collectionID-0-$conditionalID"
        val copiedConditionalID1 = "$collectionID-1-$conditionalID"
        val copiedConditionalID2 = "$collectionID-2-$conditionalID"

        val expectedNodes = listOf<Node>(
            createScreen(listOf(copiedVStackID, copiedVStackID1, copiedVStackID2)),
            createVStack(id = copiedVStackID, childIDs = listOf(copiedConditionalID)),
            createVStack(id = copiedVStackID1, childIDs = listOf(copiedConditionalID1)),
            createVStack(id = copiedVStackID2, childIDs = listOf(copiedConditionalID2)),
            // this shouldn't be here but doesn't affect functionality
            createRectangle(id = rectangleID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }

    @Test
    fun `given collection with two conditional children collection is correctly removed with order preserved`() {
        // Arrange
        val nodes = listOf<Node>(
            createScreen(childIDs = listOf(collectionID)),
            createCollection(id = collectionID, childIDs = listOf(vStackID)).apply {
                items = listOf(1, 2, 3)
            },
            createVStack(childIDs = listOf(conditionalID, conditional2ID)),
            createConditional(id = conditionalID, childIDs = listOf(rectangleID)),
            createConditional(id = conditional2ID, childIDs = listOf(rectangle2ID),
                conditions = listOf(Condition("", Predicate.IS_TRUE, null))
            ),
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID)
        )

        val copiedRectID = "$collectionID-0-$rectangleID"
        val copiedRectID1 = "$collectionID-1-$rectangleID"
        val copiedRectID2 = "$collectionID-2-$rectangleID"

        val copiedVStackID = "$collectionID-0-$vStackID"
        val copiedVStackID1 = "$collectionID-1-$vStackID"
        val copiedVStackID2 = "$collectionID-2-$vStackID"

        val copiedConditionalID = "$collectionID-0-$conditional2ID"
        val copiedConditionalID1 = "$collectionID-1-$conditional2ID"
        val copiedConditionalID2 = "$collectionID-2-$conditional2ID"

        val expectedNodes = listOf<Node>(
            createScreen(listOf(copiedVStackID, copiedVStackID1, copiedVStackID2)),
            createRectangle(id = copiedRectID),
            createRectangle(id = copiedRectID1),
            createRectangle(id = copiedRectID2),
            createVStack(id = copiedVStackID, childIDs = listOf(copiedRectID, copiedConditionalID)),
            createVStack(id = copiedVStackID1, childIDs = listOf(copiedRectID1, copiedConditionalID1)),
            createVStack(id = copiedVStackID2, childIDs = listOf(copiedRectID2, copiedConditionalID2)),
            // this shouldn't be here but doesn't affect functionality
            createRectangle(id = rectangleID),
            createRectangle(id = rectangle2ID)
        )

        val nodeTransformationPipeline = NodeTransformationPipeline(mockEnvironment)

        // Act
        val outputNodes = nodeTransformationPipeline.transformScreenNodesForLayout(
            nodes,
            emptyList(),
            screenID,
            defaultInterpolator = mockInterpolator,
            userInfo = emptyMap()
        ).nodes

        // Assert
        expectedNodes.sortedBy { it.id } shouldEqual outputNodes.sortedBy { it.id }
    }
}
