package app.judo.sdk.core.data

import app.judo.sdk.core.robots.AbstractRobotTest
import app.judo.sdk.core.robots.JsonDAORobot
import org.junit.Assert
import org.junit.Test

internal class TheJsonDAO : AbstractRobotTest<JsonDAORobot>() {

    override fun robotSupplier(): JsonDAORobot {
        return JsonDAORobot()
    }

    @Test
    fun `Can find objects from a single key`() {
        // Arrange
        val expected = """{"name":"Bob","friends":["userB"]}"""

        val key = "userA"

        // Act
        val actual = robot.dao.findValueByKey(key = key)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Can find objects from a list of keys`() {
        // Arrange
        val expected = "Bob"

        val keys = listOf("userA", "name")

        // Act
        val actual = robot.dao.findValueByKeys(keys = keys)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Can find objects from a empty list of keys`() {
        // Arrange
        val expected = """{"userA":{"name":"Bob","friends":["userB"]},"pets":[{"owner":"userA","name":"DigDog"},{"owner":"userB","name":"CatManDo"}],"userB":{"name":"Bobo","friends":["userB"]}}"""

        val keys = emptyList<String>()

        // Act
        val actual = robot.dao.findValueByKeys(keys = keys)

        // Assert
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Can find an array of objects from a key`() {
        // Arrange
        val expected1 = """{"owner":"userA","name":"DigDog"}"""
        val expected2 = """{"owner":"userB","name":"CatManDo"}"""
        val expected3 = """userA"""

        val key = "pets"

        // Act
        val jsonDAOs = robot.dao.findArrayByKey(key = key)
        val actual1 = jsonDAOs[0].value()
        val actual2 = jsonDAOs[1].value()
        val actual3 = jsonDAOs[0].findValueByKey("owner")

        println("ACTUAL1: $actual1")
        println("ACTUAL2: $actual2")
        println("ACTUAL3: $actual3")

        // Assert
        Assert.assertEquals(expected1, actual1)
        Assert.assertEquals(expected2, actual2)
        Assert.assertEquals(expected3, actual3)
    }

}