package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ColorBlindnessData
import com.example.data.model.SnellenChartData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Eye Test", appName)
    }

    @Test
    fun `verify snellen chart contains 11 lines with 20-20 standard at line 8`() {
        assertEquals(11, SnellenChartData.lines.size)
        val line8 = SnellenChartData.lines[7]
        assertEquals(8, line8.lineNumber)
        assertEquals("20/20", line8.imperialAcuity)
        assertEquals("6/6", line8.metricAcuity)
        assertTrue(line8.hasRedBar)
    }

    @Test
    fun `verify ishihara color blindness verification for normal trichromat`() {
        val normalAnswers = ColorBlindnessData.plates.associate { it.plateNumber to it.normalNumber }
        val report = ColorBlindnessData.verifyColorPerception(normalAnswers)
        assertEquals("Normal Trichromatic Color Vision", report.title)
        assertEquals(ColorBlindnessData.plates.size, report.normalScore)
        assertTrue(report.demonstrationPassed)
    }

    @Test
    fun `verify ishihara color blindness verification for deutan deficiency`() {
        val deutanAnswers = ColorBlindnessData.plates.associate { plate ->
            plate.plateNumber to (plate.deutanNumber ?: "Nothing")
        }
        val report = ColorBlindnessData.verifyColorPerception(deutanAnswers)
        assertTrue(report.title.contains("Deuteranopia"))
    }

    @Test
    fun `verify splash screen active state exists`() {
        val splashState = ActiveScreen.SPLASH
        assertNotNull(splashState)
        assertEquals("SPLASH", splashState.name)
    }
}
