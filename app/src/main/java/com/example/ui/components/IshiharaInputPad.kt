package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IshiharaInputPad(
    currentValue: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReportNothing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ishihara_input_pad"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Input display box
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Perception:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (currentValue.isEmpty()) "Tap digits or 'Nothing'" else currentValue,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (currentValue.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                )
            }
        }

        // Numeric Keypad Grid
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { digit ->
                    FilledTonalButton(
                        onClick = {
                            if (currentValue.length < 3) {
                                onValueChange(currentValue + digit)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("keypad_digit_$digit")
                    ) {
                        Text(text = digit, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Bottom keypad row: Backspace, '0', and Submit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (currentValue.isNotEmpty()) {
                        onValueChange(currentValue.dropLast(1))
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("keypad_backspace")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete")
            }

            FilledTonalButton(
                onClick = {
                    if (currentValue.length < 3) {
                        onValueChange(currentValue + "0")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("keypad_digit_0")
            ) {
                Text(text = "0", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onSubmit,
                enabled = currentValue.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("keypad_submit")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Submit")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Next", fontWeight = FontWeight.Bold)
            }
        }

        // "Nothing / Cannot See Any Number" Button
        OutlinedButton(
            onClick = onReportNothing,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("keypad_nothing_btn")
        ) {
            Icon(imageVector = Icons.Default.VisibilityOff, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nothing Visible / Cannot Distinguish", fontSize = 13.sp)
        }
    }
}
