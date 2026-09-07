package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.myapplication.core.utils.AppSettings

private val LightColors = lightColorScheme(
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

private val DarkColors = darkColorScheme(
    primary = IndigoLight,
    onPrimary = SurfaceWhite,
    primaryContainer = IndigoDark,
    onPrimaryContainer = SurfaceWhite,
    secondary = AccentBlue,
    onSecondary = SurfaceWhite,
    tertiary = AccentPink,
    onTertiary = SurfaceWhite,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceMuted,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkDivider,
    error = DangerRed,
    onError = SurfaceWhite
)

/**
 * Tema de la app. Lee las preferencias de [AppSettings] para aplicar el modo
 * oscuro y el escalado de texto de forma inmediata en toda la interfaz.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = AppSettings.modoOscuro,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val base = LocalDensity.current
    val densidad = Density(density = base.density, fontScale = base.fontScale * AppSettings.escalaTexto)

    CompositionLocalProvider(LocalDensity provides densidad) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
