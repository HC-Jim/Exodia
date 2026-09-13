package com.example.myapplication.ui.screens.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.repositories.MockApoderado
import com.example.myapplication.domain.entities.PasoRuta
import com.example.myapplication.ui.components.ChipsHijos
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.components.MapaSimulado
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.example.myapplication.ui.theme.WarningAmber
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun SeguimientoApoderadoScreen(
    modifier: Modifier = Modifier,
    viewModel: UbicacionViewModel = viewModel()
) {
    // Movilidad del hijo: identifica el bus que se va a seguir.
    val movilidad = MockApoderado.julio.movilidad

    // Pregunta al servidor la posición del bus cada 4 segundos.
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.refrescar(movilidad)
            kotlinx.coroutines.delay(4000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Saludo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InicialesAvatar(nombre = MockApoderado.nombrePadre, tamano = 42.dp)
            Spacer(Modifier.size(12.dp))
            Column {
                Text("¡Hola!", color = TextSecondary, fontSize = 12.sp)
                Text(MockApoderado.nombrePadre, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        ChipsHijos(hijos = MockApoderado.hijos)
        Spacer(Modifier.size(12.dp))

        // Mapa real de Google con el marcador del bus
        val ubic = viewModel.ubicacion
        val posicionInicial = LatLng(-12.046374, -77.042793) // Lima (mientras llega la 1ª posición)
        val camara = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(posicionInicial, 14f)
        }
        // Cuando llega una posición nueva del bus, centra la cámara ahí.
        LaunchedEffect(ubic) {
            if (ubic != null) {
                camara.position = CameraPosition.fromLatLngZoom(LatLng(ubic.lat, ubic.lng), 15f)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camara
            ) {
                if (ubic != null) {
                    Marker(
                        state = MarkerState(position = LatLng(ubic.lat, ubic.lng)),
                        title = "Bus ${ubic.movilidad}"
                    )
                }
            }
        }

        Spacer(Modifier.size(16.dp))
        // Línea de tiempo del recorrido
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Estado del recorrido", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                Spacer(Modifier.size(12.dp))
                MockApoderado.pasosRuta.forEachIndexed { index, paso ->
                    PasoLinea(paso, esUltimo = index == MockApoderado.pasosRuta.lastIndex)
                }
            }
        }
    }
}

@Composable
private fun PasoLinea(paso: PasoRuta, esUltimo: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Indicador + línea vertical
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (paso.completado) SuccessGreen else IndigoPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (paso.completado) {
                    Icon(Icons.Filled.Check, contentDescription = "Completado", tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary)
                    )
                }
            }
            if (!esUltimo) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(if (paso.completado) SuccessGreen else Color(0xFFE0E3EC))
                )
            }
        }
        Spacer(Modifier.size(14.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(bottom = if (esUltimo) 0.dp else 6.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(paso.titulo, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text(paso.hora, color = TextSecondary, fontSize = 13.sp)
            }
            Text(paso.detalle, color = TextSecondary, fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SeguimientoApoderadoPreview() {
    MyApplicationTheme {
        SeguimientoApoderadoScreen()
    }
}
