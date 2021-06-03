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
    fun `StatusBarAppearance can be de-serialized`() {
        // Arrange
        val adapter = JsonParser.moshi.adapter(StatusBarAppearance::class.java)
        var obj: Any? = null

        // Act
        try {
            obj = adapter.fromJson(TestJSON.statusBarAppearance)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is StatusBarAppearance)
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
    fun `AppBar can be de-serialized`() {
        // Arrange
        val expected = AppBar(
                showUpArrow = true,
                upArrowIconURL = "https://storage.judo.app/up-arrrow",
                iconColor = ColorVariants(
                        default = Color(
                                alpha = 0f,
                                blue = 0f,
                                green = 0f,
                                red = 0f
                        )),
                title = "AppBar!!",
                titleFont = Font.Fixed(
                        weight = FontWeight.Bold,
                        size = 20f,
                        isDynamic = false
                ),
                titleColor = ColorVariants(
                        default = Color(
                                alpha = 0f,
                                blue = 0f,
                                green = 0f,
                                red = 0f
                        )),
                backgroundColor = ColorVariants(
                        default = Color(
                                alpha = 0f,
                                blue = 0f,
                                green = 0f,
                                red = 0f
                        )),
                menuItems = listOf(MenuItem(
                        title = "Home",
                        titleFont = Font.Fixed(weight = FontWeight.Regular, size = 12F, isDynamic = true),
                        titleColor = ColorVariants(default = Color(1f, 1f, 1f, 1f)),
                        action = Action.Close(),
                        menuItemVisibility = MenuItemVisibility.ALWAYS,
                        icon = MenuItemIcon.AnIcon(Icon("house")),
                        contentDescription = "Home",
                        actionDescription = "Go Home"
                )),
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
    fun `Nav test Experience can be de-serialized`() {
        // Arrange
        var obj: Any? = null

        // Act
        try {
            obj = JsonParser.parseExperience(TestJSON.nav_test_judo)
        } catch (e: ExceptionInInitializerError) {
            println(e.exception.message)
        }

        // Assert
        assertTrue(obj is Experience)
    }

}