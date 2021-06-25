package app.judo.sdk.core.extensions

import org.junit.Assert
import org.junit.Test

class TheURLParams {

    @Test
    fun `Can be extrapolated from a url String`() {
        // Arrange
        val url = "https://brand.judo.app/exp?key1=value1&key2=value2"

        val expected = mapOf(
            "key1" to "value1",
            "key2" to "value2"
        )

        // Act
        val actual = url.urlParams()

        // Assert
        Assert.assertEquals(expected, actual)
    }

}