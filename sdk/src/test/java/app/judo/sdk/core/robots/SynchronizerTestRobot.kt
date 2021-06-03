package app.judo.sdk.core.robots

import app.judo.sdk.core.implementations.SynchronizerImpl
import app.judo.sdk.core.sync.Synchronizer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert

@ExperimentalCoroutinesApi
internal class SynchronizerTestRobot : AbstractTestRobot() {

    private lateinit var synchronizer: Synchronizer

    override fun onSetUp() {
        super.onSetUp()

        backingEnvironment.domainNames = setOf("test1.judo.app")

        synchronizer = SynchronizerImpl(
            backingEnvironment
        )

    }

    suspend fun performSync(prefetchAssets: Boolean = false, onComplete: () -> Unit = {}) {
        synchronizer.performSync(prefetchAssets, onComplete)
    }

    fun assertTheLastURLPathToBeFetchedWas(expected: String) {
        Assert.assertEquals(expected, serverDispatcher.actualRequest?.path)
    }

    fun assertTheLastURLPathToBeFetchedStartsWith(expected: String) {
        Assert.assertTrue(serverDispatcher.actualRequest?.path?.startsWith(expected) == true)
    }

}
