package com.example.myapplication.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.models.UsuarioDto
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.SanAgustinRed
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

/**
 * Pantalla de registro de cuenta. Crea un usuario con su estudiante y su
 * pregunta de seguridad (necesaria para recuperar la contraseña).
 */
@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var pregunta by remember { mutableStateOf("") }
    var respuesta by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("ESTUDIANTE") }
    var estudiante by remember { mutableStateOf("") }
    var grado by remember { mutableStateOf("") }
    var movilidad by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Crear cuenta", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = SanAgustinRed)
        Spacer(Modifier.height(16.dp))

        Campo("Nombre completo", nombre) { nombre = it }
        Campo("Correo", correo) { correo = it }
        Campo("Contraseña", contrasena, esClave = true) { contrasena = it }

        Spacer(Modifier.height(6.dp))
        Text("Rol", color = TextSecondary, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonRol("Estudiante", rol == "ESTUDIANTE", Modifier.weight(1f)) { rol = "ESTUDIANTE" }
            BotonRol("Conductor", rol == "CONDUCTOR", Modifier.weight(1f)) { rol = "CONDUCTOR" }
        }
        Spacer(Modifier.height(10.dp))

        Text("Pregunta de seguridad (para recuperar tu contraseña)", color = TextSecondary, fontSize = 13.sp)
        Campo("Pregunta (ej. ¿Nombre de tu mascota?)", pregunta) { pregunta = it }
        Campo("Respuesta", respuesta) { respuesta = it }

        if (rol == "ESTUDIANTE") {
            Spacer(Modifier.height(10.dp))
            Text("Datos del estudiante", color = TextSecondary, fontSize = 13.sp)
            Campo("Nombre del estudiante", estudiante) { estudiante = it }
            Campo("Grado", grado) { grado = it }
            Campo("Movilidad (ej. Movilidad N°04)", movilidad) { movilidad = it }
        }

        if (viewModel.error != null) {
            Spacer(Modifier.height(6.dp))
            Text(viewModel.error!!, color = DangerRed, fontSize = 13.sp)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val dto = UsuarioDto(
                    nombre = nombre,
                    correo = correo,
                    contrasena = contrasena,
                    pregunta = pregunta,
                    respuesta = respuesta,
                    rol = rol,
                    estudianteNombre = if (rol == "ESTUDIANTE") estudiante else null,
                    estudianteGrado = if (rol == "ESTUDIANTE") grado else null,
                    movilidad = movilidad.ifBlank { null }
                )
                viewModel.registrar(dto) { onRegistrado() }
            },
            enabled = !viewModel.cargando,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text(if (viewModel.cargando) "Creando…" else "Registrarme", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        TextButton(onClick = onVolver) { Text("Volver a iniciar sesión", color = TextSecondary) }
    }
}

@Composable
private fun Campo(etiqueta: String, valor: String, esClave: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        label = { Text(etiqueta) },
        visualTransformation = if (esClave) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
    )
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun BotonRol(texto: String, activo: Boolean, modifier: Modifier, onClick: () -> Unit) {
    if (activo) {
        Button(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) {
            Text(texto)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(12.dp)) {
            Text(texto, color = TextPrimary)
        }
    }
}
