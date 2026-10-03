package com.example.myapplication.ui.screens.ubicacion

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
import androidx.compose.material.icons.filled.QrCodeScanner
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
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.example.myapplication.utils.Sesion
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.Alumno
import com.example.myapplication.domain.EstadoEntrega
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.myapplication.ui.screens.alumnos.AlumnosViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun RutaActivaScreen(
    modifier: Modifier = Modifier,
    viewModel: AlumnosViewModel = viewModel(),
    ubicacionVM: UbicacionViewModel = viewModel()
) {
    // ---- Envío del GPS del conductor (seguimiento) ----
    val context = LocalContext.current
    val movilidad = Sesion.usuario?.movilidad ?: "Movilidad N°04"

    // Lanzador del escáner QR (ZXing). Abre la cámara y, al leer el QR, devuelve su
    // contenido: el id del estudiante. Con ese id lo marcamos como entregado.
    val escanerQR = rememberLauncherForActivityResult(ScanContract()) { resultado ->
        val idEscaneado = resultado.contents
        if (idEscaneado != null) {
            val encontrado = viewModel.marcarEntregadoPorId(idEscaneado)
            val mensaje = if (encontrado) "Estudiante marcado como entregado"
                          else "No se encontró un estudiante con ese código"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    // Configuración del escáner: solo códigos QR, con mensaje y sonido al leer.
    fun abrirEscanerQR() {
        val opciones = ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            .setPrompt("Escanea el código QR del estudiante")
            .setBeepEnabled(true)
            .setOrientationLocked(false)
        escanerQR.launch(opciones)
    }

    // Punto de referencia: 12°01'14.0"S 76°57'26.4"W
    val puntoReferencia = LatLng(-12.020556, -76.957333)
    // Posición del conductor: empieza en el punto de referencia y se actualiza con el GPS.
    var posConductor by remember { mutableStateOf(puntoReferencia) }

    // Estado del marcador del conductor. Se recuerda (evita recrearlo en cada recomposición)
    // y se re-sincroniza cada vez que cambia posConductor para que el marcador siga al GPS.
    val estadoMarcadorConductor = rememberMarkerState(position = posConductor)
    LaunchedEffect(posConductor) { estadoMarcadorConductor.position = posConductor }

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
                    // getCurrentLocation pide una posición FRESCA (no la guardada en caché),
                    // así toma la ubicación actual del emulador/dispositivo.
                    fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                        .addOnSuccessListener { loc ->
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
    // Cámara del mapa: arranca centrada en la zona de los paraderos.
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(puntoReferencia, 14f)
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
                        state = rememberMarkerState(key = alumno.id, position = LatLng(alumno.lat, alumno.lng)),
                        title = alumno.nombre,
                        snippet = alumno.paradero,
                        icon = BitmapDescriptorFactory.defaultMarker(colorMarcador)
                    )
                }
            }

            // 2) El punto actual del conductor (azul).
            Marker(
                state = estadoMarcadorConductor,
                title = "Conductor",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
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
            Column(Modifier.padding(14.dp)) {
                if (proxima == null) {
                    Text("Ruta completada 🎉", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text("Todos los estudiantes fueron atendidos", color = TextSecondary, fontSize = 13.sp)
                } else {
                    Text("Próxima entrega", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(proxima.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text(proxima.direccion, color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))

                    // Entregar (escaneando su QR) o cancelar. Al cancelar se marca como
                    // cancelado y automáticamente pasa al siguiente estudiante.
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Abre la cámara para leer el QR del estudiante y marcarlo como entregado.
                        Button(
                            onClick = { abrirEscanerQR() },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(6.dp))
                            Text("Escanear QR", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                viewModel.cancelar(proxima, "Cancelado por el conductor")
                                Toast.makeText(context, "Recojo cancelado. Siguiente estudiante.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                        ) {
                            Text("Cancelar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Devuelve el estudiante PENDIENTE más cercano a una posición.
 * Se saltan los ya ENTREGADOS y los CANCELADOS (ya fueron atendidos).
 */
private fun estudianteMasCercano(alumnos: List<Alumno>, desde: LatLng): Alumno? {
    var masCercano: Alumno? = null
    var menorDistancia = Double.MAX_VALUE

    for (alumno in alumnos) {
        val yaAtendido = alumno.estado == EstadoEntrega.ENTREGADO || alumno.estado == EstadoEntrega.CANCELADO
        if (!yaAtendido && alumno.lat != null && alumno.lng != null) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RutaActivaPreview() {
    MyApplicationTheme {
        RutaActivaScreen()
    }
}
