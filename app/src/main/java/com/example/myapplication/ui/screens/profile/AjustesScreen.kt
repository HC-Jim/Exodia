package com.example.myapplication.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.myapplication.core.utils.AppSettings
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

/**
 * Pantalla de accesibilidad/ajustes. Escribe en [AppSettings], por lo que el
 * modo oscuro y el tamaño de letra se aplican de inmediato en toda la app.
 */
@Composable
fun AjustesScreen(
    modifier: Modifier = Modifier,
    onSoporte: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(titulo = "Configuración")

        Text(
            "ACCESIBILIDAD",
            modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextSecondary
        )

        FilaSwitch(
            icono = if (AppSettings.modoOscuro) Icons.Filled.DarkMode else Icons.Filled.LightMode,
            titulo = "Activar modo oscuro",
            valor = AppSettings.modoOscuro,
            onCambio = { AppSettings.modoOscuro = it }
        )

        // Tamaño de letra
        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.TextFields, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(14.dp))
                Text("Tamaño de letra", color = TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text("${(AppSettings.escalaTexto * 100).toInt()}%", color = TextSecondary, fontSize = 13.sp)
            }
            Slider(
                value = AppSettings.escalaTexto,
                onValueChange = { AppSettings.escalaTexto = it },
                valueRange = 0.85f..1.30f,
                steps = 8
            )
        }

        FilaSwitch(
            icono = Icons.Filled.ScreenLockPortrait,
            titulo = "Mantener pantalla encendida",
            valor = AppSettings.mantenerPantalla,
            onCambio = { AppSettings.mantenerPantalla = it }
        )
        FilaSwitch(
            icono = Icons.Filled.NotificationsOff,
            titulo = "Silenciar notificaciones",
            valor = AppSettings.silenciarNotificaciones,
            onCambio = { AppSettings.silenciarNotificaciones = it }
        )

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onSoporte,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SolidColor(IndigoPrimary))
            ) {
                Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text("Contactar a soporte", color = IndigoPrimary, fontWeight = FontWeight.SemiBold)
            }
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
    }
}

@Composable
private fun FilaSwitch(icono: ImageVector, titulo: String, valor: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(14.dp))
        Text(titulo, color = TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = valor,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(checkedTrackColor = IndigoPrimary)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AjustesPreview() {
    MyApplicationTheme {
        AjustesScreen()
    }
}
