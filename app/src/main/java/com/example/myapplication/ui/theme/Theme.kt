package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Se fija tema claro de contraste alto y se desactiva el color dinámico
// para mantener la identidad de marca consistente (sección 6.4 del documento).
private val AppColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = IndigoLight,
    onPrimaryContainer = SurfaceWhite,
    secondary = AccentBlue,
    onSecondary = SurfaceWhite,
    tertiary = AccentPink,
    onTertiary = SurfaceWhite,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = Divider,
    error = DangerRed,
    onError = SurfaceWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
