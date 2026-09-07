package com.example.myapplication.conductor.ui

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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.conductor.model.MockConductor
import com.example.myapplication.conductor.ui.componentes.ChipAzul
import com.example.myapplication.conductor.ui.componentes.ChipRosa
import com.example.myapplication.conductor.ui.componentes.ChipsAlumnos
import com.example.myapplication.conductor.ui.componentes.EncabezadoConductor
import com.example.myapplication.conductor.ui.componentes.InicialesAvatar
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun ConfiguracionConductorScreen(
    onLlamar: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val perfil = MockConductor.perfil
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(
            titulo = "Conductor",
            accionIcono = Icons.Filled.Settings,
            accionDescripcion = "Configuración",
            onAccion = {}
        )
        Spacer(Modifier.height(4.dp))
        ChipsAlumnos(
            nombres = listOf(
                "Julio Zuñiga" to ChipAzul,
                "María Zuñiga" to ChipRosa
            )
        )
        Spacer(Modifier.height(20.dp))

        InicialesAvatar(
            nombre = perfil.nombre,
            tamano = 110.dp,
            colorFondo = IndigoPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            perfil.nombre,
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
            FilaDato(Icons.Filled.School, "Grado ${perfil.gradoAsignado}")
            FilaDato(Icons.Filled.DirectionsBus, perfil.movilidad)
            FilaDato(Icons.Filled.Place, perfil.paradero)
        }

        Spacer(Modifier.height(24.dp))
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
                    Text(perfil.contactoNombre, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(perfil.contactoRol, color = TextSecondary, fontSize = 13.sp)
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

@Composable
private fun FilaDato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(14.dp))
        Text(texto, color = TextPrimary, fontSize = 15.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfiguracionConductorPreview() {
    MyApplicationTheme {
        ConfiguracionConductorScreen(onLlamar = {}, onCerrarSesion = {})
    }
}
