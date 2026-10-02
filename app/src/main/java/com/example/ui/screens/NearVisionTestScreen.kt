package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.viewmodel.EyeTestViewModel
import com.example.ui.viewmodel.EyeTested

data class ReadingParagraph(
    val jaeger: String,
    val pointSize: Float,
    val text: String,
    val description: String
)

val readingParagraphs = listOf(
    ReadingParagraph(
        jaeger = "J7 (Large Print)",
        pointSize = 16f,
        text = "Good eye health begins with regular screenings and healthy habits. Give your eyes frequent breaks throughout the workday.",
        description = "Large font size. If this is the smallest readable, significant reading correction / reading glasses are likely needed."
    ),
    ReadingParagraph(
        jaeger = "J5 (Medium Print)",
        pointSize = 13f,
        text = "Maintain an arm's length distance between your eyes and computer monitors. Blink often to prevent tear film evaporation and dry eyes.",
        description = "Medium print (roughly newspaper subheadline). Mild difficulty reading standard books."
    ),
    ReadingParagraph(
        jaeger = "J3 (Standard Print)",
        pointSize = 10f,
        text = "Proper illumination prevents excessive ocular fatigue. Avoid high contrast glare between dark rooms and bright handheld mobile screens.",
        description = "Standard newspaper and paperback book font. Good practical near vision."
    ),
    ReadingParagraph(
        jaeger = "J2 (Fine Print)",
        pointSize = 8.5f,
        text = "Optometry exams evaluate not just visual sharpness, but also corneal curvature, intraocular pressure, and retinal vascular health.",
        description = "Fine print (contract and medication label size). Very good near visual acuity."
    ),
    ReadingParagraph(
        jaeger = "J1 (Super-Fine Print)",
        pointSize = 7f,
        text = "Presbyopia is the gradual natural loss of accommodation in the crystalline lens, typically becoming noticeable in the early forties.",
        description = "High resolution near visual acuity (J1 / 20/20 near equivalent). Pristine close focus accommodation."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearVisionTestScreen(
    viewModel: EyeTestViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEye by remember { mutableStateOf(EyeTested.BOTH_EYES) }
    var selectedParagraph by remember { mutableStateOf<ReadingParagraph?>(null) }
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
                title = { Text("Near Vision / Reading Test") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("near_vision_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("near_vision_screen")
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
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Jaeger Reading Scale",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Hold your phone at a comfortable reading distance (~35-40 cm / 14 inches). Select the smallest paragraph you can read clearly without straining.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    item {
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

                    items(readingParagraphs) { item ->
                        val isSelected = selectedParagraph == item
                        Card(
                            onClick = { selectedParagraph = item },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.jaeger,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${item.pointSize.toInt()} pt",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.text,
                                    fontSize = item.pointSize.sp,
                                    lineHeight = (item.pointSize * 1.35f).sp
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                val chosen = selectedParagraph ?: readingParagraphs[2]
                                viewModel.recordNearVisionResult(selectedEye, chosen.jaeger, chosen.description)
                                isSubmitted = true
                            },
                            enabled = selectedParagraph != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_near_vision_btn")
                        ) {
                            Text(if (selectedParagraph != null) "Confirm ${selectedParagraph!!.jaeger} is Smallest Readable" else "Select Smallest Readable Paragraph")
                        }
                    }

                    item {
                        MedicalDisclaimerBanner()
                    }
                }
            } else {
                // Results screen
                NearVisionResultsView(
                    eye = selectedEye,
                    paragraph = selectedParagraph ?: readingParagraphs.first(),
                    onRetest = { isSubmitted = false },
                    onDone = onBack
                )
            }
        }
    }
}

@Composable
fun NearVisionResultsView(
    eye: EyeTested,
    paragraph: ReadingParagraph,
    onRetest: () -> Unit,
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
                text = "Near Acuity Screening Result",
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
                        text = paragraph.jaeger,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = paragraph.description,
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
                    .testTag("near_vision_done_btn")
            ) {
                Text("Done")
            }
        }
    }
}
