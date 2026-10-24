package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val MosulDarkColorScheme = darkColorScheme(
    primary = HadbaGold,
    onPrimary = EmeraldDeep,
    primaryContainer = EmeraldCard,
    onPrimaryContainer = HadbaGoldLight,
    secondary = EmeraldBright,
    onSecondary = EmeraldDeep,
    secondaryContainer = EmeraldAccent,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = HadbaAmber,
    onTertiary = EmeraldDeep,
    background = EmeraldDeep,
    onBackground = TextPrimaryDark,
    surface = EmeraldSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = EmeraldCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF285E50)
)

private val MosulLightColorScheme = lightColorScheme(
    primary = Color(0xFF0E473B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9ECE6),
    onPrimaryContainer = Color(0xFF072921),
    secondary = Color(0xFFB38718),
    onSecondary = Color.White,
    secondaryContainer = HadbaGoldSoft,
    onSecondaryContainer = Color(0xFF3E2D00),
    tertiary = TigrisBlue,
    onTertiary = Color.White,
    background = MosulAlabasterBg,
    onBackground = TextPrimaryLight,
    surface = MosulAlabasterCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = MosulAlabasterSurface,
    onSurfaceVariant = TextSecondaryLight,
    outline = MosulStoneBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MosulDarkColorScheme else MosulLightColorScheme

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
