package app.judo.sdk.utils

import app.judo.sdk.api.models.*
import java.util.*

@Suppress("FunctionName")
fun TestExperience(screen2Id: String? = null): Experience {
    val screen1ID = UUID.randomUUID().toString()
    val textId = UUID.randomUUID().toString()
    val text2Id = UUID.randomUUID().toString()
    val text3Id = UUID.randomUUID().toString()

    val screen1 = Screen(
        id = screen1ID,
        name = "Screen 1",
        childIDs = listOf(textId),
        backgroundColor = ColorVariants(
            default = Color(
                0f,
                red = 0F,
                green = 0F,
                blue = 0F,
            )
        ),
       statusBarStyle = StatusBarStyle.DEFAULT
    )

    val screen2 = Screen(
        id = screen2Id ?: UUID.randomUUID().toString(),
        name = "Screen 2",
        childIDs = listOf(textId),
        backgroundColor = ColorVariants(
            default = Color(
                0f,
                red = 0F,
                green = 0F,
                blue = 0F,
            )
        ),
        statusBarStyle = StatusBarStyle.DEFAULT
    )

    @Suppress("SpellCheckingInspection")
    val fonts =
        listOf<FontResource>(
            FontResource.Single(
                url = "https://content.judo.app/fonts/iwqmmerhbg32mh1.ttf",
                name = "AvenirNext"
            ),
            FontResource.Collection(
                url = "https://content.judo.app/fonts/grwqwl3l299j.ttc",
                names = listOf("AvenirNext", "AvenirNext-Bold", "AvenirNext-Thin")
            ),
        )

    return Experience(
        id = "1",
        version = 1,
        revisionID = 1,
        nodes = listOf(
            screen1,
            screen2,
            Text(
                id = textId,
                text = "Hello World coming at you from in memory!",
                font = Font.Custom(size = 20F, isDynamic = false, fontName = "AvenirNext"),
                textAlignment = TextAlignment.CENTER,
                textColor = ColorVariants(
                    default = Color(
                        1f,
                        red = 0F,
                        green = 0F,
                        blue = 0F,
                    ),
                )
            ),
            Text(
                id = text2Id,
                text = "Hello World coming at you from in memory! The sequel!",
                font = Font.Custom(size = 20F, isDynamic = false, fontName = "AvenirNext-Bold"),
                textAlignment = TextAlignment.CENTER,
                textColor = ColorVariants(
                    default = Color(
                        1f,
                        red = 0F,
                        green = 0F,
                        blue = 0F,
                    ),
                )
            ),
            Text(
                id = text3Id,
                text = "Hello World coming at you from in memory! It's a trilogy!",
                font = Font.Custom(size = 20F, isDynamic = false, fontName = "AvenirNext-Thin"),
                textAlignment = TextAlignment.CENTER,
                textColor = ColorVariants(
                    default = Color(
                        1f,
                        red = 0F,
                        green = 0F,
                        blue = 0F,
                    ),
                )
            ),
        ),
        fonts = fonts,
        initialScreenID = screen1ID,
        screenIDs = listOf(screen1.id, screen2.id),
        appearance = Appearance.AUTO
    )
}