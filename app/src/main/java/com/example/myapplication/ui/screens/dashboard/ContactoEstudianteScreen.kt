package com.example.myapplication.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.repositories.MockConductor
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.components.InicialesAvatar
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import com.example.myapplication.ui.theme.WarningAmber

@Composable
fun ContactoEstudianteScreen(
    onRetroceder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alumno = MockConductor.proximaEntrega
    var busqueda by remember { mutableStateOf("") }
    val checks = remember { mutableStateListOf(false, false, false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoConductor(
            titulo = "Contacto Estudiante",
            accionIcono = Icons.AutoMirrored.Filled.ArrowBack,
            accionDescripcion = "Volver",
            onAccion = onRetroceder
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Buscar estudiante por nombre") }
        )

        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                InicialesAvatar(nombre = alumno.nombre, tamano = 44.dp)
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(alumno.nombre, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(alumno.direccion, color = TextSecondary, fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SuccessGreen, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.LocalPhone, contentDescription = "Llamar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        Titulo("Avisos de llegada")
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvisoBoton("Ya casi aquí", Icons.Filled.NearMe, Modifier.weight(1f))
            AvisoBoton("Estoy en camino", Icons.AutoMirrored.Filled.DirectionsRun, Modifier.weight(1f))
        }

        Titulo("Opciones de incidencia")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Marcar como recogido (vía QR)", fontWeight = FontWeight.SemiBold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChipIncidencia("Ausente", Modifier.weight(1f))
                ChipIncidencia("No contactado", Modifier.weight(1f))
            }
        }

        Titulo("Detalles de la incidencia / observaciones")
        Column(Modifier.padding(horizontal = 16.dp)) {
            val opciones = listOf("Esperando 5 minutos", "Llamada sin respuesta", "Ubicación incorrecta")
            opciones.forEachIndexed { i, texto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { checks[i] = !checks[i] }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (checks[i]) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        contentDescription = null,
                        tint = if (checks[i]) IndigoPrimary else TextSecondary
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(texto, color = TextPrimary, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetroceder,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Text("Confirmar incidencia y volver a ruta", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(
        texto,
        modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 8.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = TextPrimary
    )
}

@Composable
private fun AvisoBoton(texto: String, icono: ImageVector, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = {}, modifier = modifier.height(48.dp), shape = RoundedCornerShape(12.dp)) {
        Icon(icono, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(6.dp))
        Text(texto, fontSize = 13.sp, color = TextPrimary)
    }
}

@Composable
private fun ChipIncidencia(texto: String, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = {}, modifier = modifier.height(44.dp), shape = RoundedCornerShape(12.dp)) {
        Text(texto, fontSize = 13.sp, color = TextPrimary)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ContactoEstudiantePreview() {
    MyApplicationTheme {
        ContactoEstudianteScreen(onRetroceder = {})
    }
}
