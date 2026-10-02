package com.example.ui.viewmodel

import android.app.Application
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.ColorBlindnessData
import com.example.data.model.ColorPerceptionReport
import com.example.data.model.EyeTestRecord
import com.example.data.model.SnellenChartData
import com.example.data.model.SnellenLine
import com.example.data.repository.EyeTestRepository
import com.example.ui.components.EDirection
import com.example.ui.components.VisionFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AcuityTestType { SNELLEN_LETTERS, TUMBLING_E }
enum class EyeTested(val label: String, val abbrev: String) {
    RIGHT_EYE("Right Eye (OD)", "OD"),
    LEFT_EYE("Left Eye (OS)", "OS"),
    BOTH_EYES("Both Eyes (OU)", "OU")
}

data class AcuityTestState(
    val isActive: Boolean = false,
    val testType: AcuityTestType = AcuityTestType.SNELLEN_LETTERS,
    val eyeTested: EyeTested = EyeTested.RIGHT_EYE,
    val currentLineIndex: Int = 0, // 0..10
    val currentLetter: String = "E",
    val currentEDirection: EDirection = EDirection.RIGHT,
    val options: List<String> = listOf("E", "F", "P", "T"),
    val correctCountOnLine: Int = 0,
    val attemptsOnLine: Int = 0,
    val highestPassedLineIndex: Int = -1,
    val isFinished: Boolean = false,
    val finalAcuity: String = "20/200",
    val finalMetric: String = "6/60",
    val finalAssessment: String = ""
)

enum class InputMode { KEYPAD, MULTIPLE_CHOICE }

data class IshiharaTestState(
    val currentPlateIndex: Int = 0,
    val userAnswers: Map<Int, String> = emptyMap(),
    val typedBuffer: String = "",
    val inputMode: InputMode = InputMode.KEYPAD,
    val activeVisionFilter: VisionFilter = VisionFilter.NORMAL,
    val isFinished: Boolean = false,
    val report: ColorPerceptionReport? = null,
    val diagnosisTitle: String = "",
    val diagnosisDetails: String = ""
)

data class Timer202020State(
    val isRunning: Boolean = false,
    val isBreakActive: Boolean = false,
    val secondsRemaining: Int = 20 * 60, // 20 minutes
    val breakSecondsRemaining: Int = 20,  // 20 seconds
    val completedSessionsToday: Int = 0
)

class EyeTestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EyeTestRepository
    private val vibrator: Vibrator? = application.getSystemService()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = EyeTestRepository(db.eyeTestDao())
    }

    val historyRecords: StateFlow<List<EyeTestRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Acuity Test State
    private val _acuityState = MutableStateFlow(AcuityTestState())
    val acuityState: StateFlow<AcuityTestState> = _acuityState.asStateFlow()

    // Ishihara Test State
    private val _ishiharaState = MutableStateFlow(IshiharaTestState())
    val ishiharaState: StateFlow<IshiharaTestState> = _ishiharaState.asStateFlow()

    // 20-20-20 Timer State
    private val _timerState = MutableStateFlow(Timer202020State())
    val timerState: StateFlow<Timer202020State> = _timerState.asStateFlow()
    private var timerJob: Job? = null

    // Acuity test start
    fun startAcuityTest(type: AcuityTestType, eye: EyeTested) {
        _acuityState.value = AcuityTestState(
            isActive = true,
            testType = type,
            eyeTested = eye,
            currentLineIndex = 0,
            highestPassedLineIndex = -1,
            isFinished = false
        )
        setupNextTrial()
    }

    private fun setupNextTrial() {
        val state = _acuityState.value
        val line = SnellenChartData.lines[state.currentLineIndex]
        val targetLetter = line.letters.random()

        // Generate 4 multiple choice letters for Snellen letters
        val optPool = listOf("E", "F", "P", "T", "O", "Z", "L", "D", "C")
        val distractors = optPool.filter { it != targetLetter }.shuffled().take(3)
        val options = (distractors + targetLetter).shuffled()

        val randomEDir = EDirection.values().random()

        _acuityState.update {
            it.copy(
                currentLetter = targetLetter,
                currentEDirection = randomEDir,
                options = options
            )
        }
    }

    fun submitAcuityAnswer(answer: String) {
        val state = _acuityState.value
        if (state.isFinished) return

        val isCorrect = if (state.testType == AcuityTestType.SNELLEN_LETTERS) {
            answer.equals(state.currentLetter, ignoreCase = true)
        } else {
            answer.equals(state.currentEDirection.label, ignoreCase = true)
        }

        vibrateShort(if (isCorrect) 40 else 120)

        val newAttempts = state.attemptsOnLine + 1
        val newCorrect = if (isCorrect) state.correctCountOnLine + 1 else state.correctCountOnLine

        // In optometry, testing 3 trials per line: 2/3 (66%) or 3/3 advances to smaller line
        if (newAttempts >= 3) {
            val passedCurrentLine = newCorrect >= 2
            val newHighestPassed = if (passedCurrentLine) state.currentLineIndex else state.highestPassedLineIndex

            if (passedCurrentLine && state.currentLineIndex < SnellenChartData.lines.lastIndex) {
                // Advance to next smaller line
                _acuityState.update {
                    it.copy(
                        currentLineIndex = it.currentLineIndex + 1,
                        correctCountOnLine = 0,
                        attemptsOnLine = 0,
                        highestPassedLineIndex = newHighestPassed
                    )
                }
                setupNextTrial()
            } else {
                // Test complete!
                finishAcuityTest(newHighestPassed)
            }
        } else {
            _acuityState.update {
                it.copy(
                    correctCountOnLine = newCorrect,
                    attemptsOnLine = newAttempts
                )
            }
            setupNextTrial()
        }
    }

    private fun finishAcuityTest(highestPassedIndex: Int) {
        val resultLine = if (highestPassedIndex >= 0) {
            SnellenChartData.lines[highestPassedIndex]
        } else {
            SnellenChartData.lines[0]
        }

        val acuityStr = if (highestPassedIndex >= 0) resultLine.imperialAcuity else "< 20/200"
        val metricStr = if (highestPassedIndex >= 0) resultLine.metricAcuity else "< 6/60"
        val assessment = SnellenChartData.getAssessmentForAcuity(acuityStr)

        val currentState = _acuityState.value
        _acuityState.update {
            it.copy(
                isFinished = true,
                finalAcuity = acuityStr,
                finalMetric = metricStr,
                finalAssessment = assessment
            )
        }

        // Save to Room
        viewModelScope.launch {
            repository.insertRecord(
                EyeTestRecord(
                    testType = if (currentState.testType == AcuityTestType.SNELLEN_LETTERS) "Visual Acuity (Snellen)" else "Visual Acuity (Tumbling E)",
                    eyeTested = currentState.eyeTested.label,
                    visualAcuity = acuityStr,
                    metricAcuity = metricStr,
                    logMar = resultLine.logMar,
                    scorePercent = ((highestPassedIndex + 1) * 100 / SnellenChartData.lines.size).coerceIn(10, 100),
                    assessment = assessment
                )
            )
        }
    }

    fun resetAcuityTest() {
        _acuityState.value = AcuityTestState()
    }

    // Ishihara Color Blindness Test
    fun startIshiharaTest() {
        _ishiharaState.value = IshiharaTestState(
            currentPlateIndex = 0,
            userAnswers = emptyMap(),
            typedBuffer = "",
            isFinished = false
        )
    }

    fun setIshiharaInputMode(mode: InputMode) {
        _ishiharaState.update { it.copy(inputMode = mode) }
    }

    fun setIshiharaVisionFilter(filter: VisionFilter) {
        _ishiharaState.update { it.copy(activeVisionFilter = filter) }
    }

    fun updateTypedBuffer(value: String) {
        _ishiharaState.update { it.copy(typedBuffer = value) }
    }

    fun submitCurrentTypedBuffer() {
        val state = _ishiharaState.value
        val plate = ColorBlindnessData.plates[state.currentPlateIndex]
        val value = if (state.typedBuffer.isBlank()) "Nothing" else state.typedBuffer.trim()
        submitIshiharaAnswer(plate.plateNumber, value)
    }

    fun submitIshiharaAnswer(plateNumber: Int, answer: String) {
        vibrateShort(40)
        val currentAnswers = _ishiharaState.value.userAnswers.toMutableMap()
        currentAnswers[plateNumber] = answer

        val nextIndex = _ishiharaState.value.currentPlateIndex + 1
        if (nextIndex < ColorBlindnessData.plates.size) {
            _ishiharaState.update {
                it.copy(
                    currentPlateIndex = nextIndex,
                    userAnswers = currentAnswers,
                    typedBuffer = ""
                )
            }
        } else {
            // Full perception report evaluation
            val report = ColorBlindnessData.verifyColorPerception(currentAnswers)
            _ishiharaState.update {
                it.copy(
                    userAnswers = currentAnswers,
                    typedBuffer = "",
                    isFinished = true,
                    report = report,
                    diagnosisTitle = report.title,
                    diagnosisDetails = report.summary
                )
            }

            // Save to Room
            viewModelScope.launch {
                repository.insertRecord(
                    EyeTestRecord(
                        testType = "Color Vision (Ishihara)",
                        eyeTested = EyeTested.BOTH_EYES.label,
                        visualAcuity = report.title,
                        scorePercent = report.normalScore * 100 / report.totalPlates,
                        assessment = "${report.severity} • ${report.summary}"
                    )
                )
            }
        }
    }

    fun resetIshiharaTest() {
        _ishiharaState.value = IshiharaTestState()
    }

    // Astigmatism test recording
    fun recordAstigmatismResult(eye: EyeTested, selectedHour: Int?, hasAstigmatismSigns: Boolean) {
        val acuityStr = if (!hasAstigmatismSigns) {
            "No Astigmatism Detected"
        } else {
            val axis = if (selectedHour != null) ((selectedHour * 30) % 180) else 90
            "Possible Astigmatism (~${axis}° axis)"
        }

        val assessment = if (!hasAstigmatismSigns) {
            "All radial spokes appeared equally sharp and uniform. Normal corneal curvature indicated."
        } else {
            "Hour $selectedHour meridians appeared darker or sharper. This often indicates refractive cylindrical asymmetry (astigmatism). An optometrist can measure exact cylinder power."
        }

        viewModelScope.launch {
            repository.insertRecord(
                EyeTestRecord(
                    testType = "Astigmatism Dial",
                    eyeTested = eye.label,
                    visualAcuity = acuityStr,
                    scorePercent = if (!hasAstigmatismSigns) 100 else 75,
                    assessment = assessment
                )
            )
        }
    }

    // Duochrome recording
    fun recordDuochromeResult(eye: EyeTested, selection: String) {
        val (acuityStr, assessment) = when (selection) {
            "RED" -> Pair(
                "Red Dominant (Myopic shift)",
                "Letters on red appeared sharper. In clinical optometry, this indicates rays focus anterior to retina (possible uncorrected myopia / under-correction)."
            )
            "GREEN" -> Pair(
                "Green Dominant (Hyperopic shift)",
                "Letters on green appeared sharper. Indicates rays focus posterior to retina (possible uncorrected hyperopia / over-correction)."
            )
            else -> Pair(
                "Balanced Refraction",
                "Letters on red and green appeared equally sharp and clear. Your spherical focus is optimally balanced at testing distance."
            )
        }

        viewModelScope.launch {
            repository.insertRecord(
                EyeTestRecord(
                    testType = "Duochrome (Red/Green)",
                    eyeTested = eye.label,
                    visualAcuity = acuityStr,
                    scorePercent = if (selection == "EQUAL") 100 else 80,
                    assessment = assessment
                )
            )
        }
    }

    // Near vision recording
    fun recordNearVisionResult(eye: EyeTested, readableLevel: String, description: String) {
        viewModelScope.launch {
            repository.insertRecord(
                EyeTestRecord(
                    testType = "Near Reading Acuity",
                    eyeTested = eye.label,
                    visualAcuity = readableLevel,
                    scorePercent = 100,
                    assessment = description
                )
            )
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // 20-20-20 Timer controls
    fun toggle202020Timer() {
        if (_timerState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _timerState.update { it.copy(isRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning) {
                delay(1000)
                if (!_timerState.value.isBreakActive) {
                    val remaining = _timerState.value.secondsRemaining - 1
                    if (remaining <= 0) {
                        // Trigger break!
                        vibratePattern()
                        _timerState.update {
                            it.copy(
                                isBreakActive = true,
                                secondsRemaining = 20 * 60,
                                breakSecondsRemaining = 20
                            )
                        }
                    } else {
                        _timerState.update { it.copy(secondsRemaining = remaining) }
                    }
                } else {
                    // In 20-sec break
                    val breakRem = _timerState.value.breakSecondsRemaining - 1
                    if (breakRem <= 0) {
                        vibrateShort(200)
                        _timerState.update {
                            it.copy(
                                isBreakActive = false,
                                breakSecondsRemaining = 20,
                                completedSessionsToday = it.completedSessionsToday + 1
                            )
                        }
                    } else {
                        _timerState.update { it.copy(breakSecondsRemaining = breakRem) }
                    }
                }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _timerState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerState.update {
            it.copy(
                isRunning = false,
                isBreakActive = false,
                secondsRemaining = 20 * 60,
                breakSecondsRemaining = 20
            )
        }
    }

    private fun vibrateShort(durationMs: Long) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Throwable) {}
    }

    private fun vibratePattern() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 150, 100, 150), -1)
            }
        } catch (_: Throwable) {}
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
