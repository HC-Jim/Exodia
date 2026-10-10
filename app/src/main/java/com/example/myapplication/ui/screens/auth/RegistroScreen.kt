package com.example.myapplication.ui.screens.auth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.Usuario
import com.example.myapplication.ui.components.EscudoColegio
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.SanAgustinGold
import com.example.myapplication.ui.theme.SanAgustinRed
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

// Movilidades válidas. Deben coincidir EXACTAMENTE con las del backend,
// porque el conductor filtra a sus estudiantes por este texto.
private val MOVILIDADES = listOf("Movilidad N°02", "Movilidad N°04")

private val PERMISOS_UBICACION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

/**
 * Pantalla de registro de cuenta. Crea un usuario con su estudiante, su
 * pregunta de seguridad y el punto de recojo (GPS).
 */
@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    // rememberSaveable: los datos sobreviven a la rotación de pantalla.
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var pregunta by rememberSaveable { mutableStateOf("") }
    var respuesta by rememberSaveable { mutableStateOf("") }
    var estudiante by rememberSaveable { mutableStateOf("") }
    var grado by rememberSaveable { mutableStateOf("") }
    var movilidad by rememberSaveable { mutableStateOf("") }
    var latitud by rememberSaveable { mutableStateOf("") }
    var longitud by rememberSaveable { mutableStateOf("") }

    var buscandoUbicacion by remember { mutableStateOf(false) }
    var errorUbicacion by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    fun pedirUbicacion() {
        buscandoUbicacion = true
        errorUbicacion = null
        obtenerUbicacion(
            context,
            onListo = { lat, lng ->
                latitud = lat.toString()
                longitud = lng.toString()
                buscandoUbicacion = false
            },
            onError = { msg ->
                errorUbicacion = msg
                buscandoUbicacion = false
            }
        )
    }

    // Pide FINE y COARSE juntos (obligatorio desde Android 12).
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos.values.any { it }) pedirUbicacion()
        else errorUbicacion = "Activa el permiso de ubicación para marcar el punto de recojo."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ---------- Encabezado ----------
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            EscudoColegio(tamano = 56.dp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Crear cuenta", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = SanAgustinRed)
                Text(
                    "Registra al estudiante y su punto de recojo",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        // ---------- Sección: cuenta ----------
        Seccion(icono = Icons.Outlined.Person, titulo = "Tu cuenta") {
            Campo("Nombre completo", nombre, Icons.Outlined.Person,
                capitalizar = true) { nombre = it }
            Campo("Correo", correo, Icons.Outlined.Email,
                tipoTeclado = KeyboardType.Email) { correo = it }
            CampoClave("Contraseña", contrasena) { contrasena = it }
        }

        // ---------- Sección: seguridad ----------
        Seccion(
            icono = Icons.Outlined.Shield,
            titulo = "Recuperación",
            descripcion = "Te la preguntaremos si olvidas tu contraseña."
        ) {
            Campo("Pregunta (ej. ¿Nombre de tu mascota?)", pregunta, Icons.Outlined.Quiz,
                capitalizar = true) { pregunta = it }
            Campo("Respuesta", respuesta, Icons.Outlined.Key) { respuesta = it }
        }

        // ---------- Sección: estudiante ----------
        Seccion(icono = Icons.Outlined.School, titulo = "Estudiante") {
            Campo("Nombre del estudiante", estudiante, Icons.Outlined.Face,
                capitalizar = true) { estudiante = it }
            Campo("Grado (ej. 3° B)", grado, Icons.Outlined.Class) { grado = it }
            SelectorMovilidad(movilidad) { movilidad = it }

            Spacer(Modifier.height(4.dp))
            TarjetaUbicacion(
                latitud = latitud,
                longitud = longitud,
                buscando = buscandoUbicacion,
                error = errorUbicacion,
                onClick = {
                    val tienePermiso = PERMISOS_UBICACION.any {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    }
                    if (tienePermiso) pedirUbicacion() else pedirPermiso.launch(PERMISOS_UBICACION)
                }
            )
        }

        // ---------- Error del registro ----------
        AnimatedVisibility(visible = viewModel.error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DangerRed.copy(alpha = 0.10f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = DangerRed)
                Spacer(Modifier.width(10.dp))
                Text(viewModel.error ?: "", color = DangerRed, fontSize = 13.sp)
            }
        }

        // ---------- Botón principal ----------
        Button(
            onClick = {
                val nuevo = Usuario(
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    rol = "ESTUDIANTE",
                    contrasena = contrasena,
                    pregunta = pregunta.trim(),
                    respuesta = respuesta.trim(),
                    estudianteNombre = estudiante.trim(),
                    estudianteGrado = grado.trim(),
                    movilidad = movilidad.ifBlank { null },
                    lat = latitud.toDoubleOrNull(),
                    lng = longitud.toDoubleOrNull()
                )
                viewModel.registrar(nuevo) { onRegistrado() }
            },
            enabled = !viewModel.cargando,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            if (viewModel.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
                Text("Creando cuenta…", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            } else {
                Text("Crear cuenta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes cuenta?", color = TextSecondary, fontSize = 13.sp)
            TextButton(onClick = onVolver) {
                Text("Inicia sesión", color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// =====================================================================
//  Componentes de la pantalla
// =====================================================================

/** Bloque del formulario: ícono de la sección, título y sus campos. */
@Composable
private fun Seccion(
    icono: ImageVector,
    titulo: String,
    descripcion: String? = null,
    contenido: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SanAgustinRed.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = SanAgustinRed, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
        }
        if (descripcion != null) {
            Text(
                descripcion,
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 42.dp, top = 2.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        contenido()
    }
}

/** Colores comunes de los campos: borde e ícono en el rojo del colegio al enfocar. */
@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SanAgustinRed,
    focusedLabelColor = SanAgustinRed,
    focusedLeadingIconColor = SanAgustinRed,
    cursorColor = SanAgustinRed,
    unfocusedBorderColor = TextSecondary.copy(alpha = 0.35f)
)

/** Campo de texto con ícono a la izquierda. */
@Composable
private fun Campo(
    etiqueta: String,
    valor: String,
    icono: ImageVector,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizar: Boolean = false,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        colors = coloresCampo(),
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            capitalization = if (capitalizar) KeyboardCapitalization.Sentences else KeyboardCapitalization.None,
            imeAction = ImeAction.Next
        )
    )
    Spacer(Modifier.height(10.dp))
}

/** Campo de contraseña con botón para mostrar u ocultar. */
@Composable
private fun CampoClave(etiqueta: String, valor: String, onChange: (String) -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = coloresCampo(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
    )
    Spacer(Modifier.height(6.dp))
}

/** Lista desplegable de movilidades (evita errores al escribir el nombre a mano). */
@Composable
private fun SelectorMovilidad(seleccion: String, onSeleccion: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            label = { Text("Movilidad") },
            placeholder = { Text("Elige la movilidad") },
            leadingIcon = { Icon(Icons.Outlined.DirectionsBus, contentDescription = null) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            colors = coloresCampo()
        )
        // Capa transparente encima del campo para abrir el menú al tocar.
        Box(
            Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(14.dp))
                .clickable { abierto = true }
        )
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            MOVILIDADES.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    leadingIcon = { Icon(Icons.Outlined.DirectionsBus, contentDescription = null) },
                    onClick = {
                        onSeleccion(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
    Spacer(Modifier.height(10.dp))
}

/**
 * Tarjeta del punto de recojo. Cambia de aspecto según el estado:
 * sin marcar, buscando, guardada o con error.
 */
@Composable
private fun TarjetaUbicacion(
    latitud: String,
    longitud: String,
    buscando: Boolean,
    error: String?,
    onClick: () -> Unit
) {
    val guardada = latitud.isNotBlank() && longitud.isNotBlank()
    val acento by animateColorAsState(
        when {
            error != null -> DangerRed
            guardada -> SuccessGreen
            else -> SanAgustinGold
        },
        label = "acentoUbicacion"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(acento.copy(alpha = 0.08f))
            .border(1.dp, acento.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .clickable(enabled = !buscando, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(acento.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            when {
                buscando -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp), color = acento, strokeWidth = 2.dp
                )
                guardada -> Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = acento)
                else -> Icon(Icons.Filled.MyLocation, contentDescription = null, tint = acento)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = when {
                    buscando -> "Buscando tu ubicación…"
                    guardada -> "Punto de recojo guardado"
                    else -> "Usar mi ubicación actual"
                },
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = when {
                    error != null -> error
                    guardada -> String.format(
                        Locale.US, "%.5f, %.5f · Toca para actualizar",
                        latitud.toDouble(), longitud.toDouble()
                    )
                    else -> "Marca dónde recogerá el bus al estudiante"
                },
                fontSize = 12.sp,
                color = if (error != null) DangerRed else TextSecondary
            )
        }
    }
    Spacer(Modifier.height(4.dp))
}

// Obtiene la ubicación actual del dispositivo (GPS) una sola vez.
private fun obtenerUbicacion(
    context: Context,
    onListo: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    try {
        fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { loc ->
                if (loc != null) onListo(loc.latitude, loc.longitude)
                else onError("No se pudo obtener la ubicación. Activa el GPS e inténtalo de nuevo.")
            }
            .addOnFailureListener { onError("Error al obtener la ubicación. Inténtalo de nuevo.") }
    } catch (e: SecurityException) {
        onError("Activa el permiso de ubicación para marcar el punto de recojo.")
    }
}
