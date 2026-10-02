package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import com.example.ui.screens.AstigmatismTestScreen
import com.example.ui.screens.ColorVisionTestScreen
import com.example.ui.screens.DuochromeTestScreen
import com.example.ui.screens.EducationScreen
import com.example.ui.screens.EyeStrainTimerScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InteractiveAcuityTestScreen
import com.example.ui.screens.NearVisionTestScreen
import com.example.ui.screens.SnellenChartScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EyeTestViewModel

enum class MainTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Tests", Icons.Filled.RemoveRedEye, Icons.Outlined.RemoveRedEye, "nav_tests"),
    CHART("Chart", Icons.Filled.Visibility, Icons.Outlined.Visibility, "nav_chart"),
    CARE("Eye Care", Icons.Filled.Spa, Icons.Outlined.Spa, "nav_care"),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History, "nav_history"),
    GUIDE("Guide", Icons.Filled.HelpOutline, Icons.Outlined.HelpOutline, "nav_guide")
}

enum class ActiveScreen {
    SPLASH,
    MAIN_TABS,
    ACUITY_TEST,
    COLOR_TEST,
    ASTIGMATISM_TEST,
    DUOCHROME_TEST,
    NEAR_VISION_TEST
}

class MainActivity : ComponentActivity() {
    private val viewModel: EyeTestViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentTab by remember { mutableStateOf(MainTab.HOME) }
                var activeScreen by remember { mutableStateOf(ActiveScreen.SPLASH) }

                // Handle system back navigation from secondary screens to main tabs
                if (activeScreen != ActiveScreen.MAIN_TABS && activeScreen != ActiveScreen.SPLASH) {
                    BackHandler {
                        activeScreen = ActiveScreen.MAIN_TABS
                    }
                }

                AnimatedContent(
                    targetState = activeScreen,
                    transitionSpec = {
                        fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                    },
                    label = "screenTransition"
                ) { screen ->
                    when (screen) {
                        ActiveScreen.SPLASH -> {
                            SplashScreen(
                                onSplashFinished = { activeScreen = ActiveScreen.MAIN_TABS }
                            )
                        }
                        ActiveScreen.ACUITY_TEST -> {
                            InteractiveAcuityTestScreen(
                                viewModel = viewModel,
                                onBack = { activeScreen = ActiveScreen.MAIN_TABS }
                            )
                        }
                        ActiveScreen.COLOR_TEST -> {
                            ColorVisionTestScreen(
                                viewModel = viewModel,
                                onBack = { activeScreen = ActiveScreen.MAIN_TABS }
                            )
                        }
                        ActiveScreen.ASTIGMATISM_TEST -> {
                            AstigmatismTestScreen(
                                viewModel = viewModel,
                                onBack = { activeScreen = ActiveScreen.MAIN_TABS }
                            )
                        }
                        ActiveScreen.DUOCHROME_TEST -> {
                            DuochromeTestScreen(
                                viewModel = viewModel,
                                onBack = { activeScreen = ActiveScreen.MAIN_TABS }
                            )
                        }
                        ActiveScreen.NEAR_VISION_TEST -> {
                            NearVisionTestScreen(
                                viewModel = viewModel,
                                onBack = { activeScreen = ActiveScreen.MAIN_TABS }
                            )
                        }
                        ActiveScreen.MAIN_TABS -> {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = when (currentTab) {
                                                MainTab.HOME -> "Eye Test Screening"
                                                MainTab.CHART -> "Snellen Eye Chart"
                                                MainTab.CARE -> "20-20-20 & Ocular Care"
                                                MainTab.HISTORY -> "Vision Screening Log"
                                                MainTab.GUIDE -> "Optometry Health Guide"
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors()
                                )
                            },
                            bottomBar = {
                                NavigationBar(modifier = Modifier.testTag("bottom_nav_bar")) {
                                    MainTab.values().forEach { tab ->
                                        val isSelected = currentTab == tab
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { currentTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.label
                                                )
                                            },
                                            label = { Text(tab.label) },
                                            modifier = Modifier.testTag(tab.testTag)
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            when (currentTab) {
                                MainTab.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToAcuity = { activeScreen = ActiveScreen.ACUITY_TEST },
                                    onNavigateToSnellenChart = { currentTab = MainTab.CHART },
                                    onNavigateToColor = { activeScreen = ActiveScreen.COLOR_TEST },
                                    onNavigateToAstigmatism = { activeScreen = ActiveScreen.ASTIGMATISM_TEST },
                                    onNavigateToDuochrome = { activeScreen = ActiveScreen.DUOCHROME_TEST },
                                    onNavigateToNearVision = { activeScreen = ActiveScreen.NEAR_VISION_TEST },
                                    onNavigateToTimer = { currentTab = MainTab.CARE },
                                    modifier = Modifier.padding(innerPadding)
                                )
                                MainTab.CHART -> SnellenChartScreen(
                                    onStartTestClicked = { activeScreen = ActiveScreen.ACUITY_TEST },
                                    modifier = Modifier.padding(innerPadding)
                                )
                                MainTab.CARE -> EyeStrainTimerScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                                MainTab.HISTORY -> HistoryScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                                MainTab.GUIDE -> EducationScreen(
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}
