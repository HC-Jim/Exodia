package com.example.myapplication.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.ContactoEmergencia
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

/**
 * Sección reutilizable de contactos de emergencia guardados localmente en SQLite.
 * La usan tanto el apoderado como el conductor desde su perfil: agrega, lista y borra.
 */
@Composable
fun SeccionContactosEmergencia(
    modifier: Modifier = Modifier,
    vm: ContactosViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Column(modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // --- Formulario para agregar ---
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            label = { Text("Nombre del contacto") }
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            label = { Text("Teléfono") }
        )
        Button(
            onClick = {
                vm.agregar(nombre, telefono)
                nombre = ""      // limpiar el formulario tras guardar
                telefono = ""
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Icon(Icons.Filled.AddCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Guardar contacto", fontWeight = FontWeight.SemiBold)
        }

        // --- Lista de contactos guardados ---
        if (vm.contactos.isEmpty()) {
            Text("Aún no hay contactos guardados.", color = TextSecondary, fontSize = 13.sp)
        } else {
            for (contacto in vm.contactos) {
                FilaContacto(contacto, onBorrar = { vm.borrar(contacto.id) })
            }
        }
    }
}

// Fila de un contacto de emergencia guardado, con su botón de borrar.
@Composable
private fun FilaContacto(contacto: ContactoEmergencia, onBorrar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 14.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Phone, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(contacto.nombre, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (contacto.telefono.isNotBlank()) {
                Text(contacto.telefono, color = TextSecondary, fontSize = 13.sp)
            }
        }
        IconButton(onClick = onBorrar) {
            Icon(Icons.Filled.Delete, contentDescription = "Borrar contacto", tint = TextSecondary)
        }
    }
}
