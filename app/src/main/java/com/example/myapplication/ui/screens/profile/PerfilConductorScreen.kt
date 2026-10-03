package com.example.myapplication.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.utils.Sesion
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

// Perfil del conductor: datos personales editables (nube), datos del vehículo (solo lectura),
// contactos de emergencia locales (SQLite) y cerrar sesión.
@Composable
fun PerfilConductorScreen(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {},
    perfilVM: PerfilViewModel = viewModel()
) {
    val u = Sesion.usuario
    var verContrasena by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(titulo = "Perfil")

        InicialesAvatar(
            nombre = perfilVM.nombre,
            tamano = 100.dp,
            colorFondo = IndigoPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            perfilVM.nombre,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(20.dp))

        // ---- Datos personales editables (nube) ----
        Seccion("Información personal (modificable)")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Campo("Nombre completo", perfilVM.nombre) { perfilVM.nombre = it }
            Campo("Celular", perfilVM.celular) { perfilVM.celular = it }
            Campo("Correo", perfilVM.correo) { perfilVM.correo = it }
            CampoClave("Contraseña", perfilVM.contrasena, verContrasena,
                onCambio = { perfilVM.contrasena = it },
                onToggle = { verContrasena = !verContrasena })
        }

        if (perfilVM.mensaje != null) {
            Spacer(Modifier.height(8.dp))
            Text(perfilVM.mensaje!!, color = SuccessGreen, fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp))
        }

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
                onClick = { perfilVM.guardar(incluirCorreo = true) },
                enabled = !perfilVM.guardando,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) { Text(if (perfilVM.guardando) "Guardando…" else "Guardar cambios", fontWeight = FontWeight.SemiBold) }
        }

        // ---- Datos del vehículo / credenciales (solo lectura) ----
        Spacer(Modifier.height(18.dp))
        Seccion("Datos del conductor (solo lectura)")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Dato(Icons.Filled.Badge, "DNI", u?.dni ?: "—")
                Dato(Icons.Filled.MedicalServices, "Contacto emergencia", u?.contactoEmergencia ?: "—")
                Dato(Icons.Filled.CreditCard, "Licencia de conducir", u?.licencia ?: "—", valorColor = SuccessGreen)
                Dato(Icons.Filled.DirectionsCar, "Placa", u?.placa ?: "—", valorColor = SuccessGreen)
                Dato(Icons.Filled.Map, "Zona asignada", u?.zona ?: "—", valorColor = SuccessGreen)
            }
        }

        // ---- Contactos de emergencia (local, SQLite) ----
        Spacer(Modifier.height(20.dp))
        Seccion("Contactos de emergencia")
        SeccionContactosEmergencia()

        Spacer(Modifier.height(20.dp))
        Column(Modifier.padding(horizontal = 16.dp)) {
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SolidColor(DangerRed))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text("Cerrar sesión", color = DangerRed, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(24.dp))
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

@Composable
private fun Dato(icono: ImageVector, etiqueta: String, valor: String, valorColor: androidx.compose.ui.graphics.Color = TextPrimary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(etiqueta, color = TextSecondary, fontSize = 12.sp)
            Text(valor, color = valorColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PerfilConductorPreview() {
    MyApplicationTheme {
        PerfilConductorScreen()
    }
}
