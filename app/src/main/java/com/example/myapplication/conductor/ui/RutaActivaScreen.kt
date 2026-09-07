package com.example.myapplication.conductor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.conductor.model.MockConductor
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.conductor.ui.componentes.MapaSimulado
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.example.myapplication.ui.theme.WarningAmber

@Composable
fun RutaActivaScreen(
    onVerLista: () -> Unit,
    onEscanearQR: () -> Unit,
    modifier: Modifier = Modifier
) {
    val proxima = MockConductor.proximaEntrega
    Box(modifier = modifier.fillMaxSize()) {
        MapaSimulado(Modifier.fillMaxSize())

        // Marcador del bus
        MarcadorMapa(
            texto = "🚌",
            fondo = WarningAmber,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 40.dp)
        )
        // Marcadores de alumnos
        MarcadorMapa("A", IndigoPrimary, Modifier.align(Alignment.TopStart).padding(start = 60.dp, top = 120.dp))
        MarcadorMapa("B", IndigoPrimary, Modifier.align(Alignment.TopEnd).padding(end = 70.dp, top = 160.dp))
        MarcadorMapa("C", IndigoPrimary, Modifier.align(Alignment.CenterEnd).padding(end = 40.dp))

        // Cabecera superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Text(
                    "Conductor",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(Modifier.weight(1f))
            Surface(
                onClick = onVerLista,
                shape = RoundedCornerShape(12.dp),
                color = IndigoPrimary,
                shadowElevation = 3.dp
            ) {
                Text(
                    "Ver Lista (${MockConductor.totalAlumnos})",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        // Botón de recentrar
        Surface(
            onClick = {},
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, bottom = 180.dp)
                .size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Centrar mapa", tint = IndigoPrimary)
            }
        }

        // Tarjeta de próxima entrega
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Place, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Próxima Entrega", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(6.dp))
                Text(proxima.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text(proxima.direccion, color = TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onEscanearQR,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Escanear QR", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MarcadorMapa(texto: String, fondo: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        if (texto == "🚌") {
            Icon(Icons.Filled.DirectionsBus, contentDescription = "Bus", tint = Color.White, modifier = Modifier.size(22.dp))
        } else {
            Text(texto, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RutaActivaPreview() {
    MyApplicationTheme {
        RutaActivaScreen(onVerLista = {}, onEscanearQR = {})
    }
}
