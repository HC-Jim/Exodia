package com.example.myapplication.ui.screens.profile

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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.entities.ContactoEmergencia
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun ConfiguracionApoderadoScreen(
    modifier: Modifier = Modifier,
    onGuardar: () -> Unit = {},
    onCancelar: () -> Unit = {},
    onCambiarContrasena: () -> Unit = {},
    onContactoEmergencia: () -> Unit = {},
    // Contactos de emergencia guardados localmente en SQLite.
    contactosVM: ContactosViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("Marco Zuñiga") }
    var celular by remember { mutableStateOf("(+51) 987 654 121") }
    var correo by remember { mutableStateOf("marco.zuniga@email.com") }
    var direccion by remember { mutableStateOf("Av. Principal 128") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(titulo = "Configuración")

        // Avatar + cambiar foto
        Box(Modifier.align(Alignment.CenterHorizontally)) {
            InicialesAvatar(nombre = nombre, tamano = 96.dp, colorFondo = IndigoPrimary)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(AccentBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("Cambiar foto", modifier = Modifier.align(Alignment.CenterHorizontally), color = AccentBlue, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp))

        Seccion("Información personal (modificable)")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Campo("Nombre completo", nombre) { nombre = it }
            Campo("Celular", celular) { celular = it }
            Campo("Correo", correo) { correo = it }
            Campo("Dirección", direccion) { direccion = it }
        }

        Spacer(Modifier.height(16.dp))
        Seccion("Datos institucionales (solo lectura)")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaLectura("Grado del alumno", "5° Primaria")
                FilaLectura("Movilidad asignada", "Movilidad N°04")
            }
        }

        Spacer(Modifier.height(16.dp))
        Seccion("Seguridad")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onCambiarContrasena,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Cambiar contraseña", color = TextPrimary)
            }
        }

        Spacer(Modifier.height(16.dp))
        Seccion("Contactos de emergencia (SQLite)")
        SeccionContactos(contactosVM)

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Cancelar", color = TextSecondary) }
            Button(
                onClick = onGuardar,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) { Text("Guardar cambios", fontWeight = FontWeight.SemiBold) }
        }
        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Sección de contactos de emergencia: escribe y lee de SQLite mediante
 * [ContactosViewModel]. Demuestra el CRUD local (agregar, listar, borrar).
 */
@Composable
private fun SeccionContactos(vm: ContactosViewModel) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

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
            vm.contactos.forEach { contacto ->
                FilaContacto(contacto, onBorrar = { vm.borrar(contacto.id) })
            }
        }
    }
}

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

@Composable
private fun Seccion(texto: String) {
    Text(
        texto.uppercase(),
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = TextSecondary
    )
}

@Composable
private fun Campo(etiqueta: String, valor: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        label = { Text(etiqueta) }
    )
}

@Composable
private fun FilaLectura(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(valor, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfiguracionApoderadoPreview() {
    MyApplicationTheme {
        ConfiguracionApoderadoScreen()
    }
}
