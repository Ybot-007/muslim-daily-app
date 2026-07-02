package com.muslimdaily.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green = Color(0xFF0E4D3A)
private val DeepGreen = Color(0xFF082F28)
private val Gold = Color(0xFFD6B25E)
private val Ivory = Color(0xFFF7F3E8)
private val Ink = Color(0xFF15211D)

private val LightColors: ColorScheme = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = Ink,
    tertiary = Color(0xFF4D6F67),
    background = Ivory,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE6E0D2),
    onSurfaceVariant = Color(0xFF4E5C55)
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DeepGreen,
    secondary = Color(0xFF7CC7A4),
    onSecondary = DeepGreen,
    tertiary = Color(0xFFDAC48A),
    background = Color(0xFF071B17),
    onBackground = Color(0xFFF1EFE7),
    surface = Color(0xFF102A24),
    onSurface = Color(0xFFF1EFE7),
    surfaceVariant = Color(0xFF213D36),
    onSurfaceVariant = Color(0xFFD5DED8)
)

@Composable
fun MuslimDailyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
