package com.example.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SnellenChartData
import com.example.data.model.SnellenLine
import java.util.Locale

@Composable
fun SnellenChartInteractiveView(
    isolatedLine: Int? = null,
    onLineSelected: ((SnellenLine) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                    ttsInstance?.language = Locale.US
                } catch (_: Throwable) {}
            }
        }
        ttsInstance = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    var selectedLineNumber by remember { mutableStateOf(isolatedLine) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("snellen_chart_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val linesToDisplay = if (selectedLineNumber != null) {
                SnellenChartData.lines.filter { it.lineNumber == selectedLineNumber }
            } else {
                SnellenChartData.lines
            }

            linesToDisplay.forEach { line ->
                SnellenLineRow(
                    line = line,
                    isSelected = selectedLineNumber == line.lineNumber,
                    onTap = {
                        selectedLineNumber = if (selectedLineNumber == line.lineNumber) null else line.lineNumber
                        onLineSelected?.invoke(line)
                        // Speak letters
                        val speechText = line.letters.joinToString(" ")
                        ttsInstance?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "line_${line.lineNumber}")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun SnellenLineRow(
    line: SnellenLine,
    isSelected: Boolean,
    onTap: () -> Unit
) {
    val fontSizeSp = when (line.lineNumber) {
        1 -> 76.sp
        2 -> 50.sp
        3 -> 36.sp
        4 -> 28.sp
        5 -> 22.sp
        6 -> 18.sp
        7 -> 15.sp
        8 -> 13.sp
        9 -> 11.sp
        10 -> 9.sp
        11 -> 8.sp
        else -> 12.sp
    }

    val letterSpacingSp = when (line.lineNumber) {
        1 -> 0.sp
        2 -> 16.sp
        3 -> 12.sp
        4 -> 10.sp
        5 -> 8.sp
        6 -> 7.sp
        7 -> 6.sp
        8 -> 5.sp
        9 -> 4.sp
        10 -> 3.sp
        11 -> 2.5.sp
        else -> 4.sp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFE8F5E9) else Color.Transparent)
            .clickable(onClick = onTap)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main letters area centered
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = line.letters.joinToString("  "),
                    color = Color.Black,
                    fontSize = fontSizeSp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = letterSpacingSp,
                    textAlign = TextAlign.Center
                )
            }

            // Right column: Line number & Acuity notation (matches the original uploaded image)
            Row(
                modifier = Modifier.width(80.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${line.lineNumber}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = line.imperialAcuity,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(52.dp),
                    textAlign = TextAlign.Start
                )
            }
        }

        // Green line under line 6 (20/30)
        if (line.hasGreenBar) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF2E7D32))
            )
        }

        // Red line under line 8 (20/20)
        if (line.hasRedBar) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFC62828))
            )
        }
    }
}
