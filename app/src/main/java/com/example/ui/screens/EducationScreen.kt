package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MedicalDisclaimerBanner

@Composable
fun EducationScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("education_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Eye Health & Optometry Guide",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Essential clinical knowledge to understand your visual test results and protect your long-term eye health.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            EducationCard(
                title = "What Does 20/20 Vision Really Mean?",
                body = "The Snellen fraction (e.g. 20/20 in US feet, or 6/6 in metric meters) describes visual acuity at a distance of 20 feet.\n\n" +
                        "• 20/20 means you can clearly see at 20 feet what an average person with normal vision sees at 20 feet.\n" +
                        "• 20/40 means you must be as close as 20 feet to see what a person with normal vision can see from 40 feet away.\n" +
                        "• 20/15 or 20/10 is 'super-normal' vision — resolving finer detail than average."
            )
        }

        item {
            EducationCard(
                title = "Common Refractive Conditions",
                body = "• Myopia (Nearsightedness): Distant objects appear blurry while near objects are clear. Light focuses in front of the retina.\n\n" +
                        "• Hyperopia (Farsightedness): Distant objects may be clear, but near objects require excessive focusing effort. Light focuses behind the retina.\n\n" +
                        "• Astigmatism: The cornea or lens has an asymmetrical oval curve (like a football instead of a basketball), causing blur at all distances.\n\n" +
                        "• Presbyopia: Age-related loss of crystalline lens elasticity (usually starting around age 40), requiring reading glasses for close-up print."
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Urgent Red Flag Symptoms",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Seek immediate emergency ophthalmic care if you experience:\n" +
                                "• Sudden partial or total vision loss in one or both eyes\n" +
                                "• Sudden onset of flashes of light or an influx of new floaters\n" +
                                "• A dark curtain or shadow falling across your field of vision\n" +
                                "• Severe deep eye pain with redness, nausea, or rainbow halos around lights\n" +
                                "• Blunt ocular trauma or chemical exposure",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = Color(0xFF5C0000)
                    )
                }
            }
        }

        item {
            EducationCard(
                title = "Recommended Eye Exam Frequency",
                body = "The American Optometric Association (AOA) recommends:\n" +
                        "• Children: First exam at 6-12 months, at least once between ages 3-5, and annually school-age.\n" +
                        "• Adults 18-64: Comprehensive exam at least every 2 years (annually for contact lens wearers or diabetics).\n" +
                        "• Adults 65+: Comprehensive exam annually to screen for glaucoma, macular degeneration, and cataracts."
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Developer & Architecture",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Lead Developer: Osborneferds\n" +
                                "• Application: Eye Test — Clinical Vision Screening Suite\n" +
                                "• Organization: Oculus Vision Labs / VisionCare Optics\n" +
                                "• Framework: Kotlin & Jetpack Compose (Material 3)\n" +
                                "• Standards: Snellen Imperial/Metric, Ishihara 38 Plates, Lancaster-Regan Meridians",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            MedicalDisclaimerBanner()
        }
    }
}

@Composable
fun EducationCard(
    title: String,
    body: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        }
    }
}
