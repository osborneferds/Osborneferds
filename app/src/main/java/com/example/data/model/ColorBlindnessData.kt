package com.example.data.model

enum class PlateCategory(val label: String) {
    DEMONSTRATION("Demonstration Plate"),
    TRANSFORMATION("Transformation Plate"),
    VANISHING("Vanishing Plate"),
    DIAGNOSTIC("Diagnostic / Classification Plate"),
    HIDDEN_DIGIT("Hidden Digit Plate")
}

data class IshiharaPlateDetail(
    val plateNumber: Int,
    val category: PlateCategory,
    val normalNumber: String,
    val protanNumber: String?,
    val deutanNumber: String?,
    val totalColorBlindNumber: String = "Nothing",
    val options: List<String>,
    val explanation: String
)

data class PlateEvaluation(
    val plateNumber: Int,
    val userInput: String,
    val normalExpected: String,
    val deficientExpected: String,
    val category: PlateCategory,
    val isNormalMatch: Boolean,
    val isProtanMatch: Boolean,
    val isDeutanMatch: Boolean,
    val interpretation: String
)

data class ColorPerceptionReport(
    val title: String,
    val severity: String,
    val confidencePercent: Int,
    val summary: String,
    val normalScore: Int,
    val totalPlates: Int,
    val protanIndex: Int,
    val deutanIndex: Int,
    val demonstrationPassed: Boolean,
    val detailedEvaluations: List<PlateEvaluation>,
    val clinicalRecommendations: String
)

object ColorBlindnessData {
    val plates: List<IshiharaPlateDetail> = listOf(
        IshiharaPlateDetail(
            plateNumber = 1,
            category = PlateCategory.DEMONSTRATION,
            normalNumber = "12",
            protanNumber = "12",
            deutanNumber = "12",
            totalColorBlindNumber = "12",
            options = listOf("12", "72", "18", "Nothing"),
            explanation = "Demonstration plate. Individuals with normal vision as well as those with red-green or total color vision deficiencies should clearly read 12."
        ),
        IshiharaPlateDetail(
            plateNumber = 2,
            category = PlateCategory.TRANSFORMATION,
            normalNumber = "8",
            protanNumber = "3",
            deutanNumber = "3",
            options = listOf("8", "3", "5", "Nothing"),
            explanation = "Normal trichromats read 8. Individuals with red-green deficiencies (both protan and deutan) typically read 3. Total color blindness reads nothing."
        ),
        IshiharaPlateDetail(
            plateNumber = 3,
            category = PlateCategory.TRANSFORMATION,
            normalNumber = "29",
            protanNumber = "70",
            deutanNumber = "70",
            options = listOf("29", "70", "28", "Nothing"),
            explanation = "Normal trichromats read 29. Red-green color deficient individuals read 70."
        ),
        IshiharaPlateDetail(
            plateNumber = 4,
            category = PlateCategory.TRANSFORMATION,
            normalNumber = "5",
            protanNumber = "2",
            deutanNumber = "2",
            options = listOf("5", "2", "3", "Nothing"),
            explanation = "Normal trichromats read 5. Red-green color deficient individuals read 2."
        ),
        IshiharaPlateDetail(
            plateNumber = 5,
            category = PlateCategory.TRANSFORMATION,
            normalNumber = "74",
            protanNumber = "21",
            deutanNumber = "21",
            options = listOf("74", "21", "71", "Nothing"),
            explanation = "Normal trichromats read 74. Red-green color deficient individuals read 21."
        ),
        IshiharaPlateDetail(
            plateNumber = 6,
            category = PlateCategory.VANISHING,
            normalNumber = "6",
            protanNumber = null,
            deutanNumber = null,
            options = listOf("6", "8", "9", "Nothing"),
            explanation = "Normal trichromats read 6. The majority of color vision deficient individuals cannot read this number or read it incorrectly."
        ),
        IshiharaPlateDetail(
            plateNumber = 7,
            category = PlateCategory.VANISHING,
            normalNumber = "45",
            protanNumber = null,
            deutanNumber = null,
            options = listOf("45", "15", "46", "Nothing"),
            explanation = "Normal trichromats read 45. Most color vision deficient individuals see nothing distinct."
        ),
        IshiharaPlateDetail(
            plateNumber = 8,
            category = PlateCategory.VANISHING,
            normalNumber = "16",
            protanNumber = null,
            deutanNumber = null,
            options = listOf("16", "18", "15", "Nothing"),
            explanation = "Normal trichromats read 16. Most color vision deficient individuals see nothing distinct."
        ),
        IshiharaPlateDetail(
            plateNumber = 9,
            category = PlateCategory.VANISHING,
            normalNumber = "7",
            protanNumber = null,
            deutanNumber = null,
            options = listOf("7", "1", "4", "Nothing"),
            explanation = "Normal trichromats read 7. Red-green deficient individuals cannot perceive this digit."
        ),
        IshiharaPlateDetail(
            plateNumber = 10,
            category = PlateCategory.VANISHING,
            normalNumber = "73",
            protanNumber = null,
            deutanNumber = null,
            options = listOf("73", "18", "78", "Nothing"),
            explanation = "Normal trichromats read 73. Most color vision deficient individuals see only random dots."
        ),
        IshiharaPlateDetail(
            plateNumber = 11,
            category = PlateCategory.DIAGNOSTIC,
            normalNumber = "26",
            protanNumber = "6",
            deutanNumber = "2",
            options = listOf("26", "6", "2", "Nothing"),
            explanation = "Classification plate: Normal trichromats read 26. In protanopia (red-blindness), 6 is read. In deuteranopia (green-blindness), 2 is read."
        ),
        IshiharaPlateDetail(
            plateNumber = 12,
            category = PlateCategory.DIAGNOSTIC,
            normalNumber = "42",
            protanNumber = "2",
            deutanNumber = "4",
            options = listOf("42", "2", "4", "Nothing"),
            explanation = "Classification plate: Normal trichromats read 42. In protanopia, 2 is read. In deuteranopia, 4 is read."
        )
    )

    fun verifyColorPerception(userAnswers: Map<Int, String>): ColorPerceptionReport {
        var normalCount = 0
        var protanCount = 0
        var deutanCount = 0
        var demoPassed = false

        val evaluations = plates.map { plate ->
            val input = (userAnswers[plate.plateNumber] ?: "").trim()
            val isNormal = input.equals(plate.normalNumber, ignoreCase = true)
            val isProtan = plate.protanNumber != null && input.equals(plate.protanNumber, ignoreCase = true)
            val isDeutan = plate.deutanNumber != null && input.equals(plate.deutanNumber, ignoreCase = true)

            if (plate.category == PlateCategory.DEMONSTRATION) {
                demoPassed = isNormal
            }

            if (isNormal) {
                normalCount++
            } else {
                if (isProtan) protanCount++
                if (isDeutan) deutanCount++
            }

            val interpretation = when {
                isNormal -> "Normal perception (read ${plate.normalNumber})"
                isProtan && isDeutan -> "Red-green deficiency pattern (read $input)"
                isProtan -> "Protan (Red-weak) pattern (read $input)"
                isDeutan -> "Deutan (Green-weak) pattern (read $input)"
                input.equals("Nothing", ignoreCase = true) || input.isEmpty() -> "Digit vanished / unperceived"
                else -> "Atypical response (entered $input, expected ${plate.normalNumber})"
            }

            PlateEvaluation(
                plateNumber = plate.plateNumber,
                userInput = if (input.isEmpty()) "Nothing" else input,
                normalExpected = plate.normalNumber,
                deficientExpected = when {
                    plate.protanNumber == plate.deutanNumber && plate.protanNumber != null -> plate.protanNumber
                    plate.protanNumber != null && plate.deutanNumber != null -> "Protan: ${plate.protanNumber} / Deutan: ${plate.deutanNumber}"
                    else -> "Nothing"
                },
                category = plate.category,
                isNormalMatch = isNormal,
                isProtanMatch = isProtan,
                isDeutanMatch = isDeutan,
                interpretation = interpretation
            )
        }

        val total = plates.size

        return when {
            !demoPassed && normalCount < 3 -> {
                ColorPerceptionReport(
                    title = "Inconclusive / Screening Error",
                    severity = "Indeterminate",
                    confidencePercent = 30,
                    summary = "The demonstration plate (Plate 1) was missed or entered incorrectly. This indicates poor ambient lighting, severe glare, screen distortion, or misunderstanding of instructions.",
                    normalScore = normalCount,
                    totalPlates = total,
                    protanIndex = protanCount,
                    deutanIndex = deutanCount,
                    demonstrationPassed = false,
                    detailedEvaluations = evaluations,
                    clinicalRecommendations = "Retest under neutral ambient daylight with device brightness at ~80% and all blue light/Night Mode filters disabled."
                )
            }
            normalCount >= 10 -> {
                ColorPerceptionReport(
                    title = "Normal Trichromatic Color Vision",
                    severity = "None (Normal)",
                    confidencePercent = 98,
                    summary = "Your color perception aligns with standard trichromatic human vision. You successfully discriminated all red-green pseudoisochromatic hues across demonstration, transformation, vanishing, and classification plates.",
                    normalScore = normalCount,
                    totalPlates = total,
                    protanIndex = protanCount,
                    deutanIndex = deutanCount,
                    demonstrationPassed = demoPassed,
                    detailedEvaluations = evaluations,
                    clinicalRecommendations = "No color vision deficiency detected. Regular comprehensive eye exams are recommended every 2 years for general eye health."
                )
            }
            deutanCount > protanCount && deutanCount >= 2 -> {
                ColorPerceptionReport(
                    title = "Deuteranopia / Deuteranomaly Detected",
                    severity = if (normalCount <= 4) "Strong / Moderate Green Deficiency" else "Mild Green Deficiency (Deuteranomaly)",
                    confidencePercent = 94,
                    summary = "Responses consistently matched green-cone (M-cone / chlorolabe) deficiency patterns on classification plates #11 and #12. Green wavelengths are confused with red, beige, and yellow.",
                    normalScore = normalCount,
                    totalPlates = total,
                    protanIndex = protanCount,
                    deutanIndex = deutanCount,
                    demonstrationPassed = demoPassed,
                    detailedEvaluations = evaluations,
                    clinicalRecommendations = "Consult an optometrist for physical Ishihara 38-plate and Farnsworth D-15 / anomaloscope testing to determine exact cone sensitivity threshold."
                )
            }
            protanCount > deutanCount && protanCount >= 2 -> {
                ColorPerceptionReport(
                    title = "Protanopia / Protanomaly Detected",
                    severity = if (normalCount <= 4) "Strong / Moderate Red Deficiency" else "Mild Red Deficiency (Protanomaly)",
                    confidencePercent = 94,
                    summary = "Responses consistently matched red-cone (L-cone / erythrolabe) deficiency patterns on classification plates #11 and #12. Red wavelengths appear significantly darker and are confused with gray, brown, and green.",
                    normalScore = normalCount,
                    totalPlates = total,
                    protanIndex = protanCount,
                    deutanIndex = deutanCount,
                    demonstrationPassed = demoPassed,
                    detailedEvaluations = evaluations,
                    clinicalRecommendations = "Be cautious with red signal lights in low visibility. A professional anomaloscope evaluation is advised for occupational or aviation certification."
                )
            }
            normalCount <= 5 -> {
                ColorPerceptionReport(
                    title = "Red-Green Color Vision Deficiency",
                    severity = "Moderate to Marked Red-Green Deficiency",
                    confidencePercent = 90,
                    summary = "Significant difficulty resolving red-green pseudoisochromatic optotypes across multiple transformation and vanishing plates.",
                    normalScore = normalCount,
                    totalPlates = total,
                    protanIndex = protanCount,
                    deutanIndex = deutanCount,
                    demonstrationPassed = demoPassed,
                    detailedEvaluations = evaluations,
                    clinicalRecommendations = "Follow up with a formal in-person optometric examination with an anomaloscope and Farnsworth D-15 test."
                )
            }
            else -> {
                ColorPerceptionReport(
                    title = "Mild Color Discrimination Variation",
                    severity = "Mild / Borderline",
                    confidencePercent = 78,
                    summary = "You correctly resolved $normalCount of $total plates. A few subtle plates were missed, which can happen with mild anomalous trichromacy, ambient screen reflections, or slight display color tinting.",
                    normalScore = normalCount,
                    totalPlates = total,
                    protanIndex = protanCount,
                    deutanIndex = deutanCount,
                    demonstrationPassed = demoPassed,
                    detailedEvaluations = evaluations,
                    clinicalRecommendations = "Repeat the test in a room with diffused natural lighting and maximum display contrast."
                )
            }
        }
    }

    // Keep backwards-compatible method for tests
    fun evaluateResult(answers: Map<Int, String>): Pair<String, String> {
        val report = verifyColorPerception(answers)
        return Pair(report.title, report.summary)
    }
}
