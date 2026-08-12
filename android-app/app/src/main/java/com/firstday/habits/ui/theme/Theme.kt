package com.firstday.habits.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = ForestGreen,
    onPrimary = SageSurface,
    primaryContainer = ForestGreenLight,
    onPrimaryContainer = SageSurface,
    secondary = CoralAccent,
    onSecondary = SageSurface,
    secondaryContainer = CoralAccentLight,
    onSecondaryContainer = SageSurface,
    tertiary = ForestGreenLight,
    onTertiary = SageSurface,
    background = SageSurface,
    onBackground = Color(0xFF1A1F2C),
    surface = SageSurface,
    onSurface = Color(0xFF1A1F2C),
    surfaceVariant = SageSurfaceVariant,
    onSurfaceVariant = Color(0xFF49546D),
    outline = Color(0xFF9CA3B5),
    error = Color(0xFFC0392B),
    onError = SageSurface,
)

private val DarkColors = darkColorScheme(
    primary = ForestGreenDark,
    onPrimary = Color(0xFF0D1B14),
    primaryContainer = ForestGreen,
    onPrimaryContainer = SageSurface,
    secondary = CoralAccentDark,
    onSecondary = Color(0xFF3B1A0F),
    secondaryContainer = CoralAccent,
    onSecondaryContainer = SageSurface,
    tertiary = ForestGreenLight,
    onTertiary = Color(0xFF0D1B14),
    background = SageSurfaceDark,
    onBackground = SageSurface,
    surface = SageSurfaceDark,
    onSurface = SageSurface,
    surfaceVariant = SageSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB0B7C9),
    outline = Color(0xFF6B7280),
    error = Color(0xFFE57373),
    onError = Color(0xFF3B0A0A),
)

@Composable
fun FirstDayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FirstDayTypography,
        content = content,
    )
}
