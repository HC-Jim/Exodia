package com.example.myapplication.ui.screens.secure

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.components.ChipAzul
import com.example.myapplication.ui.components.ChipRosa
import com.example.myapplication.ui.components.ChipsAlumnos
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import kotlin.random.Random

@Composable
fun EscanearQRScreen(
    onRetroceder: () -> Unit,
    nombreAlumno: String = "Julio Zuñiga",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(
            titulo = "Conductor",
            accionIcono = Icons.AutoMirrored.Filled.ArrowBack,
            accionDescripcion = "Volver",
            onAccion = onRetroceder
        )
        Spacer(Modifier.height(4.dp))
        ChipsAlumnos(
            nombres = listOf(
                "Julio Zuñiga" to ChipAzul,
                "María Zuñiga" to ChipRosa
            )
        )

        Spacer(Modifier.height(28.dp))
        Text(
            "Escanea QR",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(20.dp))

        // Visor de cámara simulado con marco de escaneo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1B1B24)),
            contentAlignment = Alignment.Center
        ) {
            VisorQR(Modifier.fillMaxSize())
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Alumno: $nombreAlumno",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = TextSecondary,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun VisorQR(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val lado = size.minDimension * 0.55f
        val left = (size.width - lado) / 2f
        val top = (size.height - lado) / 2f

        // Código QR simulado (celdas pseudoaleatorias deterministas)
        val celdas = 11
        val paso = lado / celdas
        val rnd = Random(7)
        drawRoundRect(
            color = Color.White,
            topLeft = androidx.compose.ui.geometry.Offset(left - 12f, top - 12f),
            size = androidx.compose.ui.geometry.Size(lado + 24f, lado + 24f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
        )
        for (fila in 0 until celdas) {
            for (col in 0 until celdas) {
                val borde = fila < 3 && col < 3 ||
                        fila < 3 && col >= celdas - 3 ||
                        fila >= celdas - 3 && col < 3
                if (borde || rnd.nextBoolean()) {
                    drawRect(
                        color = Color(0xFF14141C),
                        topLeft = androidx.compose.ui.geometry.Offset(left + col * paso, top + fila * paso),
                        size = androidx.compose.ui.geometry.Size(paso * 0.9f, paso * 0.9f)
                    )
                }
            }
        }

        // Esquinas del marco de enfoque
        val m = size.minDimension * 0.72f
        val ml = (size.width - m) / 2f
        val mt = (size.height - m) / 2f
        val brazo = m * 0.14f
        val grosor = 8f
        val esquina = Color(0xFF6C5CE7)
        fun esquinaL(x: Float, y: Float, dx: Int, dy: Int) {
            drawLine(esquina, androidx.compose.ui.geometry.Offset(x, y), androidx.compose.ui.geometry.Offset(x + dx * brazo, y), grosor)
            drawLine(esquina, androidx.compose.ui.geometry.Offset(x, y), androidx.compose.ui.geometry.Offset(x, y + dy * brazo), grosor)
        }
        esquinaL(ml, mt, 1, 1)
        esquinaL(ml + m, mt, -1, 1)
        esquinaL(ml, mt + m, 1, -1)
        esquinaL(ml + m, mt + m, -1, -1)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EscanearQRPreview() {
    MyApplicationTheme {
        EscanearQRScreen(onRetroceder = {})
    }
}
