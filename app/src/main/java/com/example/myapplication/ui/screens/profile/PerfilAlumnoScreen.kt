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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.utils.Sesion
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

// Perfil del estudiante que ve el apoderado: datos, accesos a notas/asistencias y contacto del conductor.
// esConfiguracion cambia el encabezado para reusar esta misma pantalla como "Configuración".
@Composable
fun PerfilAlumnoScreen(
    modifier: Modifier = Modifier,
    esConfiguracion: Boolean = false,
    onRetroceder: (() -> Unit)? = null,
    onNotas: () -> Unit = {},
    onAsistencias: () -> Unit = {},
    onLlamar: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    // Datos del estudiante desde la sesión.
    val u = Sesion.usuario
    val nombreEstudiante = u?.estudianteNombre ?: "Estudiante"
    val gradoEstudiante = u?.estudianteGrado ?: ""
    val movilidadEstudiante = u?.movilidad ?: ""

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(
            titulo = if (esConfiguracion) "Configuración" else "Alumno",
            accionIcono = when {
                esConfiguracion -> Icons.Filled.Settings
                onRetroceder != null -> Icons.AutoMirrored.Filled.ArrowBack
                else -> null
            },
            accionDescripcion = if (esConfiguracion) "Configuración" else "Volver",
            onAccion = if (esConfiguracion) ({}) else onRetroceder
        )
        Spacer(Modifier.height(20.dp))

        InicialesAvatar(
            nombre = nombreEstudiante,
            tamano = 110.dp,
            colorFondo = IndigoPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            nombreEstudiante,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FilaDato(Icons.Filled.School, "Grado $gradoEstudiante")
            FilaDato(Icons.Filled.DirectionsBus, movilidadEstudiante)
        }

        Spacer(Modifier.height(20.dp))
        // Accesos rápidos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccesoRapido("Notas", Icons.Filled.Grade, Modifier.weight(1f), onNotas)
            AccesoRapido("Asistencias", Icons.Filled.EventAvailable, Modifier.weight(1f), onAsistencias)
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Contacto conductor",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Phone, contentDescription = null, tint = SuccessGreen)
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Conductor asignado", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(movilidadEstudiante, color = TextSecondary, fontSize = 13.sp)
                }
                TextButton(onClick = onLlamar) {
                    Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Llamar")
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SolidColor(DangerRed))
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text("Cerrar sesión", color = DangerRed, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(24.dp))
    }
}

// Fila simple de un dato del estudiante (ícono + texto).
@Composable
private fun FilaDato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(14.dp))
        Text(texto, color = TextPrimary, fontSize = 15.sp)
    }
}

// Botón de acceso rápido (tarjeta con ícono y texto) a una sección del estudiante.
@Composable
private fun AccesoRapido(texto: String, icono: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(10.dp))
            Text(texto, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PerfilAlumnoPreview() {
    MyApplicationTheme {
        PerfilAlumnoScreen(onRetroceder = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Configuración")
@Composable
private fun ConfiguracionApoderadoPreview() {
    MyApplicationTheme {
        PerfilAlumnoScreen(esConfiguracion = true)
    }
}
