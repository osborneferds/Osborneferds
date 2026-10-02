package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class EDirection(val angle: Float, val label: String) {
    RIGHT(0f, "Right"),
    DOWN(90f, "Down"),
    LEFT(180f, "Left"),
    UP(270f, "Up")
}

/**
 * Mathematically precise 5x5 Snellen grid Tumbling E optotype.
 * Each stroke width and gap width is exactly 1/5th of the total size.
 */
@Composable
fun TumblingEOptotype(
    direction: EDirection,
    size: Dp = 120.dp,
    color: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val unit = this.size.minDimension / 5f
        val w = unit * 5f
        val h = unit * 5f
        val left = (this.size.width - w) / 2f
        val top = (this.size.height - h) / 2f

        rotate(degrees = direction.angle, pivot = center) {
            // Left vertical spine: width = 1 unit, height = 5 units
            drawRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(unit, h)
            )
            // Top horizontal arm: width = 5 units, height = 1 unit
            drawRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(w, unit)
            )
            // Middle horizontal arm: width = 5 units, height = 1 unit
            drawRect(
                color = color,
                topLeft = Offset(left, top + unit * 2f),
                size = Size(w, unit)
            )
            // Bottom horizontal arm: width = 5 units, height = 1 unit
            drawRect(
                color = color,
                topLeft = Offset(left, top + unit * 4f),
                size = Size(w, unit)
            )
        }
    }
}
