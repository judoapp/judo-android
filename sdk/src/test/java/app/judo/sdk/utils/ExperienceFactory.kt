package app.judo.sdk.utils

import app.judo.sdk.api.models.Appearance
import app.judo.sdk.api.models.Experience


class ExperienceFactory(
    private val idFactory: IDFactory = IDFactory(),
    var textFactory: TextFactory = TextFactory(idFactory),
    var imageFactory: ImageFactory = ImageFactory(idFactory),
    var screenFactory: ScreenFactory = ScreenFactory(idFactory),
) {

    fun makeExperience(): Experience {

        return Experience(
            id = "1",
            version = 1,
            revisionID = 1,
            nodes = listOf(
                screenFactory.makeScreen1(),
                screenFactory.makeScreen2(),
                textFactory.makeText1(),
                imageFactory.makeImage1(),
                imageFactory.makeImage2(),
                imageFactory.makeImage3()
            ),
            initialScreenID = idFactory.screen1Id,
            screenIDs = listOf(idFactory.screen1Id),
            appearance = Appearance.AUTO
        )
    }

}
