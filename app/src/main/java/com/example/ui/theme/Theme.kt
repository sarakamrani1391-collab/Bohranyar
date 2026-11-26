package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = OliveGreen,
    onPrimary = PureWhite,
    primaryContainer = LightSage,
    onPrimaryContainer = DarkOlive,
    secondary = SageGreen,
    onSecondary = DarkOlive,
    secondaryContainer = SurfaceVariantSage,
    onSecondaryContainer = DarkOlive,
    tertiary = DarkOlive,
    onTertiary = PureWhite,
    background = LightGrayBg,
    onBackground = TextPrimaryOlive,
    surface = PureWhite,
    onSurface = TextPrimaryOlive,
    surfaceVariant = SurfaceVariantSage,
    onSurfaceVariant = TextSecondaryOlive,
    outline = OutlineSage,
    error = EmergencyRed,
    onError = PureWhite,
    errorContainer = EmergencyRedSoft,
    onErrorContainer = EmergencyRedDark
)

private val HighContrastLightColorScheme = LightColorScheme.copy(
    primary = DarkOlive,
    onBackground = DarkOlive,
    onSurface = DarkOlive,
    onSurfaceVariant = TextPrimaryOlive,
    outline = DarkOlive
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = DarkOlive,
    primaryContainer = DarkOlive,
    onPrimaryContainer = LightSage,
    secondary = OliveGreen,
    onSecondary = PureWhite,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = LightSage,
    tertiary = LightSage,
    onTertiary = DarkOlive,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceSecondary,
    outline = OliveGreen,
    error = EmergencyRed,
    onError = PureWhite,
    errorContainer = EmergencyRedDark,
    onErrorContainer = PureWhite
)

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun HamyarBohranTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        highContrast -> HighContrastLightColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = buildAppTypography(largeText = largeText),
            shapes = AppShapes,
            content = content
        )
    }
}
