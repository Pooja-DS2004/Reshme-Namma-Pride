package com.reshmenamma.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Mulberry,
    onPrimary = SilkWhite,
    primaryContainer = MulberryLight.copy(alpha = 0.3f),
    onPrimaryContainer = MulberryDark,
    secondary = SilkGold,
    onSecondary = Color.White,
    secondaryContainer = SilkGoldLight,
    onSecondaryContainer = SilkGoldDark,
    tertiary = LeafGreen,
    onTertiary = Color.White,
    tertiaryContainer = LeafGreenLight.copy(alpha = 0.3f),
    onTertiaryContainer = LeafGreenDark,
    error = DangerRed,
    onError = Color.White,
    errorContainer = DangerRed.copy(alpha = 0.1f),
    onErrorContainer = DangerRed,
    background = SilkWhite,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
)

@Composable
fun ReshmeNammaTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MulberryDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            displayLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 0.sp
            ),
            headlineLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            headlineMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            titleLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            titleMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            labelLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        ),
        shapes = Shapes(
            small = MaterialTheme.shapes.small,
            medium = MaterialTheme.shapes.medium,
            large = MaterialTheme.shapes.large
        ),
        content = content
    )
}