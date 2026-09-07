package com.example.myapplication.conductor.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Cabecera con título centrado, logo a la izquierda y acción opcional a la derecha. */
@Composable
fun EncabezadoConductor(
    titulo: String,
    modifier: Modifier = Modifier,
    accionIcono: ImageVector? = null,
    accionDescripcion: String? = null,
    onAccion: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text("IE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Text(
            text = titulo,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (accionIcono != null && onAccion != null) {
            IconButton(onClick = onAccion) {
                Icon(accionIcono, contentDescription = accionDescripcion)
            }
        } else {
            Spacer(Modifier.size(36.dp))
        }
    }
}

/** Fila con las "fichas" de los hijos/alumnos asociados (azul y rosa en el mockup). */
@Composable
fun ChipsAlumnos(
    nombres: List<Pair<String, Color>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        nombres.forEach { (nombre, color) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(nombre, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}
