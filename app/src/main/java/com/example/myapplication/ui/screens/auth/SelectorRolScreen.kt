package com.example.myapplication.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

/** Rol de usuario elegido en la pantalla de entrada. */
enum class Rol { NINGUNO, CONDUCTOR, APODERADO }

/**
 * Pantalla de selección de perfil. Sustituye temporalmente al login/registro
 * mientras no exista autenticación (Firebase Authentication, sección 6.2).
 */
@Composable
fun SelectorRolScreen(onRol: (Rol) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Transporte Escolar", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextPrimary)
        Text("Selecciona tu perfil", color = TextSecondary, fontSize = 15.sp)
        Spacer(Modifier.height(32.dp))
        TarjetaRol("Conductor", Icons.Filled.DirectionsBus) { onRol(Rol.CONDUCTOR) }
        Spacer(Modifier.height(16.dp))
        TarjetaRol("Apoderado", Icons.Filled.FamilyRestroom) { onRol(Rol.APODERADO) }
    }
}

@Composable
private fun TarjetaRol(texto: String, icono: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = RoundedCornerShape(20.dp),
        color = IndigoPrimary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            Spacer(Modifier.size(6.dp))
            Text(texto, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SelectorRolPreview() {
    MyApplicationTheme {
        SelectorRolScreen(onRol = {})
    }
}
