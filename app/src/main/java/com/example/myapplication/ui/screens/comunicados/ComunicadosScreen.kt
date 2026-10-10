package com.example.myapplication.ui.screens.comunicados

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.Comunicado
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Pantalla de comunicados del colegio: semana actual y lista de comunicados.
// Al tocar un día se muestran solo los comunicados de ese día; al tocarlo otra vez, todos.
@Composable
fun ComunicadosScreen(
    modifier: Modifier = Modifier,
    onComunicadoClick: (Comunicado) -> Unit = {},
    viewModel: ComunicadosViewModel = viewModel()
) {
    // Recarga cada vez que se entra a la pestaña (por si el colegio publicó algo nuevo).
    LaunchedEffect(Unit) { viewModel.cargar() }

    val semana = remember { semanaActual() }
    var diaSeleccionado by rememberSaveable { mutableStateOf<String?>(null) }   // "yyyy-MM-dd" o null

    val fechasConComunicados = viewModel.comunicados.mapNotNull { soloFecha(it.fecha) }.toSet()
    val lista = if (diaSeleccionado == null) viewModel.comunicados
    else viewModel.comunicados.filter { soloFecha(it.fecha) == diaSeleccionado }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(titulo = "Comunicados")

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Esta semana",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )
            if (diaSeleccionado != null) {
                TextButton(onClick = { diaSeleccionado = null }) {
                    Text("Ver todos", color = IndigoPrimary, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.size(8.dp))
        TiraSemanal(
            dias = semana,
            seleccionado = diaSeleccionado,
            fechasConComunicados = fechasConComunicados,
            onDiaClick = { fecha -> diaSeleccionado = if (diaSeleccionado == fecha) null else fecha }
        )
        Spacer(Modifier.size(16.dp))

        when {
            viewModel.cargando && viewModel.comunicados.isEmpty() -> Text(
                "Cargando comunicados…",
                modifier = Modifier.padding(20.dp),
                color = TextSecondary
            )
            viewModel.error != null -> Text(
                viewModel.error!!,
                modifier = Modifier.padding(20.dp),
                color = DangerRed
            )
            lista.isEmpty() -> Text(
                if (diaSeleccionado == null) "Todavía no hay comunicados."
                else "No hay comunicados para este día.",
                modifier = Modifier.padding(20.dp),
                color = TextSecondary
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(lista, key = { it.id }) { com ->
                    TarjetaComunicado(com, onClick = { onComunicadoClick(com) })
                }
            }
        }
    }
}

// Un día de la tira semanal.
private data class DiaSemana(val letra: String, val numero: Int, val fecha: String, val esHoy: Boolean)

// Construye los 7 días (lunes a domingo) de la semana actual.
private fun semanaActual(): List<DiaSemana> {
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val hoy = formato.format(Calendar.getInstance().time)
    val cal = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }
    val letras = listOf("L", "M", "X", "J", "V", "S", "D")
    return letras.map { letra ->
        val fecha = formato.format(cal.time)
        val dia = DiaSemana(letra, cal.get(Calendar.DAY_OF_MONTH), fecha, fecha == hoy)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        dia
    }
}

// Tira horizontal de la semana. Hoy va con borde; el día elegido, relleno;
// un punto debajo indica que ese día tiene comunicados.
@Composable
private fun TiraSemanal(
    dias: List<DiaSemana>,
    seleccionado: String?,
    fechasConComunicados: Set<String>,
    onDiaClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (dia in dias) {
            val activo = dia.fecha == seleccionado
            val fondo = when {
                activo -> IndigoPrimary
                dia.esHoy -> IndigoPrimary.copy(alpha = 0.12f)
                else -> Color.Transparent
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(fondo)
                    .clickable { onDiaClick(dia.fecha) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(dia.letra, color = if (activo) Color.White else TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.size(6.dp))
                Text(
                    dia.numero.toString(),
                    color = if (activo) Color.White else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.size(4.dp))
                Box(
                    Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                dia.fecha !in fechasConComunicados -> Color.Transparent
                                activo -> Color.White
                                else -> IndigoPrimary
                            }
                        )
                )
            }
        }
    }
}

// Tarjeta de un comunicado: ícono, título, resumen y fecha corta. Al tocarla abre el detalle.
@Composable
private fun TarjetaComunicado(comunicado: Comunicado, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Campaign, contentDescription = null, tint = color)
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(comunicado.titulo, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
            Text(
                comunicado.detalle ?: "",
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.size(8.dp))
        Text(fechaCorta(comunicado.fecha), color = color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

// =====================================================================
//  Utilidades de fecha (compatibles con minSdk 24, sin java.time)
//  El backend envía la fecha como "yyyy-MM-dd" (puede traer hora detrás).
// =====================================================================

private val LOCALE_ES: Locale = Locale.forLanguageTag("es-PE")

/** Deja solo la parte "yyyy-MM-dd" de la fecha, o null si no hay fecha. */
internal fun soloFecha(fecha: String?): String? =
    fecha?.takeIf { it.length >= 10 }?.substring(0, 10)

private fun parsear(fecha: String?) = try {
    soloFecha(fecha)?.let { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it) }
} catch (e: Exception) {
    null
}

/** "2026-09-20" → "20 sep" */
internal fun fechaCorta(fecha: String?): String {
    val d = parsear(fecha) ?: return fecha ?: ""
    return SimpleDateFormat("d MMM", LOCALE_ES).format(d).replace(".", "")
}

/** "2026-09-20" → "Domingo 20 de septiembre de 2026" */
internal fun fechaLarga(fecha: String?): String {
    val d = parsear(fecha) ?: return fecha ?: ""
    return SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", LOCALE_ES).format(d)
        .replaceFirstChar { it.uppercase() }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ComunicadosPreview() {
    MyApplicationTheme {
        ComunicadosScreen()
    }
}
