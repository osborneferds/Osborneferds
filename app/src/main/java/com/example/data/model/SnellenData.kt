package com.example.data.model

data class SnellenLine(
    val lineNumber: Int,
    val letters: List<String>,
    val imperialAcuity: String, // e.g. "20/200"
    val metricAcuity: String,   // e.g. "6/60"
    val logMar: Double,         // e.g. 1.0
    val decimalAcuity: Double,  // e.g. 0.1
    val hasGreenBar: Boolean = false,
    val hasRedBar: Boolean = false,
    val relativeScale: Float    // scale relative to 20/20 (20/200 is 10x, 20/20 is 1x)
)

object SnellenChartData {
    val lines: List<SnellenLine> = listOf(
        SnellenLine(1, listOf("E"), "20/200", "6/60", 1.00, 0.10, relativeScale = 10.0f),
        SnellenLine(2, listOf("F", "P"), "20/100", "6/30", 0.70, 0.20, relativeScale = 5.0f),
        SnellenLine(3, listOf("T", "O", "Z"), "20/70", "6/21", 0.54, 0.29, relativeScale = 3.5f),
        SnellenLine(4, listOf("L", "P", "E", "D"), "20/50", "6/15", 0.40, 0.40, relativeScale = 2.5f),
        SnellenLine(5, listOf("P", "E", "C", "F", "D"), "20/40", "6/12", 0.30, 0.50, relativeScale = 2.0f),
        SnellenLine(6, listOf("E", "D", "F", "C", "Z", "P"), "20/30", "6/9", 0.18, 0.67, hasGreenBar = true, relativeScale = 1.5f),
        SnellenLine(7, listOf("F", "E", "L", "O", "P", "Z", "D"), "20/25", "6/7.5", 0.10, 0.80, relativeScale = 1.25f),
        SnellenLine(8, listOf("D", "E", "F", "P", "O", "T", "E", "C"), "20/20", "6/6", 0.00, 1.00, hasRedBar = true, relativeScale = 1.0f),
        SnellenLine(9, listOf("L", "E", "F", "O", "D", "P", "C", "T"), "20/15", "6/4.5", -0.12, 1.33, relativeScale = 0.75f),
        SnellenLine(10, listOf("F", "D", "P", "L", "T", "C", "E", "O"), "20/12", "6/3.6", -0.22, 1.67, relativeScale = 0.6f),
        SnellenLine(11, listOf("P", "E", "Z", "O", "L", "C", "F", "T", "D"), "20/10", "6/3.0", -0.30, 2.00, relativeScale = 0.5f)
    )

    fun getAssessmentForAcuity(acuity: String): String = when (acuity) {
        "20/10", "20/12", "20/15" -> "Above Average Visual Acuity (Super-normal vision). Excellent detail resolution."
        "20/20" -> "Normal Standard Visual Acuity. You can see at 20 feet what a person with normal vision sees at 20 feet."
        "20/25" -> "Near-Normal Acuity. Very functional vision, typically meets driver license requirements without restriction."
        "20/30" -> "Slight Acuity Reduction. Still adequate for everyday tasks, but may benefit from refraction evaluation."
        "20/40" -> "Mild Visual Reduction. Minimum requirement for unrestricted driving in most jurisdictions. Glasses/contacts may be beneficial."
        "20/50", "20/70" -> "Moderate Visual Reduction. Noticeable difficulty with fine print and road signs without corrective lenses."
        "20/100", "20/200" -> "Significant Visual Reduction. High likelihood of uncorrected myopia, hyperopia, or astigmatism. Comprehensive exam strongly advised."
        else -> "Visual screening completed. Consult an optometrist or ophthalmologist for professional refraction."
    }
}
