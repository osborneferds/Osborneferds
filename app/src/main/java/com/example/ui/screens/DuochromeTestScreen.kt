package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.DuochromeView
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.viewmodel.EyeTestViewModel
import com.example.ui.viewmodel.EyeTested

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuochromeTestScreen(
    viewModel: EyeTestViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEye by remember { mutableStateOf(EyeTested.RIGHT_EYE) }
    var selectedResult by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    BackHandler {
        if (isSubmitted) {
            isSubmitted = false
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duochrome (Red/Green) Test") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("duochrome_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("duochrome_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!isSubmitted) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Eye selection
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

                        Text(
                            text = "Focus on the letters. Do the letters on the RED background or GREEN background look clearer and more sharply defined?",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Duochrome visual test target
                        DuochromeView()

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "How it works: Red light refracts less through the eye's lens than green light. Comparing clarity reveals if your focal point sits in front of or behind the retina.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // 3 Choices: Red Sharper, Both Equal, Green Sharper
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedResult = "RED"
                                viewModel.recordDuochromeResult(selectedEye, "RED")
                                isSubmitted = true
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("duochrome_red_btn")
                        ) {
                            Text("Letters Sharper on RED", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                selectedResult = "EQUAL"
                                viewModel.recordDuochromeResult(selectedEye, "EQUAL")
                                isSubmitted = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("duochrome_equal_btn")
                        ) {
                            Text("Both Sides Equally Clear (Balanced)", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                selectedResult = "GREEN"
                                viewModel.recordDuochromeResult(selectedEye, "GREEN")
                                isSubmitted = true
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("duochrome_green_btn")
                        ) {
                            Text("Letters Sharper on GREEN", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Results Screen
                DuochromeResultsView(
                    eye = selectedEye,
                    result = selectedResult ?: "EQUAL",
                    onRetest = { isSubmitted = false },
                    onDone = onBack
                )
            }
        }
    }
}

@Composable
fun DuochromeResultsView(
    eye: EyeTested,
    result: String,
    onRetest: () -> Unit,
    onDone: () -> Unit
) {
    val (title, explanation) = when (result) {
        "RED" -> Pair(
            "Red Dominance (Myopic Bias)",
            "Letters on red appeared sharper. In clinical optometry, red light (longer wavelength) focuses behind green light. If red is sharper, your focal point sits slightly anterior to the retina, indicating potential uncorrected nearsightedness (myopia) or under-corrected glasses."
        )
        "GREEN" -> Pair(
            "Green Dominance (Hyperopic Bias)",
            "Letters on green appeared sharper. Green light (shorter wavelength) focuses in front of red light. If green is sharper, your focal point sits posterior to the retina, indicating potential farsightedness (hyperopia) or over-minus prescription."
        )
        else -> Pair(
            "Balanced Spherical Focus",
            "Letters on both red and green appeared equally sharp and clear. This indicates your eye's refractive balance is positioned directly onto the retina at this testing distance."
        )
    }

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
                text = "Refraction Screening Result",
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
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = explanation,
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
                    .testTag("duochrome_done_btn")
            ) {
                Text("Done")
            }
        }
    }
}
