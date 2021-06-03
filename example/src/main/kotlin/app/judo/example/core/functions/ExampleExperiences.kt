package app.judo.example.core.functions

import app.judo.sdk.api.models.*
import java.util.*

@Suppress("FunctionName")
fun InMemoryExperience(screen2Id: String? = null): Experience {
    val screen1ID = UUID.randomUUID().toString()
    val textId = UUID.randomUUID().toString()

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
                font = Font.Fixed(weight = FontWeight.Bold, size = 20F, isDynamic = false),
                textAlignment = TextAlignment.CENTER,
                textColor = ColorVariants(
                    default = Color(
                        1f,
                        red = 0F,
                        green = 0F,
                        blue = 0F,
                    ),
                ),
                name = null
            )
        ),
        initialScreenID = screen1ID,
        screenIDs = listOf(screen1.id, screen2.id),
        appearance = Appearance.AUTO,
        name = "example experience"
    )
}