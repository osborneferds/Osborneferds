package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.EDirection
import com.example.ui.viewmodel.AcuityTestType
import com.example.ui.viewmodel.EyeTestViewModel
import com.example.ui.viewmodel.EyeTested
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class EyeTestViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: EyeTestViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = EyeTestViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        val acuityState = viewModel.acuityState.value
        assertFalse(acuityState.isActive)

        val timerState = viewModel.timerState.value
        assertFalse(timerState.isRunning)
        assertEquals(20 * 60, timerState.secondsRemaining)
    }

    @Test
    fun testStartAcuityTestSnellen() = runTest {
        viewModel.startAcuityTest(AcuityTestType.SNELLEN_LETTERS, EyeTested.RIGHT_EYE)
        val state = viewModel.acuityState.value
        assertTrue(state.isActive)
        assertEquals(AcuityTestType.SNELLEN_LETTERS, state.testType)
        assertEquals(EyeTested.RIGHT_EYE, state.eyeTested)
        assertEquals(0, state.currentLineIndex)
        assertEquals("E", state.currentLetter)
    }

    @Test
    fun testAcuityTestCorrectAnswerProgression() = runTest {
        viewModel.startAcuityTest(AcuityTestType.SNELLEN_LETTERS, EyeTested.RIGHT_EYE)

        // Line 0 trials (3 attempts with 3 correct answers advances line)
        repeat(3) {
            val letter = viewModel.acuityState.value.currentLetter
            viewModel.submitAcuityAnswer(letter)
        }

        // After passing line 0, it should advance to line 1
        val state = viewModel.acuityState.value
        assertEquals(1, state.currentLineIndex)
        assertEquals(0, state.highestPassedLineIndex)
    }

    @Test
    fun testStartAcuityTestTumblingE() = runTest {
        viewModel.startAcuityTest(AcuityTestType.TUMBLING_E, EyeTested.LEFT_EYE)
        val state = viewModel.acuityState.value
        assertTrue(state.isActive)
        assertEquals(AcuityTestType.TUMBLING_E, state.testType)
        assertEquals(EyeTested.LEFT_EYE, state.eyeTested)
    }

    @Test
    fun testIshiharaTestLifecycleAndBuffer() = runTest {
        viewModel.startIshiharaTest()
        var state = viewModel.ishiharaState.value
        assertEquals(0, state.currentPlateIndex)
        assertEquals("", state.typedBuffer)

        // Test typing buffer
        viewModel.updateTypedBuffer("12")
        state = viewModel.ishiharaState.value
        assertEquals("12", state.typedBuffer)

        // Submit answer for plate 1
        viewModel.submitIshiharaAnswer(plateNumber = 1, answer = "12")
        state = viewModel.ishiharaState.value
        assertEquals(1, state.currentPlateIndex)
        assertEquals("", state.typedBuffer)
        assertEquals("12", state.userAnswers[1])
    }

    @Test
    fun testAstigmatismResultSaving() = runTest {
        viewModel.recordAstigmatismResult(
            eye = EyeTested.RIGHT_EYE,
            selectedHour = null,
            hasAstigmatismSigns = false
        )
        advanceUntilIdle()

        val records = viewModel.historyRecords.value
        assertNotNull(records)
    }

    @Test
    fun testDuochromeResultSaving() = runTest {
        viewModel.recordDuochromeResult(
            eye = EyeTested.BOTH_EYES,
            selection = "EQUAL"
        )
        advanceUntilIdle()

        val records = viewModel.historyRecords.value
        assertNotNull(records)
    }

    @Test
    fun testNearVisionResultSaving() = runTest {
        viewModel.recordNearVisionResult(
            eye = EyeTested.RIGHT_EYE,
            readableLevel = "J1 (20/25)",
            description = "Sharp near acuity"
        )
        advanceUntilIdle()

        val records = viewModel.historyRecords.value
        assertNotNull(records)
    }

    @Test
    fun test202020TimerToggle() = runTest {
        viewModel.toggle202020Timer()
        assertTrue(viewModel.timerState.value.isRunning)

        viewModel.toggle202020Timer()
        assertFalse(viewModel.timerState.value.isRunning)
    }
}
