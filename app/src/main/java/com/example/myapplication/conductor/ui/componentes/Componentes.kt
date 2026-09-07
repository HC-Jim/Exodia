package com.example.myapplication.conductor.ui.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.conductor.navigation.NavegadorConductor
import com.example.myapplication.conductor.navigation.TabConductor
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.AccentPink
import com.example.myapplication.ui.theme.IndigoLight
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.SurfaceMuted
import com.example.myapplication.ui.theme.TextSecondary

/** Avatar circular con las iniciales del nombre. Evita depender de imágenes de red. */
@Composable
fun InicialesAvatar(
    nombre: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 44.dp,
    colorFondo: Color = IndigoLight
) {
    val iniciales = nombre.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Box(
        modifier = modifier
            .size(tamano)
            .background(colorFondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (tamano.value * 0.38f).sp
        )
    }
}

/**
 * Mapa estilizado dibujado con Canvas para las pantallas de seguimiento.
 * Es un marcador de posición visual: el mapa real usará Google Maps SDK
 * (sección 4.1 del documento) una vez habilitada la API.
 */
@Composable
fun MapaSimulado(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(listOf(Color(0xFFE8EEF7), Color(0xFFDCE6F2)))
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val calle = Color(0xFFC5D2E0)

            // Cuadrícula de calles
            val paso = w / 5f
            var x = paso
            while (x < w) {
                drawLine(calle, Offset(x, 0f), Offset(x, h), strokeWidth = 10f)
                x += paso
            }
            var y = paso
            while (y < h) {
                drawLine(calle, Offset(0f, y), Offset(w, y), strokeWidth = 10f)
                y += paso
            }

            // Ruta trazada (línea índigo)
            val ruta = Color(0xFF6C5CE7)
            drawLine(
                ruta,
                Offset(w * 0.15f, h * 0.85f),
                Offset(w * 0.15f, h * 0.45f),
                strokeWidth = 16f,
                cap = StrokeCap.Round
            )
            drawLine(
                ruta,
                Offset(w * 0.15f, h * 0.45f),
                Offset(w * 0.6f, h * 0.45f),
                strokeWidth = 16f,
                cap = StrokeCap.Round
            )
            drawLine(
                ruta,
                Offset(w * 0.6f, h * 0.45f),
                Offset(w * 0.6f, h * 0.2f),
                strokeWidth = 16f,
                cap = StrokeCap.Round
            )

            // Punto de inicio
            drawCircle(Color.White, radius = 22f, center = Offset(w * 0.15f, h * 0.85f))
            drawCircle(ruta, radius = 22f, center = Offset(w * 0.15f, h * 0.85f), style = Stroke(width = 8f))
        }
    }
}

/** Barra de navegación inferior con los cuatro destinos principales. */
@Composable
fun BarraInferiorConductor(navegador: NavegadorConductor) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        TabConductor.entries.forEach { destino ->
            val seleccionado = navegador.tab == destino && navegador.overlay == null
            NavigationBarItem(
                selected = seleccionado,
                onClick = { navegador.seleccionarTab(destino) },
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = destino.icono,
                        contentDescription = destino.etiqueta,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(destino.etiqueta, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IndigoPrimary,
                    selectedTextColor = IndigoPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = SurfaceMuted
                )
            )
        }
    }
}

// Colores de apoyo reexpuestos para las pantallas.
val ChipAzul = AccentBlue
val ChipRosa = AccentPink
val VerdeEntregado = SuccessGreen
