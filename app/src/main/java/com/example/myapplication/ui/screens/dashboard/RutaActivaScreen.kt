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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.example.myapplication.data.repositories.MockConductor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.entities.Alumno
import com.example.myapplication.domain.entities.EstadoEntrega
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.example.myapplication.ui.theme.WarningAmber

@Composable
fun RutaActivaScreen(
    onVerLista: () -> Unit,
    onEscanearQR: () -> Unit,
    modifier: Modifier = Modifier,
    onContactar: () -> Unit = {},
    viewModel: AlumnosViewModel = viewModel(),
    ubicacionVM: UbicacionViewModel = viewModel()
) {
    // ---- Envío del GPS del conductor (seguimiento) ----
    val context = LocalContext.current
    val movilidad = MockConductor.perfil.movilidad

    // Punto de referencia: 12°01'14.0"S 76°57'26.4"W
    val puntoReferencia = LatLng(-12.020556, -76.957333)
    // Posición del conductor: empieza en el punto de referencia y se actualiza con el GPS.
    var posConductor by remember { mutableStateOf(puntoReferencia) }

    // Próximo estudiante a recoger: el pendiente más cercano al conductor.
    val proxima = estudianteMasCercano(viewModel.alumnos, posConductor)

    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    // Pide el permiso de ubicación al abrir la pantalla.
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> tienePermiso = concedido }

    LaunchedEffect(Unit) {
        if (!tienePermiso) pedirPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Con permiso, envía la posición al servidor cada 5 segundos.
    LaunchedEffect(tienePermiso) {
        if (tienePermiso) {
            val fused = LocationServices.getFusedLocationProviderClient(context)
            while (true) {
                try {
                    fused.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            posConductor = LatLng(loc.latitude, loc.longitude)
                            ubicacionVM.enviar(movilidad, loc.latitude, loc.longitude)
                        }
                    }
                } catch (e: SecurityException) {
                    // sin permiso: no se envía
                }
                kotlinx.coroutines.delay(5000)
            }
        }
    }
    // Punto fijo del colegio (destino por defecto).
    val colegio = LatLng(-12.018000, -76.954000)

    // Cámara del mapa: arranca centrada en la zona de los paraderos.
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(puntoReferencia, 14f)
    }

    // Cuando cambia la posición del conductor o el próximo estudiante, recalcula la ruta por calles.
    LaunchedEffect(posConductor, proxima?.id) {
        if (proxima != null && proxima.lat != null && proxima.lng != null) {
            viewModel.calcularRuta(posConductor, LatLng(proxima.lat, proxima.lng))
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

      // El mapa ocupa el espacio disponible; la tarjeta va debajo.
      Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camara
        ) {
            // 1) Un punto por cada estudiante que tenga coordenadas.
            for (alumno in viewModel.alumnos) {
                if (alumno.lat != null && alumno.lng != null) {
                    val esProximo = proxima != null && alumno.id == proxima.id

                    val colorMarcador: Float
                    if (alumno.estado == EstadoEntrega.ENTREGADO) {
                        colorMarcador = BitmapDescriptorFactory.HUE_GREEN     // ya entregado
                    } else if (esProximo) {
                        colorMarcador = BitmapDescriptorFactory.HUE_ORANGE    // próximo a recoger
                    } else {
                        colorMarcador = BitmapDescriptorFactory.HUE_RED       // pendiente
                    }

                    Marker(
                        state = MarkerState(position = LatLng(alumno.lat, alumno.lng)),
                        title = alumno.nombre,
                        snippet = alumno.paradero,
                        icon = BitmapDescriptorFactory.defaultMarker(colorMarcador)
                    )
                }
            }

            // 2) El punto del conductor (azul).
            Marker(
                state = MarkerState(position = posConductor),
                title = "Conductor",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )

            // 3) El punto del colegio (violeta, fijo por defecto).
            Marker(
                state = MarkerState(position = colegio),
                title = "Colegio",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            )

            // 4) Ruta por las CALLES del conductor hacia el próximo estudiante.
            if (viewModel.ruta.size >= 2) {
                Polyline(
                    points = viewModel.ruta,
                    color = IndigoPrimary,
                    width = 8f
                )
            }
        }

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
                    "Ver Lista (${viewModel.alumnos.size})",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        // Botón de recentrar (dentro del mapa, sobre el conductor)
        Surface(
            onClick = { camara.position = CameraPosition.fromLatLngZoom(posConductor, 15f) },
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Centrar mapa", tint = IndigoPrimary)
            }
        }
      } // cierra el Box del mapa

        // Tarjeta de próxima entrega (DEBAJO del mapa, para no taparlo)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                if (proxima == null) {
                    Text("Ruta completada 🎉", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    Text("No quedan entregas pendientes", color = TextSecondary, fontSize = 14.sp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Place, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Próxima Entrega", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(proxima.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    Text(proxima.direccion, color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(14.dp))
                    // Marca la entrega: guarda en SQLite, registra historial e intenta enviar a la API.
                    Button(
                        onClick = { viewModel.marcarEntregado(proxima) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(8.dp))
                        Text("Marcar entregado", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onEscanearQR,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(8.dp))
                        Text("Escanear QR", fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onVerLista,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Listado", fontSize = 13.sp) }
                    androidx.compose.material3.OutlinedButton(
                        onClick = onContactar,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Contactar", fontSize = 13.sp) }
                }
            }
        }
    }
}

/**
 * Devuelve el estudiante PENDIENTE (no entregado) más cercano a una posición.
 * Compara la distancia (al cuadrado, que basta para saber cuál es el menor).
 */
private fun estudianteMasCercano(alumnos: List<Alumno>, desde: LatLng): Alumno? {
    var masCercano: Alumno? = null
    var menorDistancia = Double.MAX_VALUE

    for (alumno in alumnos) {
        if (alumno.estado != EstadoEntrega.ENTREGADO && alumno.lat != null && alumno.lng != null) {
            val difLat = alumno.lat - desde.latitude
            val difLng = alumno.lng - desde.longitude
            val distancia = difLat * difLat + difLng * difLng
            if (distancia < menorDistancia) {
                menorDistancia = distancia
                masCercano = alumno
            }
        }
    }

    return masCercano
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
