package com.example.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class AstigmatismDialType(val label: String, val description: String) {
    CLOCK_DIAL("Clock Dial (1–12)", "Classic 12-hour meridian dial with triple parallel lines"),
    SUNBURST_FAN("180° Fan Sunburst", "Radial spokes every 15° with degree protractor markings"),
    ORTHOGONAL_CROSS("Orthogonal Cross", "Vertical vs Horizontal high-contrast grating blocks")
}

/**
 * Astigmatism Testing Dial Component.
 * Displays high-contrast radial lines allowing users to identify potential astigmatism
 * by detecting directional blurring, line doubling, or variations in line thickness.
 *
 * Supports:
 * - Clock Face Dial (12 meridians, triple parallel lines)
 * - 180-degree Fan Sunburst Dial
 * - Orthogonal Cross Grating
 * - Interactive Astigmatic Blur & Thickness Variation Simulation
 */
@Composable
fun AstigmatismDialCanvas(
    dialType: AstigmatismDialType = AstigmatismDialType.CLOCK_DIAL,
    selectedHour: Int? = null,
    selectedDegrees: Int? = null,
    simulatedAstigmatismAxis: Float? = null, // In degrees (0..180). Simulates directional blur/thickness
    onHourSelected: ((Int) -> Unit)? = null,
    onDegreeSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .aspectRatio(1f)
    ) {
        val lineColor = MaterialTheme.colorScheme.onSurface
        val selectedColor = MaterialTheme.colorScheme.primary

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(dialType) {
                    detectTapGestures { tapOffset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val dx = tapOffset.x - centerX
                        val dy = tapOffset.y - centerY

                        // Calculate angle in degrees (0 is 12 o'clock / top)
                        var angleFromTop = Math.toDegrees(atan2(dx.toDouble(), -dy.toDouble()))
                        if (angleFromTop < 0) angleFromTop += 360.0

                        // Hour: 1..12
                        var hour = Math.round(angleFromTop / 30.0).toInt()
                        if (hour == 0) hour = 12
                        onHourSelected?.invoke(hour)

                        // Protractor angle from right (standard 0..180 axis)
                        var axisAngle = Math.toDegrees(atan2(-dy.toDouble(), dx.toDouble()))
                        if (axisAngle < 0) axisAngle += 180.0
                        if (axisAngle > 180) axisAngle -= 180.0
                        val snappedDegrees = (Math.round(axisAngle / 15.0) * 15).toInt()
                        onDegreeSelected?.invoke(snappedDegrees)
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.86f
            val innerRadius = radius * 0.22f

            // Central fixation target (keeps accommodation fixed at center)
            drawCircle(
                color = lineColor,
                radius = 8.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = center
            )
            drawCircle(
                color = lineColor,
                radius = 1.5.dp.toPx(),
                center = center
            )

            val textPaint = Paint().apply {
                color = lineColor.toArgb()
                textSize = 13.dp.toPx()
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }

            when (dialType) {
                AstigmatismDialType.CLOCK_DIAL -> {
                    // 12 Meridians with 3 parallel lines each
                    for (hour in 1..12) {
                        val angleDeg = (hour * 30.0) - 90.0 // 12 o'clock is -90 deg
                        val rad = angleDeg * PI / 180.0
                        val isSelected = selectedHour == hour || (selectedHour != null && (selectedHour + 6) % 12 == hour % 12)

                        // Calculate simulated astigmatic blur/thickness variation if simulation enabled
                        val simulatedAngle = ((hour * 30) % 180).toFloat()
                        val (lineW, lineAlpha) = calculateSimulatedBlur(simulatedAngle, simulatedAstigmatismAxis, isSelected)

                        val drawColor = if (isSelected) selectedColor else lineColor.copy(alpha = lineAlpha)
                        val strokeW = if (isSelected) 5.dp.toPx() else lineW.dp.toPx()

                        // 3 closely spaced parallel lines
                        val perpRad = rad + (PI / 2.0)
                        val spacing = 3.6.dp.toPx()

                        val offsets = listOf(-spacing, 0f, spacing)
                        for (off in offsets) {
                            val offX = (off * cos(perpRad)).toFloat()
                            val offY = (off * sin(perpRad)).toFloat()

                            val startX = (center.x + innerRadius * cos(rad)).toFloat() + offX
                            val startY = (center.y + innerRadius * sin(rad)).toFloat() + offY
                            val endX = (center.x + radius * cos(rad)).toFloat() + offX
                            val endY = (center.y + radius * sin(rad)).toFloat() + offY

                            drawLine(
                                color = drawColor,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = strokeW
                            )
                        }

                        // Hour labels
                        val labelDist = radius * 1.09f
                        val labelX = (center.x + labelDist * cos(rad)).toFloat()
                        val labelY = (center.y + labelDist * sin(rad)).toFloat() + 4.5.dp.toPx()

                        drawContext.canvas.nativeCanvas.drawText(
                            hour.toString(),
                            labelX,
                            labelY,
                            textPaint
                        )
                    }
                }

                AstigmatismDialType.SUNBURST_FAN -> {
                    // Full 180-degree fan with spokes every 15 degrees
                    for (deg in 0..180 step 15) {
                        val rad = Math.toRadians((180 - deg).toDouble()) // 0° right, 90° top, 180° left
                        val isSelected = selectedDegrees == deg

                        val (lineW, lineAlpha) = calculateSimulatedBlur(deg.toFloat(), simulatedAstigmatismAxis, isSelected)
                        val drawColor = if (isSelected) selectedColor else lineColor.copy(alpha = lineAlpha)
                        val strokeW = if (isSelected) 4.5.dp.toPx() else lineW.dp.toPx()

                        val startX = (center.x + innerRadius * cos(rad)).toFloat()
                        val startY = (center.y - innerRadius * sin(rad)).toFloat()
                        val endX = (center.x + radius * cos(rad)).toFloat()
                        val endY = (center.y - radius * sin(rad)).toFloat()

                        drawLine(
                            color = drawColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = strokeW
                        )

                        // Symmetric spoke on bottom half
                        val startXBot = (center.x - innerRadius * cos(rad)).toFloat()
                        val startYBot = (center.y + innerRadius * sin(rad)).toFloat()
                        val endXBot = (center.x - radius * cos(rad)).toFloat()
                        val endYBot = (center.y + radius * sin(rad)).toFloat()

                        drawLine(
                            color = drawColor,
                            start = Offset(startXBot, startYBot),
                            end = Offset(endXBot, endYBot),
                            strokeWidth = strokeW
                        )

                        // Degree label at outer rim
                        val labelDist = radius * 1.10f
                        val labelX = (center.x + labelDist * cos(rad)).toFloat()
                        val labelY = (center.y - labelDist * sin(rad)).toFloat() + 4.dp.toPx()

                        if (deg % 30 == 0) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "${deg}°",
                                labelX,
                                labelY,
                                textPaint
                            )
                        }
                    }
                }

                AstigmatismDialType.ORTHOGONAL_CROSS -> {
                    // High-contrast grating blocks (Vertical vs Horizontal)
                    val boxSize = radius * 0.7f
                    val numBars = 7
                    val barWidth = boxSize / (numBars * 2f)

                    // Vertical bars (top right quadrant and bottom left quadrant)
                    for (i in 0 until numBars) {
                        val xOffset = center.x - (boxSize / 2f) + (i * barWidth * 2f)
                        val isVertSelected = selectedHour == 12 || selectedDegrees == 90
                        val (w, a) = calculateSimulatedBlur(90f, simulatedAstigmatismAxis, isVertSelected)

                        drawLine(
                            color = if (isVertSelected) selectedColor else lineColor.copy(alpha = a),
                            start = Offset(xOffset, center.y - radius * 0.85f),
                            end = Offset(xOffset, center.y - innerRadius),
                            strokeWidth = barWidth * (w / 2.5f)
                        )
                        drawLine(
                            color = if (isVertSelected) selectedColor else lineColor.copy(alpha = a),
                            start = Offset(xOffset, center.y + innerRadius),
                            end = Offset(xOffset, center.y + radius * 0.85f),
                            strokeWidth = barWidth * (w / 2.5f)
                        )
                    }

                    // Horizontal bars (left and right)
                    for (i in 0 until numBars) {
                        val yOffset = center.y - (boxSize / 2f) + (i * barWidth * 2f)
                        val isHorizSelected = selectedHour == 3 || selectedDegrees == 0 || selectedDegrees == 180
                        val (w, a) = calculateSimulatedBlur(0f, simulatedAstigmatismAxis, isHorizSelected)

                        drawLine(
                            color = if (isHorizSelected) selectedColor else lineColor.copy(alpha = a),
                            start = Offset(center.x - radius * 0.85f, yOffset),
                            end = Offset(center.x - innerRadius, yOffset),
                            strokeWidth = barWidth * (w / 2.5f)
                        )
                        drawLine(
                            color = if (isHorizSelected) selectedColor else lineColor.copy(alpha = a),
                            start = Offset(center.x + innerRadius, yOffset),
                            end = Offset(center.x + radius * 0.85f, yOffset),
                            strokeWidth = barWidth * (w / 2.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calculates line thickness and alpha when astigmatic simulation is active.
 * In uncorrected astigmatism, lines parallel to the focal meridian appear sharp and dark,
 * while lines 90 degrees away appear widened (thickened) and washed out (faded/blurred).
 */
private fun calculateSimulatedBlur(
    lineAngle: Float,
    simulatedAxis: Float?,
    isSelected: Boolean
): Pair<Float, Float> {
    if (isSelected) return Pair(4.5f, 1f)
    if (simulatedAxis == null) return Pair(2.5f, 1f)

    var diff = abs((lineAngle % 180) - (simulatedAxis % 180))
    if (diff > 90) diff = 180 - diff

    // diff = 0: exactly parallel to clear meridian -> sharp, high contrast
    // diff = 90: perpendicular meridian -> blurred, thickened, lower contrast
    val blurFactor = diff / 90f // 0f .. 1f

    val simulatedWidth = 2.2f + (blurFactor * 3.2f) // lines widen from 2.2dp to 5.4dp
    val simulatedAlpha = 1.0f - (blurFactor * 0.55f) // contrast drops down to 0.45

    return Pair(simulatedWidth, simulatedAlpha)
}
