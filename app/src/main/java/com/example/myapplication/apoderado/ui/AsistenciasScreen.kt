package com.example.myapplication.apoderado.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.myapplication.apoderado.model.EstadoAsistencia
import com.example.myapplication.apoderado.model.MockApoderado
import com.example.myapplication.conductor.ui.componentes.EncabezadoConductor
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun AsistenciasScreen(
    modifier: Modifier = Modifier,
    onRetroceder: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(
            titulo = "Asistencias",
            accionIcono = if (onRetroceder != null) Icons.AutoMirrored.Filled.ArrowBack else null,
            accionDescripcion = "Volver",
            onAccion = onRetroceder
        )

        Text(
            MockApoderado.nombreMes,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Spacer(Modifier.size(16.dp))

        CalendarioMes(
            modifier = Modifier.padding(horizontal = 16.dp),
            offset = MockApoderado.offsetPrimerDia,
            dias = MockApoderado.diasDelMes,
            estados = MockApoderado.asistencia
        )

        Spacer(Modifier.size(20.dp))
        Leyenda()
    }
}

@Composable
private fun CalendarioMes(
    modifier: Modifier = Modifier,
    offset: Int,
    dias: Int,
    estados: Map<Int, EstadoAsistencia>
) {
    val cabecera = listOf("D", "L", "M", "M", "J", "V", "S")
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth()) {
                cabecera.forEach { d ->
                    Text(
                        d,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.size(6.dp))

            val totalCeldas = offset + dias
            val filas = (totalCeldas + 6) / 7
            var dia = 1
            for (fila in 0 until filas) {
                Row(Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val indice = fila * 7 + col
                        if (indice < offset || dia > dias) {
                            Box(Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            CeldaDia(dia, estados[dia])
                            dia++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.CeldaDia(dia: Int, estado: EstadoAsistencia?) {
    val color = estado?.color ?: Color.Transparent
    val esMarcado = estado != null && estado != EstadoAsistencia.SIN_CLASE
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (esMarcado) color.copy(alpha = 0.18f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                dia.toString(),
                color = if (esMarcado) color else TextPrimary,
                fontWeight = if (esMarcado) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun Leyenda() {
    val items = listOf(
        EstadoAsistencia.PRESENTE,
        EstadoAsistencia.TARDANZA,
        EstadoAsistencia.FALTA,
        EstadoAsistencia.JUSTIFICADO
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.chunked(2).forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                fila.forEach { estado ->
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(estado.color)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(estado.etiqueta, color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AsistenciasPreview() {
    MyApplicationTheme {
        AsistenciasScreen()
    }
}
