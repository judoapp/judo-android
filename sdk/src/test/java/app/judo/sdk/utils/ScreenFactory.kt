package app.judo.sdk.utils

import app.judo.sdk.api.models.Color
import app.judo.sdk.api.models.ColorVariants
import app.judo.sdk.api.models.Screen
import app.judo.sdk.api.models.StatusBarStyle
import app.judo.sdk.ui.layout.composition.SizeAndCoordinates


class ScreenFactory(
    private val idFactory: IDFactory = IDFactory(),
) {

    fun makeScreen1(): Screen {
        return Screen(
            id = idFactory.screen1Id,
            name = "Screen 1",
            childIDs = listOf(idFactory.text1Id, idFactory.image1Id, idFactory.image3Id),
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
    }

    fun makeScreen2(): Screen {
        return Screen(
            id = idFactory.screen2Id,
            name = "Screen 2",
            childIDs = listOf(idFactory.text1Id, idFactory.image2Id),
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
    }

    fun makeScreenWithSize(): Screen {
        return Screen(
            id = idFactory.screen2Id,
            name = "Screen 2",
            childIDs = listOf(idFactory.text1Id, idFactory.image2Id),
            backgroundColor = ColorVariants(
                default = Color(
                    0f,
                    red = 0F,
                    green = 0F,
                    blue = 0F,
                )
            ),
            statusBarStyle = StatusBarStyle.DEFAULT
        ).apply {
            sizeAndCoordinates = SizeAndCoordinates(width = 1080f, height = 1780f)
        }
    }

}
