package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ColorBlindnessData
import com.example.data.model.ColorPerceptionReport
import com.example.data.model.IshiharaPlateDetail
import com.example.data.model.PlateCategory
import com.example.ui.components.IshiharaInputPad
import com.example.ui.components.IshiharaPlateCanvas
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.VisionFilter
import com.example.ui.viewmodel.EyeTestViewModel
import com.example.ui.viewmodel.InputMode

enum class ColorScreenMode {
    TESTING,
    PERCEPTION_LAB
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorVisionTestScreen(
    viewModel: EyeTestViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.ishiharaState.collectAsState()
    var isStarted by remember { mutableStateOf(false) }
    var activeMode by remember { mutableStateOf(ColorScreenMode.TESTING) }
    val context = LocalContext.current

    BackHandler {
        if (activeMode == ColorScreenMode.PERCEPTION_LAB) {
            activeMode = ColorScreenMode.TESTING
        } else {
            viewModel.resetIshiharaTest()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (activeMode == ColorScreenMode.TESTING) "Ishihara Color Test" else "Color Perception Lab") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.resetIshiharaTest()
                            onBack()
                        },
                        modifier = Modifier.testTag("color_test_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            activeMode = if (activeMode == ColorScreenMode.TESTING) ColorScreenMode.PERCEPTION_LAB else ColorScreenMode.TESTING
                        },
                        modifier = Modifier.testTag("toggle_lab_mode_btn")
                    ) {
                        Icon(
                            imageVector = if (activeMode == ColorScreenMode.TESTING) Icons.Default.Science else Icons.Default.ColorLens,
                            contentDescription = "Toggle Mode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("color_vision_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (activeMode == ColorScreenMode.PERCEPTION_LAB) {
                // Interactive Ishihara Lab & Simulation Explorer
                IshiharaPerceptionLabView(
                    onExitToTest = { activeMode = ColorScreenMode.TESTING }
                )
            } else if (!isStarted) {
                // Intro Screen
                ColorVisionIntroView(
                    onStart = {
                        isStarted = true
                        viewModel.startIshiharaTest()
                    },
                    onOpenLab = {
                        activeMode = ColorScreenMode.PERCEPTION_LAB
                    }
                )
            } else if (state.isFinished && state.report != null) {
                // Detailed Clinical Perception Report
                ColorPerceptionReportView(
                    report = state.report!!,
                    onRetest = {
                        viewModel.startIshiharaTest()
                    },
                    onOpenLab = {
                        activeMode = ColorScreenMode.PERCEPTION_LAB
                    },
                    onDone = {
                        viewModel.resetIshiharaTest()
                        onBack()
                    }
                )
            } else {
                // Active Plate Testing & Perception Input
                val plate = ColorBlindnessData.plates[state.currentPlateIndex]
                ColorVisionActiveTestingView(
                    plate = plate,
                    currentIndex = state.currentPlateIndex,
                    totalPlates = ColorBlindnessData.plates.size,
                    inputMode = state.inputMode,
                    typedValue = state.typedBuffer,
                    activeFilter = state.activeVisionFilter,
                    onInputModeChange = { viewModel.setIshiharaInputMode(it) },
                    onVisionFilterChange = { viewModel.setIshiharaVisionFilter(it) },
                    onTypedChange = { viewModel.updateTypedBuffer(it) },
                    onSubmitTyped = { viewModel.submitCurrentTypedBuffer() },
                    onSelectMultipleChoice = { ans ->
                        viewModel.submitIshiharaAnswer(plate.plateNumber, ans)
                    },
                    onReportNothing = {
                        viewModel.submitIshiharaAnswer(plate.plateNumber, "Nothing")
                    }
                )
            }
        }
    }
}

@Composable
fun ColorVisionIntroView(
    onStart: () -> Unit,
    onOpenLab: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ishihara Color Blindness Test",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "12 pseudoisochromatic test plates including Demonstration, Transformation, Vanishing, and Diagnostic/Classification plates to verify red-green cone sensitivity.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Test types included
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "What This Module Verifies:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Normal Trichromacy: Intact L (red), M (green), and S (blue) cone photopigments.\n" +
                               "• Protanopia / Protanomaly: Red cone deficiency (erythrolabe). Reds appear dark brown or khaki.\n" +
                               "• Deuteranopia / Deuteranomaly: Green cone deficiency (chlorolabe). The most common type (up to 8% of males).\n" +
                               "• Vanishing Plates: Digits visible only to normal trichromats.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Testing Protocol Tips
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical Protocol for Best Accuracy:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Disable Night Mode, blue light filters, or True Tone.\n" +
                               "2. Set screen brightness to ~75-80%.\n" +
                               "3. Hold phone ~50-75 cm from eyes in neutral lighting.\n" +
                               "4. Input the number within 3-5 seconds without overthinking.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            MedicalDisclaimerBanner()
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_color_test_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.ColorLens, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Begin Color Perception Verification", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenLab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_lab_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Science, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Explore Perception Lab & Filters")
                }
            }
        }
    }
}

@Composable
fun ColorVisionActiveTestingView(
    plate: IshiharaPlateDetail,
    currentIndex: Int,
    totalPlates: Int,
    inputMode: InputMode,
    typedValue: String,
    activeFilter: VisionFilter,
    onInputModeChange: (InputMode) -> Unit,
    onVisionFilterChange: (VisionFilter) -> Unit,
    onTypedChange: (String) -> Unit,
    onSubmitTyped: () -> Unit,
    onSelectMultipleChoice: (String) -> Unit,
    onReportNothing: () -> Unit
) {
    var showFilterMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top status row with progress and mode controls
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Plate ${plate.plateNumber} of $totalPlates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plate.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Vision Filter Indicator / Switcher
                    FilterChip(
                        selected = activeFilter != VisionFilter.NORMAL,
                        onClick = { showFilterMenu = !showFilterMenu },
                        label = { Text(activeFilter.displayName, fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )

                    // Input Mode Toggle
                    IconButton(
                        onClick = {
                            onInputModeChange(if (inputMode == InputMode.KEYPAD) InputMode.MULTIPLE_CHOICE else InputMode.KEYPAD)
                        }
                    ) {
                        Icon(
                            imageVector = if (inputMode == InputMode.KEYPAD) Icons.Default.ListAlt else Icons.Default.Dialpad,
                            contentDescription = "Toggle Input Method"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalPlates },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            // Optional Vision Filter Selector Row
            if (showFilterMenu) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(VisionFilter.values()) { filter ->
                        FilterChip(
                            selected = activeFilter == filter,
                            onClick = {
                                onVisionFilterChange(filter)
                                showFilterMenu = false
                            },
                            label = { Text(filter.displayName, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        // Ishihara Plate Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            IshiharaPlateCanvas(
                number = plate.normalNumber,
                visionFilter = activeFilter,
                modifier = Modifier.size(260.dp)
            )
        }

        // User Input Collection Section
        if (inputMode == InputMode.KEYPAD) {
            IshiharaInputPad(
                currentValue = typedValue,
                onValueChange = onTypedChange,
                onSubmit = onSubmitTyped,
                onReportNothing = onReportNothing
            )
        } else {
            // Multiple Choice Mode
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select what you see in the dots:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    plate.options.forEach { opt ->
                        Button(
                            onClick = { onSelectMultipleChoice(opt) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("choice_$opt"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = opt, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPerceptionReportView(
    report: ColorPerceptionReport,
    onRetest: () -> Unit,
    onOpenLab: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            if (report.title.contains("Normal")) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (report.title.contains("Normal")) Icons.Default.Check else Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = if (report.title.contains("Normal")) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Color Perception Verification",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = report.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Classification: ${report.severity} • Confidence: ${report.confidencePercent}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = report.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Cone Photoreceptor Status Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConeStatusPill(
                            name = "L-Cone (Red)",
                            isNormal = report.protanIndex <= 1,
                            modifier = Modifier.weight(1f)
                        )
                        ConeStatusPill(
                            name = "M-Cone (Green)",
                            isNormal = report.deutanIndex <= 1,
                            modifier = Modifier.weight(1f)
                        )
                        ConeStatusPill(
                            name = "S-Cone (Blue)",
                            isNormal = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Plate-by-Plate Verification Audit Log
        item {
            Text(
                text = "Plate-by-Plate Perception Audit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(report.detailedEvaluations) { eval ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (eval.isNormalMatch) Color(0xFFF1F8E9) else Color(0xFFFFF8E1)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Plate #${eval.plateNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = eval.category.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = eval.interpretation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "You: ${eval.userInput}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (eval.isNormalMatch) Color(0xFF2E7D32) else Color(0xFFD84315)
                        )
                        Text(
                            text = "Normal: ${eval.normalExpected}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Clinical Recommendations
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical Recommendations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = report.clinicalRecommendations,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            MedicalDisclaimerBanner()
        }

        // Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val shareText = buildString {
                            appendLine("=== Ishihara Color Perception Verification Report ===")
                            appendLine("Result: ${report.title}")
                            appendLine("Classification: ${report.severity} (Confidence: ${report.confidencePercent}%)")
                            appendLine("Score: ${report.normalScore} of ${report.totalPlates} plates identified")
                            appendLine()
                            appendLine("Plate Breakdown:")
                            report.detailedEvaluations.forEach { ev ->
                                appendLine("Plate ${ev.plateNumber} (${ev.category.name}): Input='${ev.userInput}' Expected='${ev.normalExpected}' -> ${ev.interpretation}")
                            }
                            appendLine()
                            appendLine("Recommendations: ${report.clinicalRecommendations}")
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Color Perception Report"))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share / Export Perception Report")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenLab,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Science, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Perception Lab")
                    }

                    OutlinedButton(
                        onClick = onRetest,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retest")
                    }
                }

                FilledTonalButton(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun ConeStatusPill(
    name: String,
    isNormal: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isNormal) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isNormal) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
            Text(
                text = if (isNormal) "Intact" else "Deficient",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isNormal) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}

/**
 * Interactive Ishihara Perception Lab:
 * Browse any plate, toggle deficiency simulation filters, and understand psychophysics mechanisms.
 */
@Composable
fun IshiharaPerceptionLabView(
    onExitToTest: () -> Unit
) {
    var selectedPlateIndex by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf(VisionFilter.NORMAL) }
    val plate = ColorBlindnessData.plates[selectedPlateIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ishihara Perception Lab",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Examine how optotypes transform under red/green/blue cone loss using mathematical color space filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Plate Selector Carousel
        item {
            Column {
                Text(
                    text = "Select Plate (1 to ${ColorBlindnessData.plates.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ColorBlindnessData.plates.size) { idx ->
                        val p = ColorBlindnessData.plates[idx]
                        FilterChip(
                            selected = selectedPlateIndex == idx,
                            onClick = { selectedPlateIndex = idx },
                            label = { Text("Plate ${p.plateNumber}") }
                        )
                    }
                }
            }
        }

        // Vision Filter Selector
        item {
            Column {
                Text(
                    text = "Simulated Observer Perception",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(VisionFilter.values()) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.displayName) }
                        )
                    }
                }
            }
        }

        // Render Plate
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IshiharaPlateCanvas(
                        number = plate.normalNumber,
                        visionFilter = selectedFilter,
                        modifier = Modifier.size(260.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Viewing under: ${selectedFilter.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Clinical Explanation
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Plate #${plate.plateNumber} Analysis (${plate.category.label})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Normal Vision Sees: '${plate.normalNumber}'\n" +
                               "• Protanopia Sees: '${plate.protanNumber ?: "Nothing / Vanished"}'\n" +
                               "• Deuteranopia Sees: '${plate.deutanNumber ?: "Nothing / Vanished"}'\n\n" +
                               plate.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            Button(
                onClick = onExitToTest,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Return to Color Vision Test")
            }
        }
    }
}
