package com.example

import com.example.data.model.ColorBlindnessData
import com.example.data.model.PlateCategory
import com.example.data.model.SnellenChartData
import com.example.ui.viewmodel.EyeTested
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClinicalOphthalmologyTest {

    @Test
    fun testComplete11LineSnellenChartStructure() {
        val lines = SnellenChartData.lines
        assertEquals(11, lines.size)

        // Line 1: 20/200
        assertEquals(1, lines[0].lineNumber)
        assertEquals("20/200", lines[0].imperialAcuity)
        assertEquals("6/60", lines[0].metricAcuity)
        assertEquals(1.0, lines[0].logMar, 0.01)
        assertEquals(listOf("E"), lines[0].letters)

        // Line 5: 20/40 (Driving standard minimum in many jurisdictions)
        assertEquals(5, lines[4].lineNumber)
        assertEquals("20/40", lines[4].imperialAcuity)
        assertEquals("6/12", lines[4].metricAcuity)

        // Line 6: 20/30 (Green Bar dividing line)
        assertEquals(6, lines[5].lineNumber)
        assertEquals("20/30", lines[5].imperialAcuity)
        assertTrue(lines[5].hasGreenBar)
        assertFalse(lines[5].hasRedBar)

        // Line 8: 20/20 (Gold standard normal vision with Red Bar)
        assertEquals(8, lines[7].lineNumber)
        assertEquals("20/20", lines[7].imperialAcuity)
        assertEquals("6/6", lines[7].metricAcuity)
        assertEquals(0.0, lines[7].logMar, 0.01)
        assertTrue(lines[7].hasRedBar)
        assertFalse(lines[7].hasGreenBar)

        // Line 11: 20/10 (Super-normal acuity)
        assertEquals(11, lines[10].lineNumber)
        assertEquals("20/10", lines[10].imperialAcuity)
        assertEquals("6/3.0", lines[10].metricAcuity)
        assertTrue(lines[10].logMar < 0.0)
    }

    @Test
    fun testIshiharaPlatesIntegrity() {
        val plates = ColorBlindnessData.plates
        assertEquals(12, plates.size)

        // Plate 1: Demonstration plate (all viewers should see 12)
        val demoPlate = plates[0]
        assertEquals(1, demoPlate.plateNumber)
        assertEquals("12", demoPlate.normalNumber)
        assertEquals("12", demoPlate.protanNumber)
        assertEquals("12", demoPlate.deutanNumber)
        assertEquals(PlateCategory.DEMONSTRATION, demoPlate.category)

        // Transformation plate (e.g. Plate 2: 8 vs 3)
        val transformPlate = plates[1]
        assertEquals("8", transformPlate.normalNumber)
        assertEquals("3", transformPlate.protanNumber)
        assertEquals("3", transformPlate.deutanNumber)
        assertEquals(PlateCategory.TRANSFORMATION, transformPlate.category)

        // Diagnostic plate (e.g. Plate 11: 26 vs 6/2)
        val diagPlate = plates.first { it.plateNumber == 11 }
        assertEquals("26", diagPlate.normalNumber)
        assertEquals("6", diagPlate.protanNumber)
        assertEquals("2", diagPlate.deutanNumber)
        assertEquals(PlateCategory.DIAGNOSTIC, diagPlate.category)
    }

    @Test
    fun testIshiharaTrichromaticEvaluation() {
        val allNormalAnswers = ColorBlindnessData.plates.associate { it.plateNumber to it.normalNumber }
        val report = ColorBlindnessData.verifyColorPerception(allNormalAnswers)

        assertEquals("Normal Trichromatic Color Vision", report.title)
        assertTrue(report.demonstrationPassed)
        assertEquals(ColorBlindnessData.plates.size, report.normalScore)
        assertEquals(0, report.protanIndex)
        assertEquals(0, report.deutanIndex)
        assertTrue(report.clinicalRecommendations.contains("Normal") || report.clinicalRecommendations.isNotEmpty())
    }

    @Test
    fun testIshiharaProtanEvaluation() {
        val protanAnswers = ColorBlindnessData.plates.associate { plate ->
            plate.plateNumber to (plate.protanNumber ?: "Nothing")
        }
        val report = ColorBlindnessData.verifyColorPerception(protanAnswers)

        assertTrue(report.title.contains("Protanopia") || report.title.contains("Red-Green"))
        assertTrue(report.protanIndex > report.deutanIndex || report.normalScore < report.totalPlates)
    }

    @Test
    fun testIshiharaDeutanEvaluation() {
        val deutanAnswers = ColorBlindnessData.plates.associate { plate ->
            plate.plateNumber to (plate.deutanNumber ?: "Nothing")
        }
        val report = ColorBlindnessData.verifyColorPerception(deutanAnswers)

        assertTrue(report.title.contains("Deuteranopia") || report.title.contains("Red-Green"))
        assertTrue(report.normalScore < report.totalPlates)
    }

    @Test
    fun testEyeTestedTerminology() {
        assertEquals("Right Eye (OD)", EyeTested.RIGHT_EYE.label)
        assertEquals("OD", EyeTested.RIGHT_EYE.abbrev)

        assertEquals("Left Eye (OS)", EyeTested.LEFT_EYE.label)
        assertEquals("OS", EyeTested.LEFT_EYE.abbrev)

        assertEquals("Both Eyes (OU)", EyeTested.BOTH_EYES.label)
        assertEquals("OU", EyeTested.BOTH_EYES.abbrev)
    }
}
