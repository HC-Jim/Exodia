package com.example.myapplication.apoderado.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.myapplication.apoderado.model.Comunicado
import com.example.myapplication.apoderado.model.MockApoderado
import com.example.myapplication.conductor.ui.componentes.EncabezadoConductor
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun ComunicadosScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(titulo = "Comunicados")

        Text(
            "Esta semana",
            modifier = Modifier.padding(start = 20.dp, top = 4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Spacer(Modifier.size(12.dp))
        TiraSemanal()
        Spacer(Modifier.size(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(MockApoderado.comunicados, key = { it.id }) { com ->
                TarjetaComunicado(com)
            }
        }
    }
}

@Composable
private fun TiraSemanal() {
    // (letra del día, número)
    val dias = listOf("L" to 23, "M" to 24, "X" to 25, "J" to 26, "V" to 27, "S" to 28, "D" to 29)
    var seleccion by remember { mutableIntStateOf(25) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dias.forEach { (letra, numero) ->
            val activo = numero == seleccion
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (activo) IndigoPrimary else Color.Transparent)
                    .clickable { seleccion = numero }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(letra, color = if (activo) Color.White else TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.size(6.dp))
                Text(
                    numero.toString(),
                    color = if (activo) Color.White else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun TarjetaComunicado(comunicado: Comunicado) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(comunicado.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Campaign, contentDescription = null, tint = comunicado.color)
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(comunicado.titulo, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
            Text(comunicado.detalle, color = TextSecondary, fontSize = 13.sp)
        }
        Text(comunicado.fecha, color = comunicado.color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ComunicadosPreview() {
    MyApplicationTheme {
        ComunicadosScreen()
    }
}
