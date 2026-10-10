package com.example.myapplication.ui.screens.comunicados

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.Comunicado
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.screens.recordatorios.RecordatoriosViewModel
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SanAgustinRed
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

// Detalle de un comunicado del colegio. Muestra los datos reales del comunicado tocado.
// Fecha, horario y lugar solo aparecen si el comunicado los trae.
@Composable
fun EventoDetalleScreen(
    comunicado: Comunicado,
    onRetroceder: () -> Unit,
    modifier: Modifier = Modifier,
    // "Guardar" lo agrega a los recordatorios personales (SQLite local).
    recordatoriosVM: RecordatoriosViewModel = viewModel()
) {
    var guardado by rememberSaveable(comunicado.id) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(
            titulo = "Comunicado",
            accionIcono = Icons.AutoMirrored.Filled.ArrowBack,
            accionDescripcion = "Volver",
            onAccion = onRetroceder
        )

        // Banner con la fecha corta del comunicado.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(SanAgustinRed, IndigoPrimary))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Campaign,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(52.dp)
            )
            if (comunicado.fecha != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(fechaCorta(comunicado.fecha), color = SanAgustinRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            comunicado.titulo,
            modifier = Modifier.padding(horizontal = 20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextPrimary
        )
        if (!comunicado.detalle.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                comunicado.detalle,
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }

        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (comunicado.fecha != null) {
                Detalle(Icons.Filled.CalendarMonth, "Fecha", fechaLarga(comunicado.fecha))
            }
            if (!comunicado.hora.isNullOrBlank()) {
                Detalle(Icons.Filled.Schedule, "Horario", comunicado.hora)
            }
            if (!comunicado.lugar.isNullOrBlank()) {
                Detalle(Icons.Filled.Place, "Lugar", comunicado.lugar)
            }
        }

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                val cuando = listOfNotNull(
                    comunicado.fecha?.let { fechaLarga(it) },
                    comunicado.hora?.takeIf { it.isNotBlank() }
                ).joinToString(" · ")
                recordatoriosVM.agregar(comunicado.titulo, comunicado.detalle ?: "", cuando)
                guardado = true
            },
            enabled = !guardado,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                disabledContainerColor = SuccessGreen,
                disabledContentColor = Color.White
            )
        ) {
            Icon(
                if (guardado) Icons.Filled.BookmarkAdded else Icons.Filled.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.size(6.dp))
            Text(
                if (guardado) "Guardado en recordatorios" else "Guardar en recordatorios",
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

// Fila de un dato del comunicado (ícono + etiqueta + valor): fecha, horario, lugar.
@Composable
private fun Detalle(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text(etiqueta, color = TextSecondary, fontSize = 12.sp)
            Text(valor, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EventoDetallePreview() {
    MyApplicationTheme {
        EventoDetalleScreen(
            comunicado = Comunicado(
                id = 1,
                titulo = "Reunión de padres",
                detalle = "Reunión general el viernes a las 6:00 pm.",
                fecha = "2026-09-20",
                hora = "6:00 pm",
                lugar = "Auditorio del colegio"
            ),
            onRetroceder = {}
        )
    }
}
