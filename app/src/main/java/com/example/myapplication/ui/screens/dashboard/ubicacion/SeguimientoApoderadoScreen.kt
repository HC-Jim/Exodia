package com.example.myapplication.ui.screens.dashboard.ubicacion

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.components.MapaSimulado
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.DangerRed
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.example.myapplication.core.utils.Sesion

// Seguimiento del bus para el apoderado: mapa con el estudiante y el bus, y el estado del recojo.
@Composable
fun SeguimientoApoderadoScreen(
    modifier: Modifier = Modifier,
    viewModel: UbicacionViewModel = viewModel()
) {
    // Un solo estudiante por usuario: se toma de la sesión iniciada.
    val usuario = Sesion.usuario
    val movilidad = usuario?.movilidad ?: ""        // bus del estudiante del usuario

    val usuarioId = usuario?.id

    // Pregunta la posición del bus y el estado del estudiante cada 4 s.
    LaunchedEffect(movilidad) {
        while (true) {
            if (movilidad.isNotBlank()) {
                viewModel.refrescar(movilidad)
            }
            if (usuarioId != null) {
                viewModel.cargarMiEstado(usuarioId)
            }
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
            InicialesAvatar(nombre = usuario?.estudianteNombre ?: "Estudiante", tamano = 42.dp)
            Spacer(Modifier.size(12.dp))
            Column {
                Text("¡Hola!", color = TextSecondary, fontSize = 12.sp)
                Text(usuario?.estudianteNombre ?: "Estudiante", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.size(12.dp))

        // Mapa: punto del estudiante + punto del bus (conductor).
        val ubic = viewModel.ubicacion
        val puntoEstudiante = if (usuario?.lat != null && usuario.lng != null) {
            LatLng(usuario.lat, usuario.lng)
        } else {
            null
        }
        // Si no hay punto del estudiante, centra en una zona por defecto.
        val centroInicial = puntoEstudiante ?: LatLng(-12.020556, -76.957333)
        val camara = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(centroInicial, 14f)
        }
        // Cuando ya conocemos el punto del estudiante, centra el mapa ahí.
        LaunchedEffect(puntoEstudiante) {
            if (puntoEstudiante != null) {
                camara.position = CameraPosition.fromLatLngZoom(puntoEstudiante, 15f)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camara
            ) {
                // Punto del estudiante (verde). Se oculta cuando ya fue recogido.
                if (puntoEstudiante != null && viewModel.miEstado != "ENTREGADO") {
                    Marker(
                        state = MarkerState(position = puntoEstudiante),
                        title = usuario?.estudianteNombre ?: "Estudiante",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                }
                // Punto del bus / conductor (naranja)
                if (ubic != null) {
                    Marker(
                        state = MarkerState(position = LatLng(ubic.lat, ubic.lng)),
                        title = "Bus ${ubic.movilidad}",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
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

                val estado = viewModel.miEstado
                val textoEstado: String
                val colorEstado: Color
                if (estado == "ENTREGADO") {
                    textoEstado = "Recogido"
                    colorEstado = SuccessGreen
                } else if (estado == "CANCELADO") {
                    textoEstado = "Recojo cancelado"
                    colorEstado = DangerRed
                } else {
                    textoEstado = "Aún no recogido"
                    colorEstado = WarningAmber
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(colorEstado)
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(textoEstado, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }
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
