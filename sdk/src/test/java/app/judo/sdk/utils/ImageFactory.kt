package app.judo.sdk.utils

import app.judo.sdk.api.models.Image
import app.judo.sdk.api.models.ResizingMode


class ImageFactory(
    private val idFactory: IDFactory = IDFactory()
) {

    var image1URL = "https://content.judo.app/image1"
    var image2URL = "https://content.judo.app/image2"
    var image3URL = "https://content.judo.app/image3"

    fun makeImage1(): Image {
        return Image(
            id = idFactory.image1Id,
            imageURL = image1URL,
            resolution = 1f,
            resizingMode = ResizingMode.ORIGINAL,
            imageWidth = 100,
            imageHeight = 100,
        )
    }

    fun makeImage2(): Image {
        return Image(
            id = idFactory.image2Id,
            imageURL = image2URL,
            resolution = 1f,
            resizingMode = ResizingMode.ORIGINAL,
            imageWidth = 100,
            imageHeight = 100,
        )
    }

    fun makeImage3(): Image {
        return Image(
            id = idFactory.image3Id,
            imageURL = image3URL,
            resolution = 1f,
            resizingMode = ResizingMode.ORIGINAL,
            imageWidth = 100,
            imageHeight = 100,
        )
    }

}
