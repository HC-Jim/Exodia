package com.example.myapplication.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.entities.Alumno
import com.example.myapplication.domain.entities.RegistroHistorial
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.SuccessGreenBg
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.example.myapplication.ui.theme.WarningAmber

@Composable
fun AlumnosEntregadosScreen(
    modifier: Modifier = Modifier,
    onRetroceder: (() -> Unit)? = null,
    // El ViewModel trae los alumnos desde la API; "entregados" ya viene filtrado.
    viewModel: AlumnosViewModel = viewModel()
) {
    val entregados = viewModel.entregados

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
            Spacer(Modifier.size(8.dp))
            Column {
                Text("Alumnos Entregados", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text("${entregados.size} completados", color = TextSecondary, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.size(8.dp))

        // Cola offline: cuántos cambios faltan enviar + botón para sincronizar.
        BannerSincronizar(
            pendientes = viewModel.pendientes,
            mensaje = viewModel.mensaje,
            onSincronizar = { viewModel.sincronizar() }
        )
        Spacer(Modifier.size(8.dp))

        // LazyColumn con claves estables (sección 6.5 — rendimiento del documento)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(entregados, key = { it.id }) { alumno ->
                FilaAlumnoEntregado(alumno)
            }

            // Historial local de entregas (guardado en SQLite).
            if (viewModel.historial.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Historial de entregas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    }
                }
                items(viewModel.historial, key = { "h" + it.id }) { registro ->
                    FilaHistorial(registro)
                }
            }
        }
    }
}

@Composable
private fun BannerSincronizar(pendientes: Int, mensaje: String?, onSincronizar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (pendientes > 0) WarningAmber.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Sync, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                if (pendientes > 0) "$pendientes cambio(s) sin enviar" else "Todo sincronizado",
                color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium
            )
            if (mensaje != null) {
                Text(mensaje, color = TextSecondary, fontSize = 12.sp)
            }
        }
        Button(
            onClick = onSincronizar,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Text("Sincronizar", fontSize = 13.sp)
        }
    }
}

@Composable
private fun FilaHistorial(registro: RegistroHistorial) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(registro.alumnoNombre, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(registro.fecha, color = TextSecondary, fontSize = 12.sp)
        }
        Text(registro.estado, color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FilaAlumnoEntregado(alumno: Alumno) {
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
                    append(alumno.direccion)
                    alumno.horaEntrega?.let { append(" · $it") }
                },
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        // Estado por ícono + texto, no solo color (accesibilidad, sección 6.4)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SuccessGreenBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Entregado", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(4.dp))
            Icon(Icons.Filled.CheckCircle, contentDescription = "Entregado", tint = SuccessGreen, modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlumnosEntregadosPreview() {
    MyApplicationTheme {
        AlumnosEntregadosScreen()
    }
}
