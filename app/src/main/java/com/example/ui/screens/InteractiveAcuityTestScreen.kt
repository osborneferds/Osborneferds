package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SnellenChartData
import com.example.ui.components.EDirection
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.TumblingEOptotype
import com.example.ui.viewmodel.AcuityTestState
import com.example.ui.viewmodel.AcuityTestType
import com.example.ui.viewmodel.EyeTestViewModel
import com.example.ui.viewmodel.EyeTested

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveAcuityTestScreen(
    viewModel: EyeTestViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.acuityState.collectAsState()
    var selectedEye by remember { mutableStateOf(EyeTested.RIGHT_EYE) }
    var selectedType by remember { mutableStateOf(AcuityTestType.SNELLEN_LETTERS) }

    BackHandler {
        if (state.isActive && !state.isFinished) {
            viewModel.resetAcuityTest()
        }
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isActive) "Acuity Test (${state.eyeTested.abbrev})" else "Visual Acuity Test") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.isActive && !state.isFinished) {
                                viewModel.resetAcuityTest()
                            }
                            onBack()
                        },
                        modifier = Modifier.testTag("acuity_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("interactive_acuity_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!state.isActive) {
                // Setup & Instructions Screen
                AcuitySetupView(
                    selectedEye = selectedEye,
                    onSelectEye = { selectedEye = it },
                    selectedType = selectedType,
                    onSelectType = { selectedType = it },
                    onStart = {
                        viewModel.startAcuityTest(selectedType, selectedEye)
                    }
                )
            } else if (state.isFinished) {
                // Results Screen
                AcuityResultsView(
                    state = state,
                    onRetestSame = {
                        viewModel.startAcuityTest(state.testType, state.eyeTested)
                    },
                    onTestOtherEye = {
                        val nextEye = when (state.eyeTested) {
                            EyeTested.RIGHT_EYE -> EyeTested.LEFT_EYE
                            EyeTested.LEFT_EYE -> EyeTested.BOTH_EYES
                            EyeTested.BOTH_EYES -> EyeTested.RIGHT_EYE
                        }
                        viewModel.startAcuityTest(state.testType, nextEye)
                    },
                    onDone = {
                        viewModel.resetAcuityTest()
                        onBack()
                    }
                )
            } else {
                // Active Test Step
                AcuityActiveTestingView(
                    state = state,
                    onSubmitAnswer = { answer ->
                        viewModel.submitAcuityAnswer(answer)
                    }
                )
            }
        }
    }
}

@Composable
fun AcuitySetupView(
    selectedEye: EyeTested,
    onSelectEye: (EyeTested) -> Unit,
    selectedType: AcuityTestType,
    onSelectType: (AcuityTestType) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Visual Acuity Screening",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Measures the sharpness of your central vision at varying distances by stepping down through optotype rows.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }

            // Eye Selection
            Text(
                text = "1. Select Eye to Test",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EyeTested.values().forEach { eye ->
                    FilterChip(
                        selected = selectedEye == eye,
                        onClick = { onSelectEye(eye) },
                        label = { Text(eye.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Optotype Type
            Text(
                text = "2. Select Optotype Mode",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == AcuityTestType.SNELLEN_LETTERS,
                    onClick = { onSelectType(AcuityTestType.SNELLEN_LETTERS) },
                    label = { Text("Snellen Letters") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedType == AcuityTestType.TUMBLING_E,
                    onClick = { onSelectType(AcuityTestType.TUMBLING_E) },
                    label = { Text("Tumbling E") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Testing Instructions
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Instructions for Best Accuracy:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Hold your phone steadily at arm's length (~40 cm / 16 inches).\n" +
                               "• If testing Right Eye, gently cover your Left Eye with your palm (do not press on eye).\n" +
                               "• Wear your habitual glasses or contacts if you want to test corrected vision.\n" +
                               "• Ensure good ambient lighting without screen reflections.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }

            MedicalDisclaimerBanner()
        }

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("start_acuity_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Begin Test", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AcuityActiveTestingView(
    state: AcuityTestState,
    onSubmitAnswer: (String) -> Unit
) {
    val currentLine = SnellenChartData.lines[state.currentLineIndex]

    // Scale font/canvas size relative to 20/20 baseline
    val baseDp = 40.dp
    val dynamicSize = (baseDp * (currentLine.relativeScale.coerceIn(0.5f, 5.0f))).coerceIn(24.dp, 160.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top info bar
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.eyeTested.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Acuity: ${currentLine.imperialAcuity} (${currentLine.metricAcuity})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (state.currentLineIndex + 1).toFloat() / SnellenChartData.lines.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Line ${state.currentLineIndex + 1} of 11 • Trial ${state.attemptsOnLine + 1}/3",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Optotype display stage
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.testType == AcuityTestType.SNELLEN_LETTERS) {
                    Text(
                        text = state.currentLetter,
                        color = Color.Black,
                        fontSize = (dynamicSize.value * 1.1f).sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )
                } else {
                    TumblingEOptotype(
                        direction = state.currentEDirection,
                        size = dynamicSize,
                        color = Color.Black
                    )
                }
            }
        }

        // Response buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (state.testType == AcuityTestType.SNELLEN_LETTERS) "What letter do you see?" else "Which direction is the E facing?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            if (state.testType == AcuityTestType.SNELLEN_LETTERS) {
                // 4 Multiple choice letters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (opt in state.options) {
                        Button(
                            onClick = { onSubmitAnswer(opt) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("acuity_choice_$opt"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = opt, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // 4 Directions for Tumbling E: UP, DOWN, LEFT, RIGHT
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSubmitAnswer("Up") },
                        modifier = Modifier
                            .size(60.dp)
                            .testTag("direction_up"),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Up")
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onSubmitAnswer("Left") },
                            modifier = Modifier
                                .size(60.dp)
                                .testTag("direction_left"),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left")
                        }
                        Button(
                            onClick = { onSubmitAnswer("Right") },
                            modifier = Modifier
                                .size(60.dp)
                                .testTag("direction_right"),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right")
                        }
                    }

                    Button(
                        onClick = { onSubmitAnswer("Down") },
                        modifier = Modifier
                            .size(60.dp)
                            .testTag("direction_down"),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Down")
                    }
                }
            }
        }
    }
}

@Composable
fun AcuityResultsView(
    state: AcuityTestState,
    onRetestSame: () -> Unit,
    onTestOtherEye: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "Screening Complete!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.eyeTested.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.finalAcuity,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Metric: ${state.finalMetric}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = state.finalAssessment,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            MedicalDisclaimerBanner()
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilledTonalButton(
                onClick = onTestOtherEye,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Test Next / Other Eye")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRetestSame,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retest")
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("acuity_done_btn")
                ) {
                    Text("Done")
                }
            }
        }
    }
}
