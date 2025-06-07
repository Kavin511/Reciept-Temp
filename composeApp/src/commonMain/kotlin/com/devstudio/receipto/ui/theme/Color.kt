package com.devstudio.receipto.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Define your light theme colors
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBB86FC),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF03A9F4),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFB3E5FC),
    onTertiaryContainer = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFCD8DF),
    onErrorContainer = Color.Black,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F), // A good candidate for replacing Color.Gray
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFF4EFF4),
    inverseSurface = Color(0xFF313033),
    inversePrimary = Color(0xFFD0BCFF),
    surfaceTint = Color(0xFF6200EE),
    // scrim = Color.Black, // Typically not overridden directly for Material 3
)

// Define your dark theme colors
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF03DAC5), // Example, can be same as secondary for dark
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFF81D4FA),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF01579B),
    onTertiaryContainer = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF121212), // Or another dark surface color like #1E1E1E
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F), // Darker variant of surface
    onSurfaceVariant = Color(0xFFCAC4D0), // Text/icons on surfaceVariant
    outline = Color(0xFF938F99),
    inverseOnSurface = Color(0xFF1C1B1F),
    inverseSurface = Color(0xFFE6E1E5),
    inversePrimary = Color(0xFF6200EE),
    surfaceTint = Color(0xFFBB86FC),
    // scrim = Color.Black, // Typically not overridden directly for Material 3
)
