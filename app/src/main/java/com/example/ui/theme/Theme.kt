package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MedicalDarkPrimary,
    onPrimary = MedicalDarkOnPrimary,
    primaryContainer = MedicalDarkPrimaryContainer,
    onPrimaryContainer = MedicalDarkOnPrimaryContainer,
    secondary = MedicalDarkSecondary,
    onSecondary = MedicalDarkOnSecondary,
    secondaryContainer = MedicalDarkSecondaryContainer,
    onSecondaryContainer = MedicalDarkOnSecondaryContainer,
    tertiary = MedicalDarkTertiary,
    onTertiary = MedicalDarkOnTertiary,
    tertiaryContainer = MedicalDarkTertiaryContainer,
    onTertiaryContainer = MedicalDarkOnTertiaryContainer
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalPrimary,
    onPrimary = MedicalOnPrimary,
    primaryContainer = MedicalPrimaryContainer,
    onPrimaryContainer = MedicalOnPrimaryContainer,
    secondary = MedicalSecondary,
    onSecondary = MedicalOnSecondary,
    secondaryContainer = MedicalSecondaryContainer,
    onSecondaryContainer = MedicalOnSecondaryContainer,
    tertiary = MedicalTertiary,
    onTertiary = MedicalOnTertiary,
    tertiaryContainer = MedicalTertiaryContainer,
    onTertiaryContainer = MedicalOnTertiaryContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep medical blue branding consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
