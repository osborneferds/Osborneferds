package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

enum class VisionFilter(val displayName: String, val shortDesc: String) {
    NORMAL("Normal Vision", "Standard trichromatic human perception"),
    PROTANOPIA("Protanopia", "Red-blindness (L-cone absent)"),
    DEUTERANOPIA("Deuteranopia", "Green-blindness (M-cone absent)"),
    TRITANOPIA("Tritanopia", "Blue-blindness (S-cone absent)"),
    ACHROMATOPSIA("Monochrome", "Total absence of color perception")
}

data class IshiharaDot(
    val relX: Float, // -1f .. 1f
    val relY: Float, // -1f .. 1f
    val relRadius: Float,
    val colorIndex: Int
)

/**
 * Procedural Ishihara Pseudoisochromatic Plate.
 * Generates an authentic dot mosaic mimicking medical color blindness test plates.
 * Supports clinical simulation filters for Protanopia, Deuteranopia, Tritanopia, and Achromatopsia.
 */
@Composable
fun IshiharaPlateCanvas(
    number: String,
    visionFilter: VisionFilter = VisionFilter.NORMAL,
    modifier: Modifier = Modifier
) {
    // Generate deterministic dots seeded by plate number for consistent rendering
    val dots = remember(number) {
        generatePlateDots(seed = number.hashCode().toLong())
    }

    // Color palettes for foreground (digit) vs background
    val bgColors = remember(number) {
        listOf(
            Color(0xFF758A46), Color(0xFF869E4F), Color(0xFF6B803E),
            Color(0xFF919E63), Color(0xFF63733A), Color(0xFF809458),
            Color(0xFF8D8D5A), Color(0xFF6E7D4E)
        )
    }

    val fgColors = remember(number) {
        when (number) {
            "12" -> listOf(
                Color(0xFFE65100), Color(0xFFD84315), Color(0xFFBF360C),
                Color(0xFFF4511E), Color(0xFFEF6C00)
            )
            "8", "29", "5", "74" -> listOf(
                Color(0xFFC75C41), Color(0xFFBD543B), Color(0xFFB54C34),
                Color(0xFFD1684E), Color(0xFFA8422B)
            )
            "26", "42" -> listOf(
                Color(0xFFBD4B3B), Color(0xFFB54234), Color(0xFFC65842),
                Color(0xFFA83A2C), Color(0xFF9E3224)
            )
            else -> listOf(
                Color(0xFFBC583A), Color(0xFFB04D30), Color(0xFFA54327),
                Color(0xFFC86649), Color(0xFF9E3A20)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(Color(0xFF1E232A))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.95f

            // Fill base plate disc
            drawCircle(
                color = applyVisionFilter(Color(0xFFF2EFE9), visionFilter),
                radius = radius,
                center = center
            )

            // Render each mosaic dot
            for (dot in dots) {
                val dotCenter = Offset(
                    center.x + dot.relX * radius * 0.92f,
                    center.y + dot.relY * radius * 0.92f
                )
                val dotPxRadius = dot.relRadius * radius

                val isInDigit = isInsideGlyph(number, dot.relX, dot.relY)
                val rawColor = if (isInDigit) {
                    fgColors[dot.colorIndex % fgColors.size]
                } else {
                    bgColors[dot.colorIndex % bgColors.size]
                }

                val finalColor = applyVisionFilter(rawColor, visionFilter)

                drawCircle(
                    color = finalColor,
                    radius = dotPxRadius,
                    center = dotCenter
                )
            }
        }
    }
}

/**
 * Mathematically simulates color vision deficiency using standard LMS color transformation.
 */
fun applyVisionFilter(color: Color, filter: VisionFilter): Color {
    val r = color.red
    val g = color.green
    val b = color.blue
    return when (filter) {
        VisionFilter.NORMAL -> color
        VisionFilter.PROTANOPIA -> {
            val rNew = (0.56667f * r + 0.43333f * g).coerceIn(0f, 1f)
            val gNew = (0.55833f * r + 0.44167f * g).coerceIn(0f, 1f)
            val bNew = (0.24167f * g + 0.75833f * b).coerceIn(0f, 1f)
            Color(rNew, gNew, bNew, color.alpha)
        }
        VisionFilter.DEUTERANOPIA -> {
            val rNew = (0.625f * r + 0.375f * g).coerceIn(0f, 1f)
            val gNew = (0.70f * r + 0.30f * g).coerceIn(0f, 1f)
            val bNew = (0.30f * g + 0.70f * b).coerceIn(0f, 1f)
            Color(rNew, gNew, bNew, color.alpha)
        }
        VisionFilter.TRITANOPIA -> {
            val rNew = (0.95f * r + 0.05f * g).coerceIn(0f, 1f)
            val gNew = (0.43333f * g + 0.56667f * b).coerceIn(0f, 1f)
            val bNew = (0.475f * g + 0.525f * b).coerceIn(0f, 1f)
            Color(rNew, gNew, bNew, color.alpha)
        }
        VisionFilter.ACHROMATOPSIA -> {
            val lum = (0.299f * r + 0.587f * g + 0.114f * b).coerceIn(0f, 1f)
            Color(lum, lum, lum, color.alpha)
        }
    }
}

private fun generatePlateDots(seed: Long): List<IshiharaDot> {
    val rng = Random(seed)
    val list = mutableListOf<IshiharaDot>()

    // Concentric ring distribution with random angular jitter for dense, natural packing
    val rings = 14
    for (r in 1..rings) {
        val normR = r.toFloat() / rings
        val circumference = 2.0 * Math.PI * normR
        val numDotsInRing = (circumference * 11).toInt().coerceAtLeast(6)

        for (i in 0 until numDotsInRing) {
            val angle = (i.toFloat() / numDotsInRing) * 2.0 * Math.PI + (rng.nextFloat() * 0.2 - 0.1)
            val radialJitter = normR + (rng.nextFloat() * 0.05f - 0.025f)
            if (radialJitter <= 0.98f) {
                val x = (radialJitter * cos(angle)).toFloat()
                val y = (radialJitter * sin(angle)).toFloat()
                val relRadius = (0.028f + rng.nextFloat() * 0.022f)
                val cIndex = rng.nextInt(100)
                list.add(IshiharaDot(x, y, relRadius, cIndex))
            }
        }
    }
    // Add center dots
    for (i in 0..5) {
        val ang = rng.nextFloat() * 2f * Math.PI.toFloat()
        val dist = rng.nextFloat() * 0.08f
        list.add(
            IshiharaDot(
                dist * cos(ang),
                dist * sin(ang),
                0.035f + rng.nextFloat() * 0.015f,
                rng.nextInt(100)
            )
        )
    }

    return list
}

/**
 * Checks whether a normalized coordinate (-1..1, -1..1) falls within the stroke of the requested number.
 */
private fun isInsideGlyph(number: String, x: Float, y: Float): Boolean {
    // If double digit (e.g. "12", "29", "74", "45", "16", "26", "42", "73")
    return if (number.length == 2) {
        val leftX = (x + 0.35f) * 1.7f
        val rightX = (x - 0.35f) * 1.7f
        val scaledY = y * 1.4f
        isInsideSingleDigit(number[0], leftX, scaledY) || isInsideSingleDigit(number[1], rightX, scaledY)
    } else {
        isInsideSingleDigit(number.firstOrNull() ?: ' ', x * 1.3f, y * 1.3f)
    }
}

private fun isInsideSingleDigit(digit: Char, x: Float, y: Float): Boolean {
    val tol = 0.18f

    return when (digit) {
        '0' -> {
            val ovalDist = hypot((x / 0.7f).toDouble(), y.toDouble()).toFloat()
            kotlin.math.abs(ovalDist - 0.38f) < tol
        }
        '1' -> {
            // Vertical bar at x ~ 0, y between -0.6 and 0.6
            (kotlin.math.abs(x) < 0.12f && y in -0.6f..0.6f) ||
            // Top left serif flag
            (x in -0.25f..0f && y in -0.6f..-0.35f && kotlin.math.abs(y - (-0.6f + (x + 0.25f) * 0.9f)) < 0.12f)
        }
        '2' -> {
            // Top arc: center (0, -0.28), radius ~0.28, y <= -0.28
            val arcDist = hypot(x.toDouble(), (y + 0.28f).toDouble()).toFloat()
            (kotlin.math.abs(arcDist - 0.26f) < tol && y <= -0.15f && x >= -0.28f) ||
            // Diagonal line from (0.22, -0.15) to (-0.24, 0.45)
            (kotlin.math.abs(y - (-0.15f + (x - 0.22f) * -1.3f)) < 0.14f && y in -0.15f..0.45f) ||
            // Base horizontal bar
            (y in 0.38f..0.54f && x in -0.26f..0.26f)
        }
        '3' -> {
            val topArc = hypot(x.toDouble(), (y + 0.22f).toDouble()).toFloat()
            val botArc = hypot(x.toDouble(), (y - 0.22f).toDouble()).toFloat()
            (kotlin.math.abs(topArc - 0.26f) < tol && x >= -0.05f && y <= 0.02f) ||
            (kotlin.math.abs(botArc - 0.28f) < tol && x >= -0.05f && y >= -0.02f)
        }
        '4' -> {
            // Vertical right stem at x ~ 0.18
            (kotlin.math.abs(x - 0.18f) < 0.12f && y in -0.6f..0.6f) ||
            // Horizontal crossbar at y ~ 0.15
            (kotlin.math.abs(y - 0.15f) < 0.12f && x in -0.35f..0.25f) ||
            // Diagonal stroke
            (kotlin.math.abs(y - (-0.6f + (x + 0.3f) * 1.6f)) < 0.14f && x in -0.3f..0.18f && y in -0.6f..0.18f)
        }
        '5' -> {
            // Top bar
            (y in -0.58f..-0.42f && x in -0.28f..0.25f) ||
            // Left vertical upper stem
            (x in -0.28f..-0.12f && y in -0.52f..-0.05f) ||
            // Bottom loop centered around (0, 0.2)
            (kotlin.math.abs(hypot(x.toDouble(), (y - 0.18f).toDouble()).toFloat() - 0.28f) < tol && x >= -0.18f && y >= -0.1f)
        }
        '6' -> {
            // Bottom circle
            val botCircle = hypot(x.toDouble(), (y - 0.18f).toDouble()).toFloat()
            // Top curved stem
            (kotlin.math.abs(botCircle - 0.28f) < tol) ||
            (x in -0.32f..-0.08f && y in -0.55f..0.18f) ||
            (y in -0.6f..-0.45f && x in -0.2f..0.15f)
        }
        '7' -> {
            // Top bar
            (y in -0.58f..-0.42f && x in -0.3f..0.3f) ||
            // Slanted diagonal stem
            (kotlin.math.abs(x - (0.25f - (y + 0.5f) * 0.45f)) < 0.12f && y in -0.5f..0.6f)
        }
        '8' -> {
            val topLoop = hypot(x.toDouble(), (y + 0.24f).toDouble()).toFloat()
            val botLoop = hypot(x.toDouble(), (y - 0.24f).toDouble()).toFloat()
            (kotlin.math.abs(topLoop - 0.23f) < tol) || (kotlin.math.abs(botLoop - 0.27f) < tol)
        }
        '9' -> {
            val topCircle = hypot(x.toDouble(), (y + 0.18f).toDouble()).toFloat()
            (kotlin.math.abs(topCircle - 0.28f) < tol) ||
            (x in 0.08f..0.32f && y in -0.18f..0.55f) ||
            (y in 0.45f..0.6f && x in -0.15f..0.2f)
        }
        else -> false
    }
}
