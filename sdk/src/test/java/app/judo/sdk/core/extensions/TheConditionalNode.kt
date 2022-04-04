package app.judo.sdk.core.extensions

import app.judo.sdk.api.models.Conditional
import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.dataContextOf
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.lang.Keyword
import app.judo.sdk.utils.TestJSON
import org.junit.Assert
import org.junit.Assert.*

import org.junit.Test

class TheConditionalNode {

    @Test
    fun `Can be resolved from a given DataContext `() {
        // Arrange
        val conditional = JsonParser.moshi.adapter(Conditional::class.java).fromJson(TestJSON.conditional)!!

        val dataContext = dataContextOf(
            Keyword.DATA.value to mapOf("first_name" to "George")
        )

        // Act
        val actual = conditional.resolve(dataContext = dataContext, interpolator = InterpolatorImpl(
            dataContext = dataContext
        ))

        // Assert
        assertTrue(actual)
    }

}