package app.judo.sdk.ui.robots

import android.content.Intent
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Visitor
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_KEY
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_URL
import app.judo.sdk.core.environment.Environment.Keys.IGNORE_CACHE
import app.judo.sdk.core.environment.Environment.Keys.LOAD_FROM_MEMORY
import app.judo.sdk.core.environment.Environment.Keys.SCREEN_ID
import app.judo.sdk.core.implementations.EnvironmentImpl
import app.judo.sdk.api.data.UserDataSupplier
import app.judo.sdk.core.robots.AbstractTestRobot
import app.judo.sdk.ui.ExperienceViewModel
import app.judo.sdk.ui.events.ExperienceRequested
import app.judo.sdk.ui.models.ExperienceState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.junit.Assert

@ExperimentalCoroutinesApi
internal class ExperienceViewModelRobot : AbstractTestRobot() {

    private val scope = CoroutineScope(environment.mainDispatcher)

    private val experienceStates = mutableListOf<ExperienceState>()

    private val model = ExperienceViewModel(
        environment = environment,
        dispatcher = environment.mainDispatcher,
        ioDispatcher = environment.ioDispatcher,
    )

    private var stateJob: Job? = null

    override fun onSetUp() {
        super.onSetUp()
        stateJob = scope.launch {
            model.stateFlow.collect { judoState ->
                experienceStates += judoState
            }
        }
    }

    override fun onTearDown() {
        super.onTearDown()
        scope.cancel()
    }

    suspend fun assertThatExperienceStateAtPositionEquals(
        position: Int,
        state: ExperienceState,
    ) {
        if (state is ExperienceState.Retrieved) {
            delay(500)
            val stateInList = experienceStates[position] as? ExperienceState.Retrieved
            val expected = state.experience.id to state.screenId
            val actual = stateInList?.experience?.id to stateInList?.screenId
            Assert.assertEquals(expected, actual)
        } else {
            Assert.assertEquals(state, experienceStates[position])
        }
    }

    fun initializeExperienceFromURL(url: String) {

        val intent = Intent().apply {
            putExtra(EXPERIENCE_URL, url)
            putExtra(IGNORE_CACHE, true)
        }

        model.onEvent(
            event = ExperienceRequested(
                intent
            )
        )

    }

    fun loadExperienceIntoMemory(experience: Experience) {
        environment.experienceRepository.put(experience)
    }

    fun initializeExperienceFromMemory(judoKey: String, screenId: String? = null) {
        val intent = Intent().apply {
            putExtra(LOAD_FROM_MEMORY, true)
            putExtra(EXPERIENCE_KEY, judoKey)
            putExtra(SCREEN_ID, screenId)
        }

        model.onEvent(
            event = ExperienceRequested(
                intent
            )
        )
    }

    suspend fun inspectExperienceAt(position: Int, visitor: Visitor<*>) {
        delay(1000)
        (experienceStates[position] as? ExperienceState.Retrieved)
            ?.experience
            ?.let(visitor::visit)
    }

    fun setUserDataSupplierTo(supplier: UserDataSupplier) {
        (environment as? EnvironmentImpl)?.userDataSupplier = supplier
    }

}
