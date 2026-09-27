package com.example.myapplication.ui.screens.alumnos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.Alumno
import com.example.myapplication.domain.EstadoEntrega
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.example.myapplication.ui.theme.WarningAmber

// Listado de TODOS los estudiantes de la ruta con su estado actual de entrega.
@Composable
fun AlumnosEntregadosScreen(
    modifier: Modifier = Modifier,
    onRetroceder: (() -> Unit)? = null,
    // El ViewModel trae los estudiantes desde la API.
    viewModel: AlumnosViewModel = viewModel()
) {
    val alumnos = viewModel.alumnos
    val entregados = viewModel.entregados.size   // solo para el resumen del encabezado

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(
            titulo = "Listado Estudiantes",
            accionIcono = if (onRetroceder != null) Icons.AutoMirrored.Filled.ArrowBack else null,
            accionDescripcion = "Volver",
            onAccion = onRetroceder
        )

        // Resumen: cuántos estudiantes hay y cuántos ya fueron entregados.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Groups, contentDescription = null, tint = IndigoPrimary)
            Spacer(Modifier.size(8.dp))
            Column {
                Text("Estudiantes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text("${alumnos.size} en total · $entregados entregados", color = TextSecondary, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.size(8.dp))

        // Estados de la carga: mensaje mientras pide o si hubo error; si no, la lista.
        when {
            viewModel.cargando -> Text(
                "Cargando estudiantes…",
                modifier = Modifier.padding(20.dp),
                color = TextSecondary
            )
            viewModel.error != null -> Text(
                viewModel.error!!,
                modifier = Modifier.padding(20.dp),
                color = DangerRed
            )
            alumnos.isEmpty() -> Text(
                "No hay estudiantes registrados en esta movilidad.",
                modifier = Modifier.padding(20.dp),
                color = TextSecondary
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alumnos, key = { it.id }) { alumno ->
                    FilaAlumno(alumno)
                }
            }
        }
    }
}

// Fila de un estudiante: avatar, nombre/dirección y una etiqueta con su estado actual.
@Composable
private fun FilaAlumno(alumno: Alumno) {
    val estado = estadoVisual(alumno.estado)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InicialesAvatar(nombre = alumno.nombre, tamano = 42.dp)
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(alumno.nombre, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
            Text(
                buildString {
                    append(alumno.direccion.ifBlank { alumno.paradero })
                    if (alumno.horaEntrega != null) {
                        append(" · " + alumno.horaEntrega)
                    }
                },
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        // Estado por ícono + texto, no solo color (accesibilidad).
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(estado.color.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(estado.texto, color = estado.color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(4.dp))
            Icon(estado.icono, contentDescription = estado.texto, tint = estado.color, modifier = Modifier.size(16.dp))
        }
    }
}

// Cómo se muestra cada estado: etiqueta, color e ícono.
private data class EstadoVisual(val texto: String, val color: Color, val icono: ImageVector)

private fun estadoVisual(estado: EstadoEntrega): EstadoVisual = when (estado) {
    EstadoEntrega.ENTREGADO -> EstadoVisual("Entregado", SuccessGreen, Icons.Filled.CheckCircle)
    EstadoEntrega.ABORDO -> EstadoVisual("A bordo", AccentBlue, Icons.Filled.DirectionsBus)
    EstadoEntrega.CANCELADO -> EstadoVisual("Cancelado", DangerRed, Icons.Filled.Cancel)
    EstadoEntrega.PENDIENTE -> EstadoVisual("Pendiente", WarningAmber, Icons.Filled.Schedule)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlumnosEntregadosPreview() {
    MyApplicationTheme {
        AlumnosEntregadosScreen()
    }
}
