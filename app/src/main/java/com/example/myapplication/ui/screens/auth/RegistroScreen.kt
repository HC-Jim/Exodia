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
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.Usuario
import com.example.myapplication.ui.theme.SuccessGreen
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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
    // Un estado por cada campo del formulario (datos de cuenta, seguridad y del estudiante).
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var pregunta by remember { mutableStateOf("") }
    var respuesta by remember { mutableStateOf("") }
    var estudiante by remember { mutableStateOf("") }
    var grado by remember { mutableStateOf("") }
    var movilidad by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }   // punto de recojo (se llena con el GPS)
    var longitud by remember { mutableStateOf("") }

    val context = LocalContext.current
    // Al conceder el permiso, obtiene la ubicación y la guarda.
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            obtenerUbicacion(context) { lat, lng ->
                latitud = lat.toString()
                longitud = lng.toString()
            }
        }
    }

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
        Text("Pregunta de seguridad (para recuperar tu contraseña)", color = TextSecondary, fontSize = 13.sp)
        Campo("Pregunta (ej. ¿Nombre de tu mascota?)", pregunta) { pregunta = it }
        Campo("Respuesta", respuesta) { respuesta = it }

        Spacer(Modifier.height(10.dp))
        Text("Datos del estudiante", color = TextSecondary, fontSize = 13.sp)
        Campo("Nombre del estudiante", estudiante) { estudiante = it }
        Campo("Grado", grado) { grado = it }
        Campo("Movilidad (ej. Movilidad N°04)", movilidad) { movilidad = it }

        Text("Punto de recojo del estudiante", color = TextSecondary, fontSize = 13.sp)
        OutlinedButton(
            onClick = {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (tienePermiso) {
                    obtenerUbicacion(context) { lat, lng ->
                        latitud = lat.toString()
                        longitud = lng.toString()
                    }
                } else {
                    pedirPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Usar mi ubicación actual")
        }
        if (latitud.isNotBlank() && longitud.isNotBlank()) {
            Text("Ubicación guardada: $latitud, $longitud", color = SuccessGreen, fontSize = 12.sp)
        } else {
            Text("Aún no has marcado tu ubicación", color = TextSecondary, fontSize = 12.sp)
        }

        if (viewModel.error != null) {
            Spacer(Modifier.height(6.dp))
            Text(viewModel.error!!, color = DangerRed, fontSize = 13.sp)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                // Junta todos los campos en un DTO y lo manda al ViewModel para registrar.
                val nuevo = Usuario(
                    nombre = nombre,
                    correo = correo,
                    rol = "ESTUDIANTE",
                    contrasena = contrasena,
                    pregunta = pregunta,
                    respuesta = respuesta,
                    estudianteNombre = estudiante,
                    estudianteGrado = grado,
                    movilidad = movilidad.ifBlank { null },
                    lat = latitud.toDoubleOrNull(),
                    lng = longitud.toDoubleOrNull()
                )
                viewModel.registrar(nuevo) { onRegistrado() }
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

// Campo de texto reutilizable para el formulario; esClave = true oculta el texto (contraseña).
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

// Obtiene la ubicación actual del dispositivo (GPS) una sola vez.
private fun obtenerUbicacion(context: Context, onListo: (Double, Double) -> Unit) {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    try {
        fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { loc ->
                if (loc != null) onListo(loc.latitude, loc.longitude)
            }
    } catch (e: SecurityException) {
        // sin permiso de ubicación
    }
}

