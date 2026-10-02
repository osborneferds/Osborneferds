package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AstigmatismDialCanvas
import com.example.ui.components.AstigmatismDialType
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.viewmodel.EyeTestViewModel
import com.example.ui.viewmodel.EyeTested

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstigmatismTestScreen(
    viewModel: EyeTestViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEye by remember { mutableStateOf(EyeTested.RIGHT_EYE) }
    var selectedDialType by remember { mutableStateOf(AstigmatismDialType.CLOCK_DIAL) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedDegrees by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var hasAstigmatismSigns by remember { mutableStateOf(false) }

    // Demonstration & Simulation State
    var isSimulationActive by remember { mutableStateOf(false) }
    var simulatedAxis by remember { mutableFloatStateOf(90f) }

    BackHandler {
        if (isSimulationActive) {
            isSimulationActive = false
        } else if (isSubmitted) {
            isSubmitted = false
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Astigmatism Dial Test") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("astigmatism_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("astigmatism_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!isSubmitted) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Radar,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Radial Astigmatism Screening",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "In astigmatism, asymmetrical corneal curvature causes light rays in different meridians to focus at different depths. Lines parallel to the clearest focal plane look sharp, while perpendicular lines appear thicker, blurred, or grayed out.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // 1. Eye Selection Chips
                    item {
                        Column {
                            Text(
                                text = "1. Select Eye to Test",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EyeTested.values().forEach { eye ->
                                    FilterChip(
                                        selected = selectedEye == eye,
                                        onClick = { selectedEye = eye },
                                        label = { Text(eye.label) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Dial Pattern Selection
                    item {
                        Column {
                            Text(
                                text = "2. Select Dial Target Style",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AstigmatismDialType.values().forEach { type ->
                                    FilterChip(
                                        selected = selectedDialType == type,
                                        onClick = { selectedDialType = type },
                                        label = { Text(type.label, fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 3. The Astigmatism Testing Dial Canvas
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AstigmatismDialCanvas(
                                    dialType = selectedDialType,
                                    selectedHour = selectedHour,
                                    selectedDegrees = selectedDegrees,
                                    simulatedAstigmatismAxis = if (isSimulationActive) simulatedAxis else null,
                                    onHourSelected = { hr ->
                                        selectedHour = hr
                                        selectedDegrees = (hr * 30) % 180
                                        hasAstigmatismSigns = true
                                    },
                                    onDegreeSelected = { deg ->
                                        selectedDegrees = deg
                                        hasAstigmatismSigns = true
                                    },
                                    modifier = Modifier.size(280.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                if (selectedHour != null || selectedDegrees != null) {
                                    val deg = selectedDegrees ?: ((selectedHour!! * 30) % 180)
                                    val classification = getAstigmatismClassification(deg)

                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = if (selectedHour != null) "Selected: Hour $selectedHour (~$deg° axis)" else "Selected: ~$deg° axis",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = classification,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Tap on any darker/thicker lines on the dial",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // 4. Astigmatic Blur & Thickness Simulator (Educational Tool)
                    item {
                        OutlinedCard(
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Science,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Astigmatism Blur Visualizer",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Switch(
                                        checked = isSimulationActive,
                                        onCheckedChange = { isSimulationActive = it },
                                        modifier = Modifier.testTag("simulation_switch")
                                    )
                                }

                                AnimatedVisibility(visible = isSimulationActive) {
                                    Column(modifier = Modifier.padding(top = 10.dp)) {
                                        Text(
                                            text = "Simulated Axis: ${simulatedAxis.toInt()}° (${getAstigmatismClassification(simulatedAxis.toInt())})",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Slider(
                                            value = simulatedAxis,
                                            onValueChange = { simulatedAxis = it },
                                            valueRange = 0f..180f,
                                            steps = 11,
                                            modifier = Modifier.testTag("axis_slider")
                                        )
                                        Text(
                                            text = "Notice how lines along ${simulatedAxis.toInt()}° remain sharp and black, while lines 90° away (${(simulatedAxis.toInt() + 90) % 180}°) become blurred, thicker, and lower contrast.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. Test Observation Prompts
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Screening Checklist:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Keep your gaze steadily on the center fixation dot.\n" +
                                           "• Do NOT squint or tilt your head.\n" +
                                           "• Check if some lines look blacker, thicker, or sharper while others appear grayed or doubled.",
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    // 6. Action Submission Buttons
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    hasAstigmatismSigns = false
                                    selectedHour = null
                                    selectedDegrees = null
                                    viewModel.recordAstigmatismResult(selectedEye, null, false)
                                    isSubmitted = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("astigmatism_all_equal_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("All Lines Look Uniform (No Astigmatism)", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val hr = selectedHour ?: 12
                                    viewModel.recordAstigmatismResult(selectedEye, hr, true)
                                    isSubmitted = true
                                },
                                enabled = selectedHour != null || selectedDegrees != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("astigmatism_some_darker_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    if (selectedHour != null || selectedDegrees != null)
                                        "Confirm Selected Meridian Is Darker/Thicker"
                                    else
                                        "Tap Darker Lines On Dial First"
                                )
                            }
                        }
                    }

                    item {
                        MedicalDisclaimerBanner()
                    }
                }
            } else {
                // Results View
                AstigmatismResultsView(
                    eye = selectedEye,
                    hasAstigmatism = hasAstigmatismSigns,
                    selectedHour = selectedHour,
                    selectedDegrees = selectedDegrees,
                    onRetest = {
                        isSubmitted = false
                        selectedHour = null
                        selectedDegrees = null
                        isSimulationActive = false
                    },
                    onDone = onBack
                )
            }
        }
    }
}

private fun getAstigmatismClassification(axisDeg: Int): String {
    val norm = axisDeg % 180
    return when {
        norm in 70..110 -> "With-The-Rule (WTR) Astigmatism — vertical meridian steepest"
        norm in 0..20 || norm in 160..180 -> "Against-The-Rule (ATR) Astigmatism — horizontal meridian steepest"
        else -> "Oblique Astigmatism — diagonal meridian steepest (~${norm}°)"
    }
}

@Composable
fun AstigmatismResultsView(
    eye: EyeTested,
    hasAstigmatism: Boolean,
    selectedHour: Int?,
    selectedDegrees: Int?,
    onRetest: () -> Unit,
    onDone: () -> Unit
) {
    val axis = selectedDegrees ?: if (selectedHour != null) ((selectedHour * 30) % 180) else 90

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
                    .background(
                        if (!hasAstigmatism) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (!hasAstigmatism) Icons.Default.Check else Icons.Default.Radar,
                    contentDescription = null,
                    tint = if (!hasAstigmatism) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "Astigmatism Screening Results",
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
                        text = eye.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (!hasAstigmatism) "Normal Corneal Symmetry" else "Possible Astigmatism (~${axis}° Axis)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (hasAstigmatism) {
                        Text(
                            text = getAstigmatismClassification(axis),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Text(
                        text = if (!hasAstigmatism) {
                            "All radial spokes and clock hours appeared equally sharp, dark, and uniformly spaced. This indicates spherical corneal curvature without significant regular astigmatism."
                        } else {
                            "You observed darker or thicker lines along the $axis° meridian (Hour ${if (selectedHour != null) selectedHour else "$axis/30"}). In clinical refraction, light along the perpendicular meridian is falling out of focus on your retina. Corrective lenses with a cylinder (CYL) prescription can sharpen all meridians equally."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            MedicalDisclaimerBanner()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onRetest,
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
                    .testTag("astigmatism_done_btn")
            ) {
                Text("Done")
            }
        }
    }
}
