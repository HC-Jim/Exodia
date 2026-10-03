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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.utils.Sesion
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

// Perfil del estudiante: datos personales reales (nube), contraseña editable,
// datos del colegio (solo lectura) y, abajo, los contactos de emergencia (local).
@Composable
fun ConfiguracionApoderadoScreen(
    modifier: Modifier = Modifier,
    perfilVM: PerfilViewModel = viewModel(),
    // Contactos de emergencia guardados localmente en SQLite.
    contactosVM: ContactosViewModel = viewModel()
) {
    var verContrasena by remember { mutableStateOf(false) }
    val usuario = Sesion.usuario

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(titulo = "Perfil")

        // Avatar
        Box(Modifier.align(Alignment.CenterHorizontally)) {
            InicialesAvatar(nombre = perfilVM.nombre, tamano = 96.dp, colorFondo = IndigoPrimary)
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
        Spacer(Modifier.height(18.dp))

        // ---- Datos personales (nube) ----
        Seccion("Información personal (modificable)")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Campo("Nombre completo", perfilVM.nombre) { perfilVM.nombre = it }
            Campo("Celular", perfilVM.celular) { perfilVM.celular = it }
            // El correo es el usuario de acceso: se muestra pero no se edita aquí.
            FilaLecturaCard("Correo", perfilVM.correo)
            // Contraseña: oculta por defecto, con botón para revelarla.
            CampoClave("Contraseña", perfilVM.contrasena, verContrasena,
                onCambio = { perfilVM.contrasena = it },
                onToggle = { verContrasena = !verContrasena })
        }

        if (perfilVM.mensaje != null) {
            Spacer(Modifier.height(8.dp))
            Text(perfilVM.mensaje!!, color = SuccessGreen, fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp))
        }

        // ---- Guardar / Cancelar (ARRIBA de contactos) ----
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { perfilVM.cancelar() },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Cancelar", color = TextSecondary) }
            Button(
                onClick = { perfilVM.guardar(incluirCorreo = false) },
                enabled = !perfilVM.guardando,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) { Text(if (perfilVM.guardando) "Guardando…" else "Guardar cambios", fontWeight = FontWeight.SemiBold) }
        }

        // ---- Datos del colegio (solo lectura) ----
        Spacer(Modifier.height(18.dp))
        Seccion("Datos institucionales (solo lectura)")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaLectura("Estudiante", usuario?.estudianteNombre ?: "-")
                FilaLectura("Grado del alumno", usuario?.estudianteGrado ?: "-")
                FilaLectura("Movilidad asignada", usuario?.movilidad ?: "-")
            }
        }

        // ---- Contactos de emergencia (local, abajo) ----
        Spacer(Modifier.height(18.dp))
        Seccion("Contactos de emergencia")
        SeccionContactosEmergencia(vm = contactosVM)

        Spacer(Modifier.height(24.dp))
    }
}

// Encabezado de sección en mayúsculas.
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

// Campo de texto editable reutilizable.
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

// Campo de contraseña con botón de ojo para revelar/ocultar.
@Composable
private fun CampoClave(
    etiqueta: String,
    valor: String,
    visible: Boolean,
    onCambio: (String) -> Unit,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        label = { Text(etiqueta) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        }
    )
}

// Fila de un dato de solo lectura (etiqueta a la izquierda, valor a la derecha).
@Composable
private fun FilaLectura(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(valor, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// Dato de solo lectura con aspecto de campo (para el correo).
@Composable
private fun FilaLecturaCard(etiqueta: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(etiqueta, color = TextSecondary, fontSize = 12.sp)
            Text(valor, color = TextPrimary, fontSize = 15.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfiguracionApoderadoPreview() {
    MyApplicationTheme {
        ConfiguracionApoderadoScreen()
    }
}
