package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.screens.EducationCard
import com.example.ui.screens.EducationScreen
import com.example.ui.screens.SplashScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ComposeUiRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSplashScreenElementsAndDeveloperAttribution() {
        composeTestRule.setContent {
            MaterialTheme {
                SplashScreen(onSplashFinished = {})
            }
        }

        composeTestRule.onNodeWithTag("splash_screen").assertExists()
        composeTestRule.onNodeWithText("OCULUS VISION LABS").assertExists()
        composeTestRule.onNodeWithText("Eye Test").assertExists()
        composeTestRule.onNodeWithText("Developed by Osborneferds").assertExists()
    }

    @Test
    fun testMedicalDisclaimerBannerContent() {
        composeTestRule.setContent {
            MaterialTheme {
                MedicalDisclaimerBanner()
            }
        }

        composeTestRule.onNodeWithTag("medical_disclaimer_banner").assertExists()
        composeTestRule.onNodeWithText("Screening tool only", substring = true).assertExists()
    }

    @Test
    fun testEducationCard() {
        composeTestRule.setContent {
            MaterialTheme {
                EducationCard(
                    title = "Visual Acuity Explained",
                    body = "20/20 represents standard visual resolution."
                )
            }
        }

        composeTestRule.onNodeWithText("Visual Acuity Explained").assertExists()
        composeTestRule.onNodeWithText("20/20 represents standard visual resolution.").assertExists()
    }

    @Test
    fun testEducationScreenRootPresence() {
        composeTestRule.setContent {
            MaterialTheme {
                EducationScreen()
            }
        }

        composeTestRule.onNodeWithTag("education_screen").assertExists()
    }
}
