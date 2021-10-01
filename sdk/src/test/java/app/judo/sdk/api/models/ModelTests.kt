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

package app.judo.sdk.api.models

import app.judo.sdk.core.data.JsonParser
import app.judo.sdk.core.data.JudoMessage
import app.judo.sdk.utils.TestJSON
import app.judo.sdk.utils.shouldEqual
import org.junit.Assert.*
import org.junit.Test

class
ModelTests {

    @Test
    fun `Color can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Color::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.color)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Color)
    }

    @Test
    fun `ColorVariants can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(ColorVariants::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.colorVariants)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is ColorVariants)
    }

    @Test
    fun `Shadow can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Shadow::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.shadow)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Shadow)
    }

    @Test
    fun `JudoMessage can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(JudoMessage::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.judo_message)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is JudoMessage)
    }

    @Test
    fun `Screen can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Screen::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.screen)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Screen)
    }

    @Test
    fun `Action can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Action::class.java)
        var close: Action.Close? = null
        var performSeguePush: Action.PerformSegue? = null
        var performSegueModal: Action.PerformSegue? = null
        var openURL: Action.OpenURL? = null
        var presentWebsite: Action.PresentWebsite? = null
        var custom: Action.Custom? = null

        // Act
        try {
            close = adapter.fromJson(TestJSON.action_Close) as? Action.Close
            performSeguePush = adapter.fromJson(TestJSON.action_PerformSegue_push) as? Action.PerformSegue
            performSegueModal = adapter.fromJson(TestJSON.action_PerformSegue_modal) as? Action.PerformSegue
            openURL = adapter.fromJson(TestJSON.action_openURL) as? Action.OpenURL
            presentWebsite = adapter.fromJson(TestJSON.action_PresentWebsite) as? Action.PresentWebsite
            custom = adapter.fromJson(TestJSON.action_Custom) as? Action.Custom
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertNotNull(close)
        assertNotNull(performSeguePush?.screenID)
        assertNotNull(performSegueModal?.screenID)
        assertNotNull(openURL?.url)
        assertNotNull(presentWebsite?.url)
        assertNotNull(custom)
    }

    @Test
    fun `Border can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Border::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.border)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Border)
    }

    @Test
    fun `Fill can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Fill::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.fill_flat)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Fill)
    }

    @Test
    fun `GradientVariants can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(GradientVariants::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.gradientVariant)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is GradientVariants)
    }

    @Test
    fun `Font can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Font::class.java)
        var fixed: Font? = null
        var dynamic: Font? = null
        var custom: Font? = null

        // Act
        try {
            fixed = adapter.fromJson(TestJSON.font_fixed)
            dynamic = adapter.fromJson(TestJSON.font_dynamic)
            custom = adapter.fromJson(TestJSON.font_custom)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(fixed is Font.Fixed)
        assertTrue(dynamic is Font.Dynamic)
        assertTrue(custom is Font.Custom)
    }

    @Test
    fun `FontResource can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(FontResource::class.java)
        var single: FontResource? = null
        var family: FontResource? = null

        // Act
        try {
            single = adapter.fromJson(TestJSON.resource_font_single)
            family = adapter.fromJson(TestJSON.resource_font_family)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(single is FontResource.Single)
        assertTrue(family is FontResource.Collection)
    }

    @Test
    fun `Localizations can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Map::class.java)
        var actual: Map<*, *>? = null

        // Act
        try {
            actual = adapter.fromJson(TestJSON.localization)
            print("$actual")
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(actual is Map<*, *>)
        assertFalse(actual?.isNullOrEmpty() == true)
    }

    @Test
    fun `Carousel can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Carousel::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.carousel)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Carousel)
    }

    @Test
    fun `Text can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Text::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.text)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Text)
    }

    @Test
    fun `Frame can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Frame::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.frame)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Frame)
    }

    @Test
    fun `WebView can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(WebView::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.webView)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is WebView)
    }

    @Test
    fun `URL WebViewSource can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(WebViewSource::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.webViewSource_URL)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is WebViewSource.URL)
    }

    @Test
    fun `HTML WebViewSource can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(WebViewSource::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.webViewSource_HTML)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is WebViewSource.HTML)
    }

    @Test
    fun `WebView with URL source can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(WebView::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.webView_URL_source)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        println(obj)
        assertTrue(obj is WebView)
    }


    @Test
    fun `WebView with HTML source can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(WebView::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.webView_HTML_source)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        println(obj)
        assertTrue(obj is WebView)
    }


    @Test
    fun `DataSource can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(DataSource::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.data_source)
            println(obj)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is DataSource)
    }

    @Test
    fun `Collection can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Collection::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.collection)
            println(obj)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Collection)
    }

    @Test
    fun `Conditional can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(Conditional::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.conditional)
            println(obj)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Conditional)
    }

    @Test
    fun `AppBar can be de-serialized`() {
        // Arrange
        val expected = AppBar(
                id = "54BDDF49-BD4D-4232-AA87-15FA584D819C",
                hideUpIcon = false,
                childIDs = listOf(
                    "2BE7C25E-FC7D-454A-B611-69FD25202BBF",
                    "197BD70A-0B99-4BDB-9DD2-FBCA78B441C5"
                ),
                buttonColor = ColorVariants(
                        default = Color(
                                alpha = 1f,
                                blue = 1f,
                                green = 1f,
                                red = 1f
                        )),
                title = "Screen",
                titleFont = Font.Fixed(
                        weight = FontWeight.Medium,
                        size = 20f,
                        isDynamic = false
                ),
                titleColor = ColorVariants(
                        default = Color(
                                alpha = 1f,
                                blue = 1f,
                                green = 1f,
                                red = 1f
                        )),
                backgroundColor = ColorVariants(
                        default = Color(
                                alpha = 1f,
                                blue = 0.9f,
                                green = 0f,
                                red = 0.3f
                        )
                ),
        )
        val adapter = JsonParser.moshi.adapter(AppBar::class.java)
        var actual: Any? = null

        // Act
        try {
            actual = adapter.fromJson(TestJSON.app_bar)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        println(actual)
        expected shouldEqual actual
    }

    @Test
    fun `Experience can be de-serialized`() {
        // Arrange
        var obj: Any? = null

        // Act
        try {
            obj = JsonParser.parseExperience(TestJSON.experience)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Experience)
    }

    @Test
    fun `Experience with interpolated DataSources can be de-serialized`() {
        // Arrange
        var obj: Any? = null

        // Act
        try {
            obj = JsonParser.parseExperience(TestJSON.interpolated_data_source_url_expereience)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Experience)
    }

    @Test
    fun `Nav test Experience can be de-serialized`() {
        // Arrange
        var obj: Any? = null

        // Act
        try {
            obj = JsonParser.parseExperience(TestJSON.nav_test_experience)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Experience)
    }

}