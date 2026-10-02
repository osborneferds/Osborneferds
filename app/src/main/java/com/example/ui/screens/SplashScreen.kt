package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.75f) }
    val alphaAnim = remember { Animatable(0f) }
    var statusText by remember { mutableStateOf("Initializing clinical optotypes...") }
    var progress by remember { mutableFloatStateOf(0.15f) }

    LaunchedEffect(Unit) {
        // Entrance animation
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )

        delay(400)
        statusText = "Loading Snellen & Ishihara charts..."
        progress = 0.55f

        delay(500)
        statusText = "Calibrating optical display contrast..."
        progress = 0.85f

        delay(450)
        statusText = "System Ready"
        progress = 1.0f

        delay(350)
        onSplashFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1118), // Deep corporate midnight
                        Color(0xFF0F1E2E), // Slate navy
                        Color(0xFF09141F)
                    )
                )
            )
            .clickable { onSplashFinished() }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Company Logo with glowing aperture frame
            Box(
                modifier = Modifier
                    .scale(scaleAnim.value * pulseScale)
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.5.dp, Color(0xFF00B4D8), CircleShape)
                    .testTag("company_logo_image"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = "Company Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Company Brand Title
            Text(
                text = "OCULUS VISION LABS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00B4D8),
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // App Title
            Text(
                text = "Eye Test",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Clinical Acuity & Ocular Diagnostics Suite",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Bar & Loading Status
            Column(
                modifier = Modifier.fillMaxWidth(0.72f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF00B4D8),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Feature Highlights
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SplashFeatureBadge("Snellen 20/20")
                SplashFeatureBadge("Ishihara Plates")
                SplashFeatureBadge("Astigmatism Dial")
            }
        }

        // Company Footer at bottom of splash screen
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF00B4D8),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "VisionCare Optics & Health Sciences",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = Color(0xFF00B4D8),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Developed by Osborneferds",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF00B4D8)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap anywhere to continue",
                fontSize = 10.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun SplashFeatureBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFCBD5E1),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
