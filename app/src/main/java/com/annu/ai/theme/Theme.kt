package com.annu.ai.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = VoidBlack,
    primaryContainer = CyanGlow,
    onPrimaryContainer = NeonCyan,
    secondary = RadiantViolet,
    onSecondary = VoidBlack,
    secondaryContainer = VioletGlow,
    onSecondaryContainer = RadiantViolet,
    tertiary = NeonPink,
    onTertiary = VoidBlack,
    tertiaryContainer = PinkGlow,
    onTertiaryContainer = NeonPink,
    background = VoidBlack,
    onBackground = TextPrimary,
    surface = DeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavy,
    onSurfaceVariant = TextSecondary,
    outline = BorderNavy,
    error = CoralRed,
    onError = VoidBlack
)

@Composable
fun AnnuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Annu AI is intentionally designed as an immersive futuristic dark theme
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AnnuTypography,
        content = content
    )
}
