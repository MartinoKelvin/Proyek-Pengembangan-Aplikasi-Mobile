package com.example.cakapAi.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS ====================

// Brand Primary (Hijau/Emerald premium)
private val PrimaryEmerald = Color(0xFF10B981)
private val OnPrimary = Color(0xFFFFFFFF)

// Primary Container for Light vs Dark
private val PrimaryContainerLight = Color(0xFFD1FAE5) // Soft mint green
private val OnPrimaryContainerLight = Color(0xFF064E3B)

private val PrimaryContainerDark = Color(0xFF064E3B) // Dark forest green
private val OnPrimaryContainerDark = Color(0xFFD1FAE5)

// Secondary (Biru/Sky premium)
private val SecondarySky = Color(0xFF38BDF8)
private val OnSecondary = Color(0xFFFFFFFF)

private val SecondaryContainerLight = Color(0xFFE0F2FE)
private val OnSecondaryContainerLight = Color(0xFF0369A1)

private val SecondaryContainerDark = Color(0xFF0369A1)
private val OnSecondaryContainerDark = Color(0xFFE0F2FE)

// Tertiary (Neutral Slate / Dark Accents)
private val TertiarySlate = Color(0xFF0EA5E9) // Sky Blue as Tertiary
private val OnTertiary = Color(0xFFFFFFFF)

// Errors
private val ErrorColor = Color(0xFFEF4444)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainerLight = Color(0xFFFEE2E2)
private val OnErrorContainerLight = Color(0xFF991B1B)
private val ErrorContainerDark = Color(0xFF991B1B)
private val OnErrorContainerDark = Color(0xFFFEE2E2)

// Light Theme Background & Surface
private val BackgroundLight = Color(0xFFF8FAFC) // Very soft light slate
private val OnBackgroundLight = Color(0xFF0F172A) // Dark slate text
private val SurfaceLight = Color(0xFFFFFFFF) // Pure white cards
private val OnSurfaceLight = Color(0xFF0F172A)
private val SurfaceVariantLight = Color(0xFFF1F5F9) // Extra soft cards
private val OnSurfaceVariantLight = Color(0xFF475569) // Muted slate text
private val OutlineLight = Color(0xFFCBD5E1)

// Dark Theme Background & Surface (Desain awal gelap laut dalam khas CakapAI)
private val BackgroundDark = Color(0xFF050B14)
private val OnBackgroundDark = Color(0xFFFFFFFF)
private val SurfaceDark = Color(0xFF0C1424) // Surface/card tetap gelap
private val OnSurfaceDark = Color(0xFFFFFFFF)
private val SurfaceVariantDark = Color(0xFF152238)
private val OnSurfaceVariantDark = Color(0xFFE2E8F0)
private val OutlineDark = Color(0xFF1E293B)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = PrimaryEmerald,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondarySky,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiarySlate,
    onTertiary = OnTertiary,
    error = ErrorColor,
    onError = OnError,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmerald,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondarySky,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiarySlate,
    onTertiary = OnTertiary,
    error = ErrorColor,
    onError = OnError,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

// ==================== THEME ====================

@Composable
fun cakapAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
