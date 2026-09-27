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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.example.myapplication.domain.ConductorPerfil
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

// Perfil del conductor: sus datos personales y del vehículo, más los contactos de emergencia
// guardados localmente (SQLite) y la opción de cerrar sesión.
@Composable
fun PerfilConductorScreen(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {}
) {
    // Perfil del conductor tomado del usuario que inició sesión.
    val u = Sesion.usuario
    val p = ConductorPerfil(
        nombre = u?.nombre ?: "Conductor",
        celular = u?.celular ?: "—",
        correo = u?.correo ?: "—",
        contactoEmergencia = u?.contactoEmergencia ?: "—",
        dni = u?.dni ?: "—",
        licencia = u?.licencia ?: "—",
        placa = u?.placa ?: "—",
        zona = u?.zona ?: "—"
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(titulo = "Perfil")

        InicialesAvatar(
            nombre = p.nombre,
            tamano = 100.dp,
            colorFondo = IndigoPrimary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            p.nombre,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Dato(Icons.Filled.LocalPhone, "Celular", p.celular)
                Dato(Icons.Filled.Email, "Correo", p.correo)
                Dato(Icons.Filled.MedicalServices, "Contacto emergencia", p.contactoEmergencia)
                Dato(Icons.Filled.Badge, "DNI", p.dni)
            }
        }

        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Dato(Icons.Filled.CreditCard, "Licencia de conducir", p.licencia, valorColor = SuccessGreen)
                Dato(Icons.Filled.DirectionsCar, "Placa", p.placa, valorColor = SuccessGreen)
                Dato(Icons.Filled.Map, "Zona asignada", p.zona, valorColor = SuccessGreen)
            }
        }

        Spacer(Modifier.height(20.dp))
        // Contactos de emergencia guardados localmente en SQLite (agregar / listar / borrar).
        Text(
            "CONTACTOS DE EMERGENCIA",
            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = TextSecondary
        )
        SeccionContactosEmergencia()

        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

// Fila de un dato del perfil: ícono + etiqueta + valor (con color opcional para destacarlo).
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
