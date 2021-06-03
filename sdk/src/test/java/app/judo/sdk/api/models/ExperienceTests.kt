package app.judo.sdk.api.models

import app.judo.sdk.utils.*
import org.junit.Before
import org.junit.Test

class ExperienceTests {

    private lateinit var idFactory: IDFactory
    private lateinit var imageFactory: ImageFactory
    private lateinit var screenFactory: ScreenFactory
    private lateinit var factory: ExperienceFactory
    private lateinit var experience: Experience

    @Before
    fun setUp() {

        idFactory = IDFactory()

        imageFactory = ImageFactory(idFactory = idFactory)

        screenFactory = ScreenFactory(idFactory = idFactory)

        factory = ExperienceFactory(
            idFactory = idFactory,
            imageFactory = imageFactory
        )

        experience = factory.makeExperience()
    }

    @Test
    fun nodes() {

        val expected = listOf(
            screenFactory.makeScreen1(),
            screenFactory.makeScreen2()
        )

        val actual: List<Screen> = experience.nodes()

        expected shouldEqual actual
    }
}