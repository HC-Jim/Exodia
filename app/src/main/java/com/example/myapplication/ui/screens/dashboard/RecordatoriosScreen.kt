package com.example.myapplication.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.entities.Recordatorio
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

/**
 * Pantalla de recordatorios personales del estudiante.
 * CRUD 100% local con SQLite (agregar / listar / borrar).
 */
@Composable
fun RecordatoriosScreen(
    modifier: Modifier = Modifier,
    onRetroceder: (() -> Unit)? = null,
    viewModel: RecordatoriosViewModel = viewModel()
) {
    var titulo by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(
            titulo = "Recordatorios",
            accionIcono = if (onRetroceder != null) Icons.AutoMirrored.Filled.ArrowBack else null,
            accionDescripcion = "Volver",
            onAccion = onRetroceder
        )

        // --- Formulario para agregar ---
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Título") }
            )
            OutlinedTextField(
                value = detalle,
                onValueChange = { detalle = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                label = { Text("Detalle (opcional)") }
            )
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Fecha / hora (opcional)") }
            )
            Button(
                onClick = {
                    viewModel.agregar(titulo, detalle, fecha)
                    titulo = ""
                    detalle = ""
                    fecha = ""
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Agregar recordatorio", fontWeight = FontWeight.SemiBold)
            }
        }

        // --- Lista de recordatorios ---
        if (viewModel.recordatorios.isEmpty()) {
            Text(
                "Aún no tienes recordatorios.",
                modifier = Modifier.padding(20.dp),
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(viewModel.recordatorios, key = { it.id }) { recordatorio ->
                    FilaRecordatorio(recordatorio, onBorrar = { viewModel.borrar(recordatorio.id) })
                }
            }
        }
    }
}

@Composable
private fun FilaRecordatorio(recordatorio: Recordatorio, onBorrar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.EditNote, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(recordatorio.titulo, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            if (recordatorio.detalle.isNotBlank()) {
                Text(recordatorio.detalle, color = TextSecondary, fontSize = 13.sp)
            }
            if (recordatorio.fecha.isNotBlank()) {
                Text(recordatorio.fecha, color = IndigoPrimary, fontSize = 12.sp)
            }
        }
        IconButton(onClick = onBorrar) {
            Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = TextSecondary)
        }
    }
}
