package app.judo.sdk.utils

import app.judo.sdk.api.models.*


class TextFactory(
    private val idFactory: IDFactory = IDFactory()
) {

    fun makeText1(): Text {
        return  Text(
            id = idFactory.text1Id,
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
            )
        )
    }

}
