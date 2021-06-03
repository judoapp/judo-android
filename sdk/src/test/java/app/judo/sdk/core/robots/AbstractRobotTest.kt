package app.judo.sdk.core.robots

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class AbstractRobotTest<R : AbstractTestRobot> {

    lateinit var robot: R

    abstract fun robotSupplier(): R

    @Before
    open fun setUp() {

        robot = robotSupplier()

        robot.onSetUp()

    }

    @After
    open fun tearDown() {

        robot.onTearDown()

    }

}